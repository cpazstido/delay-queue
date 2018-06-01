package com.meipian.redis.test;

import sun.misc.Unsafe;

import java.lang.reflect.Field;

public class UnsafeTest {

    static class Validation {
        private int MAX_SIZE = 10;
        public boolean sizeValidate() {
            return 20 < MAX_SIZE;
        }
    }
    public static void main(String[] args) throws Exception{
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
    }
}
