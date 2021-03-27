package com.mq.service;

import com.mq.utils.MQUtils;
import com.rabbitmq.client.*;
import sun.rmi.runtime.Log;
import java.io.IOException;

public class Reciever {
    protected static final String QUEUE_NAME = "rabbit";
    protected static String message = "";
    protected static int k = 0;
    protected static double startTime = 0.0;

    // 4；通过 mq 连接对象创建 消息渠道对象
    //  注意：以下等价于 static Channel channel = MQUtils.getChannel().queueDeclare(QUEUE_NAME,true,false,false,null);
    static Channel channel = MQUtils.getChannel();
    static {
        try {
            channel.queueDeclare(QUEUE_NAME, true,false,false,null);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    //  注意：以下不会等价于 static Channel channel = MQUtils.getChannel().queueDeclare(QUEUE_NAME,true,false,false,null);
//    static Channel channel = MQUtils.getChannel();
//    try {
//        channel.queueDeclare(QUEUE_NAME, true,false,false,null);
//    } catch (IOException e) {
//       e.printStackTrace();
//    }

    public static void recieveMessage() throws InterruptedException {
        for (int i = 0; i < 10; i++) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    for (int j = 0; j < 10; j++) {
                        // 查看 消息队列 是否已创建
                        // 值得注意的是，队列只会在它不存在的时候创建，多次声明并不会重复创建。
                        // 信息的内容是字节数组，也就意味着你可以传递任何数据。
                        System.out.println(Consumer.class.hashCode()
                                + " [*] Waiting for messages. To exit press CTRL+C");

                        if (channel != null) {
                            // 5：通过 消息渠道对象 创建 消费者对象 接收消息
                            Consumer consumer = new DefaultConsumer(channel) {
                                @Override
                                public void handleDelivery(String s, Envelope envelope, AMQP.BasicProperties basicProperties,
                                                           byte[] bytes) throws IOException {
                                    message = bytes.toString();
                                    System.out.println("reciever 接收：" + message);

                                }
                            };

                            //  注意：Consumer 是 I 类 API ，不带构造函数，要 new 对象得使用 C 类 API 来实现
//                            Consumer consumer = new Consumer(channel) {
//                                @Override
//                                public void handleDelivery(String s, Envelope envelope, AMQP.BasicProperties basicProperties,
//                                                           byte[] bytes) throws IOException {
//                                }
//                            };

                            // 5.1：再把 消息渠道对象 和 消息队列、消费者对象 一起绑定
                            try {
                                //  5.1.1：设定 最大消息接收数量
                                int prefetchCount = 10;
                                channel.basicQos(prefetchCount);
                                //  5.1.2：设定 自动确认消息被成功消费 为 false
                                boolean ACK = false;
                                channel.basicConsume(QUEUE_NAME, ACK, consumer);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }

                            double l = (double) ((System.currentTimeMillis() - startTime) / 1000);
                            int t = ++k;
                            System.out.println(" Reciever 接收: " + message + " ，耗时: " + t/l);

                        }
                    }

                    while (!Thread.currentThread().isInterrupted()) {
                        try {
                            Log.getLog("tag","线程运行中: " + Thread.currentThread().getId(),0);
                            // 每执行一次暂停 40 毫秒
                            // 当 sleep 方法抛出 InterruptedException 中断状态也会被清掉
                            Thread.sleep(40);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                            // 如果抛出异常则再次设置中断请求
                            Thread.currentThread().interrupt();
                        }
                    }
                }
            }).start();
        }

        // 触发条件设置中断
        Thread.currentThread().interrupt();

    }

//    public static void main(String[] args) throws InterruptedException {
//        startTime = System.currentTimeMillis();
//        Reciever.recieveMessage();
//    }
}