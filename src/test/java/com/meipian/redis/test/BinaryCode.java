package com.meipian.redis.test;

public class BinaryCode {
    public static void main(String[] args) {
        int a = -99;
        StringBuilder stringBuilder = new StringBuilder();
        for(int i=0;i<32;i++){
            int t = (a&(0x80000000>>>i))>>>(31-i);
            stringBuilder.append(t);
        }
        System.out.println(stringBuilder.toString());

        String value=convert(-100.2f);
        System.out.println(value);
    }

    public static String convert(float num) {
        int intVal = Float.floatToIntBits(num);
        System.out.println(intVal);
        return intVal > 0 ? "0" + Integer.toBinaryString(intVal) : Integer
                .toBinaryString(intVal);
    }
}
