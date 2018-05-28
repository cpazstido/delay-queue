package com.meipian.queues.redis;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.meipian.queues.core.Message;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisCluster;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.Tuple;
import redis.clients.jedis.params.sortedset.ZAddParams;

import java.io.IOException;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.LockSupport;
import java.util.concurrent.locks.ReentrantLock;

public class DelayQueue implements com.meipian.queues.core.DelayQueue {
    private transient final ReentrantLock lock = new ReentrantLock();

    private final Condition available = lock.newCondition();

    private JedisPool jedisPool;

    private long MAX_TIMEOUT = 525600000; // 最大超时时间不能超过一年

    private ObjectMapper om;

    private int unackTime = 60 * 1000;

    private String queueName;

    private String redisKeyPrefix;

    private String messageStoreKey;

    private String realQueueName;

    private DelayQueueProcessListener delayQueueProcessListener;

    private volatile boolean isEmpty = true;

    public DelayQueue(String redisKeyPrefix, String queueName, JedisPool jedisPool, int unackTime,
                           DelayQueueProcessListener delayQueueProcessListener) {
        om = new ObjectMapper();
        om.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        om.configure(DeserializationFeature.FAIL_ON_IGNORED_PROPERTIES, false);
        om.configure(DeserializationFeature.FAIL_ON_NULL_FOR_PRIMITIVES, false);
        om.setSerializationInclusion(Include.NON_NULL);
        om.setSerializationInclusion(Include.NON_EMPTY);
        om.disable(SerializationFeature.INDENT_OUTPUT);
        this.redisKeyPrefix = redisKeyPrefix;
        this.messageStoreKey = redisKeyPrefix + ".MESSAGE." + queueName;
        this.unackTime = unackTime;
        this.jedisPool = jedisPool;
        realQueueName = redisKeyPrefix + ".QUEUE." + queueName;
        this.delayQueueProcessListener = delayQueueProcessListener;
    }

    public String getQueueName() {
        return queueName;
    }

    private void returnToPool(Jedis jedis) {
        if (jedis != null) {
            jedis.close();
        }
    }

