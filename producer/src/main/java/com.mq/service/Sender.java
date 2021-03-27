package com.mq.service;

import com.mq.utils.MQUtils;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.MessageProperties;
import sun.rmi.runtime.Log;
import java.io.IOException;

public class Sender {
    protected static final String QUEUE_NAME = "rabbitmq";
    protected static String message = "一条消息....";
    protected static int k = 0;
    protected static double startTime = 0.0;

    // 4；通过 mq 连接对象创建 消息渠道对象
    static Channel channel = MQUtils.getChannel();
    static {
        try {
            channel.queueDeclare(QUEUE_NAME,true,false,false,null);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void sendMessage() throws Exception {
        for (int i = 0; i < 10; i++) {
            new Thread(new Runnable() {
                public void run() {
                    for (int j = 0; j < 10; j++) {
                        try {
                            if (channel != null) {
                                // 5：绑定 消息渠道对象 与 消息队列
                                //  相当于 jdbc 的 statement
                                //  参数1；交换机名称，如果直接发送信息到队列，则交换机名称为""
                                //  参数2；目标队列名称
                                //  参数3；设置消息的属性（设置过期时间：10）
                                //  参数4；消息的内容
                                channel.basicPublish("", QUEUE_NAME, MessageProperties.PERSISTENT_TEXT_PLAIN, message.getBytes());
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                        double l = (double) ((System.currentTimeMillis() - startTime) / 1000);
                        int t = ++k;
                        System.out.println(" Sender 发送: " + message + "耗时" + t/l);

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

            // 触发条件设置中断
            Thread.currentThread().interrupt();

        }
    }

//    public static void main(String[] args) throws Exception {
//        startTime = System.currentTimeMillis();
//        Sender.sendMessage();
//    }
}