package com.meipian.redis.test;

public class InterruptTest {
    public static void main(String[] args) throws Exception {
        Thread t = new Thread(){
            @Override
            public void run() {
                try {
                    synchronized (this){
                        this.wait();
//                        Thread.sleep(60000);//必须在同步块中使用
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    System.out.println("1thread is interrupted!");
                }

                System.out.println("i'm go on run!");
                try {
                    synchronized (this){
                        this.wait();//必须在同步块中使用
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    System.out.println("2thread is interrupted!");
                }

            }
        };
        t.start();
        Thread.sleep(1000);
        t.interrupt();

        Thread.sleep(1000);
        t.interrupt();

        t.join();
    }
}
