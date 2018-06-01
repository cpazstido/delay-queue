package com.meipian.redis.test;

import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.*;

public class BlockingQueueTest {
    /**
     * 实例化一个队列，队列中的容量为10
     */
    private static BlockingQueue<Integer> blockingQueue = new LinkedBlockingQueue<>(10);
    public static void main(String[] args) {
        ScheduledExecutorService product = Executors.newScheduledThreadPool(1);

        product.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                Random random = new Random();
                int value = random.nextInt(101);
                try{
                    blockingQueue.offer(value);  //offer()方法就是网队列的尾部设置值
                }catch(Exception ex){
                    ex.printStackTrace();
                }
            }
        }, 0, 200, TimeUnit.MILLISECONDS);  //每100毫秒执行线程

        new Thread(new Runnable() {
            @Override
            public void run() {
                while(true){
                    try {
                        Thread.sleep(2000);
                        System.out.println("开始取值");
                        List<Integer> list = new LinkedList<>();
                        blockingQueue.drainTo(list);  //drainTo()将队列中的值全部从队列中移除，并赋值给对应集合
                        for(Integer i:list){
                            System.out.println(i);
                        }
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
    }
}
