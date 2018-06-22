package com.meipian.redis.test;

import com.sun.xml.internal.stream.util.ThreadLocalBufferAllocator;

import java.util.concurrent.locks.LockSupport;
import java.util.concurrent.locks.ReentrantLock;

public class LockSupportTest {
    public static void main(String[] args) throws Exception {
        final ReentrantLock lock = new ReentrantLock();
        Thread t = new Thread(){
            @Override
            public void run() {
                System.out.println("entry thread0");
                lock.lock();
                try {
                    System.out.println("thread0 run");
                    Thread.sleep(30000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
//                LockSupport.park();
                System.out.println("thread0 go on");
            }
        };

        Thread t1 = new Thread(){
            @Override
            public void run() {
                System.out.println("entry thread1");
//                lock.lock();
                System.out.println("thread1 run");
                LockSupport.park();
                System.out.println("thread1 go on");
            }
        };

        t.start();
        Thread.sleep(1000);
        t1.start();

        Thread.sleep(1000);
        t1.interrupt();
        System.out.println(t1.isInterrupted());
//        LockSupport.unpark(t1);

//        t.join();
//        System.in.read();
    }
}
