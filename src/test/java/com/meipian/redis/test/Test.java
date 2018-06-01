package com.meipian.redis.test;

import sun.misc.Lock;
import sun.misc.Unsafe;
import sun.reflect.Reflection;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

public class Test {
    private static ThreadLocal threadLocal = new ThreadLocal();
    static class Validation {
        private int MAX_SIZE = 10;
        public boolean sizeValidate() {
            return 20 < MAX_SIZE;
        }
    }

    public static void main(String[] args) throws Exception {
        // 通过反射得到theUnsafe对应的Field对象
        Field field = Unsafe.class.getDeclaredField("theUnsafe");
        // 设置该Field为可访问
        field.setAccessible(true);
        // 通过Field得到该Field对应的具体对象，传入null是因为该Field为static的
        Unsafe unsafe = (Unsafe) field.get(null);
        Validation v = new Validation();
        System.out.println(v.sizeValidate());   // false
        Field f = v.getClass().getDeclaredField("MAX_SIZE");
        unsafe.putInt(v, unsafe.objectFieldOffset(f), 100); // memory corruption
        System.out.println(v.sizeValidate()); // true

        Thread thread = new Thread();
        threadLocal.set("ssss");
        threadLocal.get();
        thread.start();

        thread.join();

        AtomicInteger atomicInteger = new AtomicInteger();
        AtomicBoolean atomicBoolean = new AtomicBoolean();
        System.out.println("");
    }

    public void aa() throws Exception{
        throw new RuntimeException("run");
    }
}
