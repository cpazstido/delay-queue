package com.meipian.redis.test.list;

import org.junit.Test;

import java.util.ArrayList;

public class ArrayListTest {

    @Test
    public void test(){
        ArrayList arrayList = new ArrayList();
        for(int i=0;i<12;i++){
            arrayList.add(i);
        }
        System.out.println("");
    }
}
