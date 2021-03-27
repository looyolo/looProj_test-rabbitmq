package com.mq.utils;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import java.io.IOException;
import java.util.concurrent.TimeoutException;

public class MQUtils {
    // 1；创建连接工厂对象
    static ConnectionFactory factory = new ConnectionFactory();
    static Connection connection = null;
    static Channel channel = null;

    static {
        // 2：给连接工厂对象设定 mq 连接信息
        factory.setHost("127.0.0.1");
        factory.setPort(5672);
        factory.setUsername("guest");
        factory.setPassword("guest");

        try {
            // 3；通过工厂连接对象创建 mq 连接对象
            connection = factory.newConnection();
            channel = connection.createChannel();
        } catch (IOException | TimeoutException e) {
            e.printStackTrace();
        }
    }

    // 实现获取 mq 连接方法
    public static Channel getChannel() {
        return channel;
    }

//    public static void main(String[] args) {
//        System.out.println(getChannel());
//    }

}