package com.meipian.redis.test;

public class BinaryCode {
    public static void main(String[] args) {
        int a = 6;
        StringBuilder stringBuilder = new StringBuilder();
        for(int i=0;i<32;i++){
            int t = (a&(0x80000000>>>i))>>>(31-i);
            stringBuilder.append(t);
        }
        System.out.println(stringBuilder.toString());
        int b = 18;
        System.out.println(0x12>>>1);
        char ss = '好';
        System.out.println(ss);
    }
}
