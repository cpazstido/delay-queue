package com.meipian.redis.test;

public class Person {
    private String name;
    private int age;
    private boolean sex;

    public void sayHi(){
        System.out.println("Say hi from ITer_ZC");
    }

    public static void main(String[] args){
        Person p = new Person();
        p.sayHi();

        try {
            Thread.sleep(5000000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
