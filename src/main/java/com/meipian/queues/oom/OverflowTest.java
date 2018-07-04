package com.meipian.queues.oom;

/**
 * 你能想到有什么办法，可以让一个程序的函数调用层次变的更深。比如，你在一个递归调用中，发生了stack的溢出，你可以做哪些方面的尝试，使系统尽量不溢出？阐述你的观点和原因。

 答：首先了解到线程在调用每个方法的时候，都会创建相应的栈，在退出方法的时候移出栈桢，并且栈是私用的，也需要占用空间，所以让一个程序的函数调用层次变的更深
 减少栈贞的空间很必要。或者增大线程的栈的大小。
 通过volatile增加调用层次深度。线程会对一个没有volatile的变量进行临时存储，这就导致线程栈的空间增大，如果对一个变量增加volatile修饰，可以适当增加深度，详情看实验：
 */
public class OverflowTest {
    private volatile int i=0;
    private volatile int b=0;
    private volatile int c=0;

//	private  int i=0;
//	private  int b=0;
//	private  int c=0;

    public static void main(String[] args) {
        OverflowTest o=new OverflowTest();
        try {
            o.deepTest();
        } catch (Throwable e) {
            System.out.println("over flow deep:"+o.i);
            e.printStackTrace();
        }
    }
    private void deepTest() {
        ++i;
        ++b;
        ++c;
        deepTest();
    }
}
