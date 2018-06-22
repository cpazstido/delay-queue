package com.meipian.redis.test;

import java.text.DateFormat;
import java.util.*;
import java.util.concurrent.DelayQueue;
import java.util.concurrent.Delayed;
import java.util.concurrent.TimeUnit;

enum Times {
    SUBMIT_TIME(13), SUMBMIT_LIMIT(8), MAX_RAND_TIME(15);
    private final int value;

    private Times(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}

public class TestDelayedQueue {
    public static void main(String[] args) throws InterruptedException {
        final DelayQueue<Student> queue = new DelayQueue<>();
        Thread thread = new Thread(){
            @Override
            public void run() {
                queue.add(new Student("范冰冰1"));
                queue.add(new Student("成  龙2"));
                queue.add(new Student("李一桐3"));
                queue.add(new Student("宋小宝4"));
                queue.add(new Student("吴  京5"));
                queue.add(new Student("绿巨人6"));
                queue.add(new Student("洪金宝7"));
                queue.add(new Student("李云龙8"));
                queue.add(new Student("钢铁侠9"));
                queue.add(new Student("刘德华10"));
                queue.add(new Student("戴安娜11"));
                queue.add(new Student("submit", Times.SUBMIT_TIME.getValue(), TimeUnit.SECONDS));
            }
        };
        thread.start();

        Thread.sleep(100);

        Thread consumerThrea = new Thread(){
            @Override
            public void run() {
                while (true) {
                    Student s = null; // 必要时进行阻塞等待
                    try {
                        s = queue.take();
                        if (s.getName().equals("submit")) {
                            System.out.println("时间已到，全部交卷！");
                            Iterator<Student> it = queue.iterator();
                            while(it.hasNext()){
                                Student student = it.next();
                                student.submit();
                                System.out.println(student.toString());
                            }
                            break;
                        }
                        System.out.println(s);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        };
        consumerThrea.start();

        Thread.sleep(9000);

        Thread thread1 = new Thread(){
            @Override
            public void run() {
                queue.add(new Student("XXXXX", 3, TimeUnit.SECONDS));
            }
        };
        thread1.start();
    }
}

class Student implements Delayed {
    private String name;
    private long start;//考试开始时间
    private long delay; // 考试花费时间，单位为毫秒
    private long expire; // 交卷时间，单位为毫秒

    // 此构造可随机生成考试花费时间
    public Student(String name) {
        this.name = name;
        this.delay = TimeUnit.MILLISECONDS.convert(getRandomSeconds(), TimeUnit.SECONDS); // 随机生成考试花费时间
        this.expire = System.currentTimeMillis() + this.delay;
        this.start = System.currentTimeMillis();
    }

    // 此构造可指定考试花费时间
    public Student(String name, long delay, TimeUnit unit) {
        this.name = name;
        this.delay = TimeUnit.MILLISECONDS.convert(delay, unit);
        this.expire = System.currentTimeMillis() + this.delay;
        this.start = System.currentTimeMillis();
    }

    public int getRandomSeconds() { // 获取随机花费时间
        return new Random().nextInt(Times.MAX_RAND_TIME.getValue() - Times.SUMBMIT_LIMIT.getValue())
                + Times.SUMBMIT_LIMIT.getValue();
    }

    public Student submit() { // 设置花费时间和交卷时间，考试时间结束强制交卷时调用此方法
        setDelay(Times.SUBMIT_TIME.getValue(), TimeUnit.SECONDS);
        setExpire(System.currentTimeMillis());
        return this;
    }

    public String getName() {
        return name;
    }

    public long getExpire() {
        return expire;
    }

    public void setDelay(long delay, TimeUnit unit) {
        this.delay = TimeUnit.MILLISECONDS.convert(delay, TimeUnit.SECONDS);
    }

    public void setExpire(long expire) {
        this.expire = expire;
    }

    @Override
    public int compareTo(Delayed o) { // 此方法的实现用于定义优先级
        long td = this.getDelay(TimeUnit.MILLISECONDS);
        long od = o.getDelay(TimeUnit.MILLISECONDS);
        return td > od ? 1 : td == od ? 0 : -1;
    }

    @Override
    public long getDelay(TimeUnit unit) { // 这里返回的是剩余延时，当延时为0时，此元素延时期满，可从take()取出
        return unit.convert(this.expire - System.currentTimeMillis(), TimeUnit.MILLISECONDS);
    }

    @Override
    public String toString() {
        return "学生姓名：" + this.name
                + ",考试用时：" + TimeUnit.SECONDS.convert(delay, TimeUnit.MILLISECONDS)
                + ",考试开始时间：" + DateFormat.getDateTimeInstance().format(new Date(this.start))
                + ",交卷时间：" + DateFormat.getDateTimeInstance().format(new Date(this.expire))
                + ",考试实际用时：" + (new Date().getTime() - this.start)/1000;
    }
}