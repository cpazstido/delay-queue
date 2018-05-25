package com.meipian.redis.test;

import com.meipian.queues.core.Message;
import com.meipian.queues.redis.DelayQueue;
import com.meipian.queues.redis.DelayQueueProcessListener;
import com.meipian.queues.redis.RedisDelayQueue;
import org.junit.Before;
import org.junit.Test;
import redis.clients.jedis.HostAndPort;
import redis.clients.jedis.JedisCluster;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

import java.util.HashSet;
import java.util.Set;

public class TestDelayQueue {

    JedisPool jedisPool = null;
    DelayQueue queue = null;

    @Before
    public void init() {
        String ip = "192.168.2.160";
        jedisPool = new JedisPool();
        queue = new DelayQueue("com.meipian", "delayqueue", jedisPool, 60 * 1000,
                new DelayQueueProcessListener() {
                    @Override
                    public void pushCallback(Message message) {

                    }

                    @Override
                    public void peekCallback(Message message) {
                        System.out.println("message----->" + message);
                        queue.ack(message.getId());//确认操作。将会删除消息
                    }

                    @Override
                    public void ackCallback(Message message) {
                    }
                });
        queue.listen();

    }

    @Test
    public void testCreate() throws InterruptedException {
        Message message = new Message();
        for (int i = 0; i < 10; i++) {
            message.setId(i + "");
            message.setPayload("test");
            message.setPriority(0);
            message.setTimeout(3000);
            queue.push(message);
        }
        // message = queue.peek();
        // queue.ack("1234");

    }
}
