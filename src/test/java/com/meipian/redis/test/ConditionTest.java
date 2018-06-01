package com.meipian.redis.test;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class ConditionTest {
    public static ReentrantLock lock=new ReentrantLock();
    public static Condition condition =lock.newCondition();
    public static void main(String[] args) {
        final Thread thread0 = new Thread(){
            @Override
            public void run() {
                System.out.println("thread0 enter ");
                lock.lock();//请求锁
                try{
                    System.out.println("thread0");
//                    Thread.sleep(100000);//休息2秒
                    Thread.currentThread().interrupt();
                    System.out.println("thread0 before wait");
//                    condition.await();//设置当前线程进入等待
                    System.out.println("thread0 after wait");
                }catch (Exception e) {
                    e.printStackTrace();
                    System.out.println("haha");
                }finally{
                    lock.unlock();//释放锁
                }
                System.out.println(Thread.currentThread().getName()+"==》继续执行");
            }
        };

        Thread thread1 = new Thread(){
            @Override
            public void run() {
                System.out.println("thread1 enter ");
                lock.lock();//请求锁
                System.out.println("thread1");
                try{
//                    condition.signal();//随机唤醒等待队列中的一个线程
                    Thread.sleep(1000);//休息2秒
                    System.out.println(Thread.currentThread().getName()+"休息结束");
                }catch (Exception e) {
                    e.printStackTrace();
                }finally{
                    lock.unlock();//释放锁
                }
            }
        };
        thread0.start();
        thread1.start();

    }
}
