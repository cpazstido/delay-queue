package com.meipian.redis.test;

import java.text.DateFormat;
import java.util.*;
import java.util.concurrent.DelayQueue;
import java.util.concurrent.Delayed;
import java.util.concurrent.TimeUnit;

enum Times {
    SUBMIT_TIME(10), SUMBMIT_LIMIT(2), MAX_RAND_TIME(15);
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
        DelayQueue<Student> queue = new DelayQueue<>();
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
        while (true) {
            Student s = queue.take(); // 必要时进行阻塞等待
            if (s.getName().equals("submit")) {
                System.out.println("时间已到，全部交卷！");
                // 利用Java8 Stream使尚未交卷学生交卷
                List<Student> students = new ArrayList<>();
//
                Iterator<Student> it = queue.iterator();
                while(it.hasNext()){
                    Student student = it.next();
                    student.submit();
                    System.out.println(student.toString());
                }
                System.exit(0);
            }
            System.out.println(s);
        }
    }
}

class Student implements Delayed {
    private String name;
    private long delay; // 考试花费时间，单位为毫秒
    private long expire; // 交卷时间，单位为毫秒

    // 此构造可随机生成考试花费时间
    public Student(String name) {
        this.name = name;
        this.delay = TimeUnit.MILLISECONDS.convert(getRandomSeconds(), TimeUnit.SECONDS); // 随机生成考试花费时间
        this.expire = System.currentTimeMillis() + this.delay;
    }

    // 此构造可指定考试花费时间
    public Student(String name, long delay, TimeUnit unit) {
        this.name = name;
        this.delay = TimeUnit.MILLISECONDS.convert(delay, unit);
        this.expire = System.currentTimeMillis() + this.delay;
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
        return "学生姓名：" + this.name + ",考试用时：" + TimeUnit.SECONDS.convert(delay, TimeUnit.MILLISECONDS) + ",交卷时间："
                + DateFormat.getDateTimeInstance().format(new Date(this.expire));
    }
}