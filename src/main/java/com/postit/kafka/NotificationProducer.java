package com.postit.kafka;

import com.postit.event.PostCommentedEvent;
import com.postit.event.PostLikedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class NotificationProducer {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    private static final String POSTIT_TOPIC = "postit";

    public void publishPostLiked(PostLikedEvent event) {
        log.info("Publishing POST_LIKED event for post: {}", event.getPostId());
        kafkaTemplate.send(POSTIT_TOPIC, "post-liked:" + event.getPostId(), event);
    }

    public void publishPostCommented(PostCommentedEvent event) {
        log.info("Publishing POST_COMMENTED event for post: {}", event.getPostId());
        kafkaTemplate.send(POSTIT_TOPIC, "post-commented:" + event.getPostId(), event);
    }
}

