package com.meipian.redis.test.rabbitmq;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import java.util.HashMap;
import java.util.Map;

public class Send {
    private final static String QUEUE_NAME = "queue1";

    public static void main(String[] argv) throws Exception {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("211.149.177.186");
        factory.setPort(5672);
        factory.setVirtualHost("/");
        factory.setUsername("sofmit");
        factory.setPassword("sofmit");
        Connection connection = factory.newConnection();
        Channel channel = connection.createChannel();
        int i = 1;
        while(true){

        channel.exchangeDeclare("some.exchange.name", "direct");
        Map<String, Object> args = new HashMap<String, Object>();
        args.put("x-dead-letter-exchange", "some.exchange.name");
        //消息过期后发送的路由key不能与发送方的路由key相同
        args.put("x-dead-letter-routing-key", "some-routing-key2");

        channel.queueDeclare("queue1", true, false, false, args);
        channel.queueBind("queue1","some.exchange.name","some-routing-key");
        String message = "[" + i + "]" + "Hello World!";
        AMQP.BasicProperties properties = new AMQP.BasicProperties.
                Builder().expiration("5000").build();
        channel.basicPublish("some.exchange.name", "some-routing-key", properties, message.getBytes("UTF-8"));
        System.out.println("Sent '" + message + "'");
        Thread.sleep(1000);
            i++;
        }

//        channel.close();
//        connection.close();
    }
}
