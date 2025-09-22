package org.kosa.listener;

import lombok.extern.slf4j.Slf4j;
import org.kosa.config.RabbitMQConfig;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class NotificationListener {

    @RabbitListener(queues = RabbitMQConfig.NOTIFICATION_QUEUE)
    public void handleOrderEvent(Object event) {
        log.info("알림 서비스: 이벤트 수신 - {}", event);
        
        // 이메일/SNS 발송 로직
        try {
            Thread.sleep(500);
            log.info("고객 알림 발송 완료: {}", event.getClass().getSimpleName());
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.error("알림 발송 중 오류 발생, ", e);
        }
    }

}
