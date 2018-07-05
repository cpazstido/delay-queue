package com.meipian.redis.test;

public class ToStringTest {
    static class WilltoStringInvoked {
        private volatile  int value = 0;
        public WilltoStringInvoked(){
            System.out.println("");
            int a=0;
            int b = a;
            System.out.println("");
        }
        private int setValue() {
            if (value == 0) {
                synchronized (this) {
                    if (value == 0) {
                        value = 1;
                    }
                }
            }
            return value;
        }

        public int getValue() {
            return value;
        }

        @Override
        public String toString() {
            System.out.println("tostring");
            return "This value is: " + setValue();
        }
    }

    public static void main(String[] args) {
        WilltoStringInvoked will = new WilltoStringInvoked();

        // #breakpoint1
        System.out.println("If breakpoint here value will be 1");

        /**
         * If we set an breakpoint before this method the output will
         * be 1, otherwise the output will be 0
         */
        System.out.println(will.getValue());

        // #breakpoint2
        System.out.println("If breakpoint here value will be 0");
    }
}
