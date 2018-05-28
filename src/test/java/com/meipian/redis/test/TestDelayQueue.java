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

import java.io.IOException;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;

public class TestDelayQueue {

    JedisPool jedisPool = null;
    DelayQueue queue = null;
    Thread demonThread = null;

    @Before
    public void init() {
        jedisPool = new JedisPool();
        queue = new DelayQueue("com.meipian", "delayqueue", jedisPool, 60 * 1000,
                new DelayQueueProcessListener() {
                    @Override
                    public void pushCallback(Message message) {
                        //System.out.println("入队:"+message.toString());
                    }

                    @Override
                    public void peekCallback(Message message) {
                        System.out.println(Thread.currentThread().getName()+" 消费：" + message.toString());
//                        queue.ack(message.getId());//确认操作。将会删除消息
                    }

                    @Override
                    public void ackCallback(Message message) {
                        System.out.println("确认："+message.toString());
                    }
                });
        demonThread =new Thread(new Runnable() {
            @Override
            public void run() {
                queue.listen();
            }
        });
        demonThread.start();
    }

    @Test
    public void testCreate() throws InterruptedException {
        Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
            @Override
            public void run() {
                queue.clear();
                System.out.println("清理资源");
            }
        }));

        queue.clear();

        MessageThread messageThread = new MessageThread("线程1",queue);
        new Thread(messageThread).start();

        Thread.sleep(4000);

        MessageThread messageThread1 = new MessageThread("线程2",queue);
        new Thread(messageThread1).start();

        // message = queue.peek();
        // queue.ack("1234");
        try {
            System.in.read();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private class MessageThread implements Runnable{
        private DelayQueue queue;
        private String name;

        public MessageThread(){
        }

        public MessageThread(String name,DelayQueue queue){
            this.queue = queue;
            this.name = name;
        }

        @Override
        public void run() {
            System.out.println(name+"开始");
            Message message = new Message();
            Random random = new Random(99);
            for (int i = 0; i < 100; i++) {
                message.setId(name+"_"+ i + "");
                message.setPayload(name+"test");
                message.setPriority(0);
                int r = random.nextInt(99);
                if(r <= 0){
                    continue;
                }
                message.setTimeout(r);
                queue.push(message);
            }
        }
    }
}
