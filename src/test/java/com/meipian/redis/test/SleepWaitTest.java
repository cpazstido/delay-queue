package com.meipian.redis.test;

public class SleepWaitTest {
    public static void main(String[] args) {
        Thread thread0 = new Thread(){
            @Override
            public void run() {
                synchronized (this){
                    try {
                        System.out.println(this);
//                        Thread.sleep(10000);
                        wait();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        };

        Thread thread1 = new Thread(){
            @Override
            public void run() {
                synchronized (this){
                    System.out.println(this);
                }
            }
        };

        thread0.start();
        thread1.start();
    }
}
