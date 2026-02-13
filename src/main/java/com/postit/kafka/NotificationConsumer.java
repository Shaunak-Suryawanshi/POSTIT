package com.postit.kafka;

import com.postit.event.PostCommentedEvent;
import com.postit.event.PostLikedEvent;
import com.postit.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
@ConditionalOnProperty(name = "kafka.enabled", havingValue = "true", matchIfMissing = true)
public class NotificationConsumer {

    private final NotificationService notificationService;

    @KafkaListener(topics = "postit", groupId = "postit-group")
    public void handlePostLiked(
            @Payload PostLikedEvent event,
            Acknowledgment acknowledgment,
            @Header(KafkaHeaders.RECEIVED_TOPIC) String topic,
            @Header("kafka_receivedPartitionId") int partition,
            @Header(KafkaHeaders.OFFSET) long offset) {
        try {
            log.info("Received POST_LIKED event for post: {} [topic: {}, partition: {}, offset: {}]",
                event.getPostId(), topic, partition, offset);
            notificationService.createLikeNotification(event);
            acknowledgment.acknowledge();
            log.info("Successfully processed and acknowledged POST_LIKED event");
        } catch (Exception e) {
            log.error("Error processing POST_LIKED event", e);
            // Don't acknowledge on error - will be retried
        }
    }

    @KafkaListener(topics = "postit", groupId = "postit-group")
    public void handlePostCommented(
            @Payload PostCommentedEvent event,
            Acknowledgment acknowledgment,
            @Header(KafkaHeaders.RECEIVED_TOPIC) String topic,
            @Header("kafka_receivedPartitionId") int partition,
            @Header(KafkaHeaders.OFFSET) long offset) {
        try {
            log.info("Received POST_COMMENTED event for post: {} [topic: {}, partition: {}, offset: {}]",
                event.getPostId(), topic, partition, offset);
            notificationService.createCommentNotification(event);
            acknowledgment.acknowledge();
            log.info("Successfully processed and acknowledged POST_COMMENTED event");
        } catch (Exception e) {
            log.error("Error processing POST_COMMENTED event", e);
            // Don't acknowledge on error - will be retried
        }
    }
}

