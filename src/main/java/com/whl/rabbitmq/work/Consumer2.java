package com.whl.rabbitmq.work;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.QueueingConsumer;
import com.whl.rabbitmq.util.ConnectionUtil;

/**
 * 
 * description: 消息消费者，由服务器异步push到消费者
 * 
 * @author whling
 * @date 2017年7月4日 下午3:16:56
 *
 */
public class Consumer2 {
	private final static String QUEUE_NAME = "test_queue_work";

	public static void main(String[] argv) throws Exception {

        // 获取到连接以及mq通道
        Connection connection = ConnectionUtil.getConnection();
        Channel channel = connection.createChannel();

        // 声明队列
        channel.queueDeclare(QUEUE_NAME, false, false, false, null);

        // 同一时刻服务器只会发一条消息给消费者,所以能者多劳
        channel.basicQos(1);

        // 定义队列的消费者
        QueueingConsumer consumer = new QueueingConsumer(channel);
        // 监听队列，手动返回完成状态
        channel.basicConsume(QUEUE_NAME, false, consumer);

        // 获取消息
        while (true) {
            QueueingConsumer.Delivery delivery = consumer.nextDelivery();
            String message = new String(delivery.getBody());
            System.out.println(" [x] Received '" + message + "'");
            // 休眠1秒
            Thread.sleep(100);

            channel.basicAck(delivery.getEnvelope().getDeliveryTag(), false);
        }
    }
}