package com.tianji.learning.mq;

import com.tianji.api.dto.trade.OrderBasicDTO;
import com.tianji.common.constants.MqConstants;
import com.tianji.common.utils.CollUtils;
import com.tianji.learning.service.ILearningLessonService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.ExchangeTypes;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class LessonChangeListener {

    private final ILearningLessonService lessonService;

    /**
     * 监听 RabbitMQ 消息队列中的消息，并根据指定的交换机、队列和路由键进行绑定。
     *
     * 该方法使用 @RabbitListener 注解监听消息，具体配置如下：
     * - 队列名称为 "learning.lesson.pay.queue"，且设置为持久化（durable = true）。
     * - 交换机名称由常量 MqConstants.Exchange.ORDER_EXCHANGE 指定，类型为 TOPIC。
     * - 路由键由常量 MqConstants.Key.ORDER_PAY_KEY 指定。
     *
     * 此注解会自动将队列与交换机通过指定的路由键进行绑定，用于处理订单支付相关的消息。
     */
    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(value = "learning.lesson.pay.queue", durable = "true"),
            exchange = @Exchange(name = MqConstants.Exchange.ORDER_EXCHANGE, type = ExchangeTypes.TOPIC),
            key = MqConstants.Key.ORDER_PAY_KEY
    ))
    public void listenLessonPay(OrderBasicDTO order){

        // 1.健壮性处理
        if (order == null || order.getOrderId() == null || CollUtils.isEmpty(order.getCourseIds())){
            // 异常处理
            log.error("课程支付，异常消息，数据为空");
            return;
        }
        // 2.处理逻辑(添加课程)
        log.debug("处理课程支付消息：{}", order);
        lessonService.addUserLessons(order.getUserId(), order.getCourseIds());
    }

    /**
     * 监听订单退款消息，删除用户对应的课程学习记录
     *
     * 绑定队列：learning.lesson.refund.queue
     * 交换机：MqConstants.Exchange.ORDER_EXCHANGE（TOPIC 类型）
     * 路由键：MqConstants.Key.ORDER_REFUND_KEY
     */
    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(value = "learning.lesson.refund.queue", durable = "true"),
            exchange = @Exchange(name = MqConstants.Exchange.ORDER_EXCHANGE, type = ExchangeTypes.TOPIC),
            key = MqConstants.Key.ORDER_REFUND_KEY
    ))
    public void listenLessonRefund(OrderBasicDTO order) {
        // 1. 健壮性校验
        if (order == null || order.getOrderId() == null || CollUtils.isEmpty(order.getCourseIds())) {
            log.error("课程退款消息异常：订单ID或课程ID为空，orderId={}, userId={}",
                    order != null ? order.getOrderId() : null,
                    order != null ? order.getUserId() : null);
            return;
        }

        Long userId = order.getUserId();
        List<Long> courseIds = order.getCourseIds();

        log.debug("处理课程退款消息：orderId={}, userId={}, courseIds={}",
                order.getOrderId(), userId, courseIds);

        // 2. 调用服务层删除对应课程学习记录
        lessonService.removeUserLessons(userId, courseIds);
    }
}
