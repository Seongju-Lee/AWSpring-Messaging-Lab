package awsspringmessaginglab.sj.sns.core;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

import awsspringmessaginglab.sj.sns.Matchers;
import io.awspring.cloud.sns.core.DefaultTopicArnResolver;
import io.awspring.cloud.sns.core.SnsTemplate;
import java.util.function.Consumer;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import software.amazon.awssdk.services.sns.SnsClient;
import software.amazon.awssdk.services.sns.model.CreateTopicRequest;
import software.amazon.awssdk.services.sns.model.CreateTopicResponse;
import software.amazon.awssdk.services.sns.model.PublishRequest;

@Slf4j
public class SnsTemplateTest {

    private static final String TOPIC_ARN = "arn:aws:sns:ap-northeast-2:123456789012:test-sns";
    private final SnsClient snsClient = mock(SnsClient.class);

    private SnsTemplate snsTemplate;

    @BeforeEach
    void init() {
        MappingJackson2MessageConverter converter = new MappingJackson2MessageConverter();
        converter.setSerializedPayloadClass(String.class);
        snsTemplate = new SnsTemplate(snsClient, new DefaultTopicArnResolver(snsClient), converter);

        when(snsClient.createTopic(CreateTopicRequest.builder().name("topic name").build()))
            .thenReturn(CreateTopicResponse.builder().topicArn(TOPIC_ARN).build());
    }


    @Nested
    class sendNotification_테스트 {


        @Test
        void sendTextMessage() {
            log.info("SnsTemplate.sendNotification() Test");
            snsTemplate.sendNotification("topic name", "message content", "subject");

            verify(snsClient).publish(Matchers.requestMatches(r -> {
                assertThat(r.topicArn()).isEqualTo(TOPIC_ARN);
                assertThat(r.message()).isEqualTo("message content");
                assertThat(r.subject()).isEqualTo("subject");
                assertThat(r.messageAttributes().keySet()).contains(MessageHeaders.ID);
                assertThat(r.messageAttributes().keySet()).contains(MessageHeaders.TIMESTAMP);
            }));
        }

    }


}
