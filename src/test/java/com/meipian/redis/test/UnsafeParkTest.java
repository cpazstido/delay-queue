package com.meipian.redis.test;

import com.meipian.queues.untils.UnsafeUtil;
import org.junit.*;
import org.junit.Test;
import sun.misc.Unsafe;

public class UnsafeParkTest {
    @org.junit.Test
    public void unpack() throws Exception {
        Unsafe unsafe = UnsafeUtil.getUnsafe();

        Thread currThread = Thread.currentThread();

//        unsafe.unpark(currThread);
//        unsafe.unpark(currThread);
        unsafe.unpark(currThread);

        unsafe.park(false, 0);
        unsafe.park(false, 0);

        System.out.println("SUCCESS!!!");
    }

    @Test
    public void test1() throws Exception {
        Unsafe unsafe = UnsafeUtil.getUnsafe();

        final Thread currThread = Thread.currentThread();
        new Thread() {
            @Override
            public void run() {
                try {
                    Thread.sleep(3000);
                    currThread.interrupt();
                    //unsafe.unpark(currThread);
                } catch (Exception e) {
                }
            }
        }.start();
        unsafe.park(false, 0);
        System.out.println("SUCCESS!!!");
    }

    @Test
    public void test2() throws Exception{
        Unsafe unsafe = UnsafeUtil.getUnsafe();
        //相对时间后面的参数单位是纳秒
        unsafe.park(false, 3000000000l);
        System.out.println("SUCCESS!!!");
    }

    @Test
    public void test3() throws Exception{
        Unsafe unsafe = UnsafeUtil.getUnsafe();
        long time = System.currentTimeMillis()+3000;
        //绝对时间后面的参数单位是毫秒
        unsafe.park(true, time);
        System.out.println("SUCCESS!!!");
    }
}
