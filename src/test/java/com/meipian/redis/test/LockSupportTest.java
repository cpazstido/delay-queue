package com.meipian.redis.test;

import com.sun.xml.internal.stream.util.ThreadLocalBufferAllocator;

import java.util.concurrent.locks.LockSupport;

public class LockSupportTest {
    public static void main(String[] args) throws Exception {
        Thread t = new Thread(){
            @Override
            public void run() {
                System.out.println("entry thread0");
                LockSupport.park();
                System.out.println("go on");
            }
        };

        t.start();

        Thread.sleep(2000);
        LockSupport.unpark(t);

        t.join();
    }
}