    @Override
    public boolean push(Message message) {
        if (message.getTimeout() > MAX_TIMEOUT) {
            throw new RuntimeException("Maximum delay time should not be exceed one year");
        }
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            String json = om.writeValueAsString(message);
            jedis.hset(messageStoreKey, message.getId(), json);
            double priority = message.getPriority() / 100;
            double score = Long.valueOf(System.currentTimeMillis() + message.getTimeout()*1000).doubleValue() + priority;
            jedis.zadd(realQueueName, score, message.getId());
            delayQueueProcessListener.pushCallback(message);
            isEmpty = false;
            return true;
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        } finally {
            returnToPool(jedis);
        }
        return false;

    }

    public void listen() {
        while (true) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            String id = peekId();
            if (id == null) {
                continue;
            }
            Jedis jedis = null;
            try {
                jedis = jedisPool.getResource();
                String json = jedis.hget(messageStoreKey, id);
                Message message = om.readValue(json, Message.class);
                if (message == null) {
                    continue;
                }
                long delay = message.getCreateTime() + message.getTimeout()*1000 - System.currentTimeMillis();

                if (delay <= 0) {
                    delayQueueProcessListener.peekCallback(message);
                }else {
                    push(message);
                }
//                else {
//                    LockSupport.parkNanos(this, TimeUnit.NANOSECONDS.convert(delay, TimeUnit.MILLISECONDS));
//                    delayQueueProcessListener.peekCallback(message);
//                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                returnToPool(jedis);
            }

        }
    }

    @Override
    public boolean ack(String messageId) {
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            String unackQueueName = getUnackQueueName(queueName);
            jedis.zrem(unackQueueName, messageId);
            Long removed = jedis.zrem(realQueueName, messageId);
            Long msgRemoved = jedis.hdel(messageStoreKey, messageId);
            if (removed > 0 && msgRemoved > 0) {
                return true;
            }
        } catch (Exception e){
            return false;
        } finally {
            returnToPool(jedis);
        }

        return false;

    }

    @Override
    public boolean setUnackTimeout(String messageId, long timeout) {
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            double unackScore = Long.valueOf(System.currentTimeMillis() + timeout).doubleValue();
            String unackQueueName = getUnackQueueName(queueName);
            Double score = jedis.zscore(unackQueueName, messageId);
            if (score != null) {
                jedis.zadd(unackQueueName, unackScore, messageId);
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        } finally {
            returnToPool(jedis);
        }
        return false;

    }

    @Override
    public boolean setTimeout(String messageId, long timeout) {
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            String json = jedis.hget(messageStoreKey, messageId);
            if (json == null) {
                return false;
            }
            Message message = om.readValue(json, Message.class);
            message.setTimeout(timeout);
            Double score = jedis.zscore(realQueueName, messageId);
            if (score != null) {
                double priorityd = message.getPriority() / 100;
                double newScore = Long.valueOf(System.currentTimeMillis() + timeout).doubleValue() + priorityd;
                ZAddParams params = ZAddParams.zAddParams().xx();
                long added = jedis.zadd(realQueueName, newScore, messageId, params);
                if (added == 1) {
                    json = om.writeValueAsString(message);
                    jedis.hset(messageStoreKey, message.getId(), json);
                    return true;
                }
                return false;
            }
            return false;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        } finally {
            returnToPool(jedis);
        }
    }

    @Override
    public Message get(String messageId) {
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            String json = jedis.hget(messageStoreKey, messageId);
            if (json == null) {
                return null;
            }
            Message msg;
            msg = om.readValue(json, Message.class);
            return msg;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        } finally {
            returnToPool(jedis);
        }

    }

    @Override
    public long size() {
        long num = 0;
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            num =  jedis.zcard(realQueueName);
            return num;
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        } finally {
            returnToPool(jedis);
        }
    }

    @Override
    public void clear() {
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            String unackShard = getUnackQueueName(queueName);
            jedis.del(realQueueName);
            jedis.del(unackShard);
            jedis.del(messageStoreKey);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            returnToPool(jedis);
        }

    }

    private String peekId() {
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            if (!isEmpty) {
                lock.lockInterruptibly();
                double max = Long.valueOf(System.currentTimeMillis() + MAX_TIMEOUT).doubleValue();
                Set<String> scanned = jedis.zrangeByScore(realQueueName, 0, max, 0, 1);
                if (scanned.size() > 0) {
                    String messageId = scanned.toArray()[0].toString();
                    jedis.zrem(realQueueName, messageId);
                    setUnackTimeout(messageId, unackTime);
                    if (size() == 0) {
                        isEmpty = true;
                    }
                    available.signal();
                    lock.unlock();
                    return messageId;
                }
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
            available.signal();
            lock.unlock();
        } finally {
            returnToPool(jedis);
        }
        return null;
    }

    public void processUnacks() {
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            long queueDepth = size();
            int batchSize = 1_000;
            String unackQueueName = getUnackQueueName(queueName);
            double now = Long.valueOf(System.currentTimeMillis()).doubleValue();
            Set<Tuple> unacks = jedis.zrangeByScoreWithScores(unackQueueName, 0, now, 0, batchSize);
            for (Tuple unack : unacks) {
                double score = unack.getScore();
                String member = unack.getElement();
                String payload = jedis.hget(messageStoreKey, member);
                if (payload == null) {
                    jedis.zrem(unackQueueName, member);
                    continue;
                }
                jedis.zadd(realQueueName, score, member);
                jedis.zrem(unackQueueName, member);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            returnToPool(jedis);
        }
    }

    private String getUnackQueueName(String queueName) {
        return redisKeyPrefix + ".UNACK." + queueName;
    }

    @Override
    public String getName() {
        return this.realQueueName;
    }

    @Override
    public int getUnackTime() {

        return this.unackTime;
    }
}
