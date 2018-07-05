package com.meipian.queues.oom;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class PermOOM {
    public static void main(String[] args) {
        List<String> list = new ArrayList<String>();
        while (true) {
            list.add(UUID.randomUUID().toString().intern());
        }
    }
}
