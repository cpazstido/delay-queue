package com.meipian.redis.test;

public class MultiThreadCheck {
    public static void main(String[] args) {
        Thread threadA = new Thread(new ThreadA());
        threadA.start();
        Thread threadB = new Thread(new ThreadB());
        threadB.start();
        Thread threadC = new Thread(new ThreadC());
        threadC.start();
    }

    private static class ThreadA implements Runnable{
        @Override
        public void run() {
            while(true){
                try {
                    Thread.sleep(200);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private static class ThreadB implements Runnable{
        @Override
        public void run() {
            while (true){
                while(true){

                }
            }
        }
    }

    private static class ThreadC implements Runnable{
        @Override
        public void run() {
            while (true){
                while(true){
                    try {
                        Thread.sleep(200);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }
}
