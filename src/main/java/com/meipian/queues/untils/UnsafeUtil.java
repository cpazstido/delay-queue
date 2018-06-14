package com.meipian.queues.untils;

import sun.misc.Unsafe;

import java.lang.reflect.Field;

public class UnsafeUtil {

    public static Unsafe getUnsafe() throws Exception{
        // 通过反射获取rt.jar下的Unsafe类
        Field theUnsafeInstance = Unsafe.class.getDeclaredField("theUnsafe");
        theUnsafeInstance.setAccessible(true);
        // return (Unsafe) theUnsafeInstance.get(null);是等价的
        return (Unsafe) theUnsafeInstance.get(Unsafe.class);
    }
}
