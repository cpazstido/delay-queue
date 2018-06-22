package com.meipian.redis.test;

import org.junit.*;
import org.junit.Test;

import java.util.Date;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.LockSupport;
import java.util.concurrent.locks.ReentrantLock;

public class ConditionTest {

    public static void main(String[] args) throws Exception {
        final ReentrantLock lock=new ReentrantLock();
        final Condition condition =lock.newCondition();
        Thread thread = new Thread(){
            @Override
            public void run() {
                lock.lock();//请求锁
                try{
                    System.out.println(Thread.currentThread().getName()+"==》进入等待"+new Date());
                    long re = condition.awaitNanos(99999999999999999l);//设置当前线程进入等待
                    System.out.println(re);
                    System.out.println(Thread.currentThread().getName()+"==》等待完成"+new Date());
                }catch (InterruptedException e) {
                    e.printStackTrace();
                }finally{
                    lock.unlock();//释放锁
                }
//                System.out.println(Thread.currentThread().getName()+"==》继续执行"+new Date());
            }
        };
        thread.start();

//        Thread.sleep(2000);

        Thread thread1 = new Thread(){
            @Override
            public void run() {
                lock.lock();//请求锁
                try{
                    System.out.println(Thread.currentThread().getName()+"=》进入"+new Date());
                    Thread.sleep(2000);//休息2秒
                    condition.signal();//随机唤醒等待队列中的一个线程
//                    System.out.println("signal");
//                    Thread.sleep(5000);
                    System.out.println(Thread.currentThread().getName()+"休息结束"+new Date());
                }catch (InterruptedException e) {
                    e.printStackTrace();
                }finally{
                    lock.unlock();//释放锁
                }
            }
        };
//        thread1.start();


    }

    @Test
    public void test1() throws Exception{
        final ReentrantLock lock=new ReentrantLock(true);
        final Condition condition =lock.newCondition();
        final Thread thread0 = new Thread(){
            @Override
            public void run() {
                System.out.println("thread0 enter ");
                lock.lock();//请求锁
//                LockSupport.park(lock);
                System.out.println("unparck");
                try{
//                    LockSupport.park(lock);
                    System.out.println("thread0");
//                    lock.lock();
//                    Thread.sleep(2000);//休息2秒
//                    Thread.currentThread().interrupt();
                    System.out.println("thread0 before wait");
//                    condition.await();//设置当前线程进入等待
                    System.out.println("thread0 after wait");
                }catch (Exception e) {
                    e.printStackTrace();
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
                    Thread.sleep(2000);//休息2秒
                    System.out.println(Thread.currentThread().getName()+"休息结束");
                }catch (Exception e) {
                    e.printStackTrace();
                }finally{
                    lock.unlock();//释放锁
                    System.out.println(Thread.currentThread().getName()+"==》继续执行");
                }

            }
        };

        Thread thread2 = new Thread(){
            @Override
            public void run() {
                System.out.println("thread2 enter ");
                lock.lock();//请求锁
                System.out.println("thread2");
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

        Thread thread3 = new Thread(){
            @Override
            public void run() {
                System.out.println("thread3 enter ");
                lock.lock();//请求锁
                System.out.println("thread3");
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


//        thread1.start();
        thread0.start();
        Thread.sleep(1000);
        thread0.interrupt();
        System.out.println(thread0.isInterrupted());
//        thread1.start();
//        thread2.start();
//        thread3.start();
    }

    @Test
    public void test2() throws Exception{
        final ReentrantLock lock=new ReentrantLock();
        final Condition condition =lock.newCondition();
        Thread thread = new Thread(){
            @Override
            public void run() {
                lock.lock();//请求锁
                try{
                    System.out.println(Thread.currentThread().getName()+"==》进入等待");
                    condition.await();//设置当前线程进入等待
                    System.out.println(Thread.currentThread().getName()+"==》等待完成");
                }catch (InterruptedException e) {
                    e.printStackTrace();
                }finally{
                    lock.unlock();//释放锁
                }
                System.out.println(Thread.currentThread().getName()+"==》继续执行");
            }
        };
        thread.start();
        Thread thread1 = new Thread(){
            @Override
            public void run() {
                lock.lock();//请求锁
                try{
                    System.out.println(Thread.currentThread().getName()+"=》进入");
                    Thread.sleep(2000);//休息2秒
                    condition.signal();//随机唤醒等待队列中的一个线程
                    System.out.println(Thread.currentThread().getName()+"休息结束");
                }catch (InterruptedException e) {
                    e.printStackTrace();
                }finally{
                    lock.unlock();//释放锁
                }
            }
        };
        thread1.start();
    }
}
