package awsspringmessaginglab.sj.sns.snsTemplate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import awsspringmessaginglab.sj.support.Matchers;
import io.awspring.cloud.sns.core.SnsTemplate;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.messaging.MessageHeaders;
import software.amazon.awssdk.services.sns.SnsClient;
import software.amazon.awssdk.services.sns.model.CreateTopicRequest;
import software.amazon.awssdk.services.sns.model.CreateTopicResponse;

@Slf4j
public class SnsTemplateTest {

    private static final String TOPIC_ARN = "arn:aws:sns:ap-northeast-2:123456789012:test-sns";
    private final SnsClient snsClient = mock(SnsClient.class);

    private SnsTemplate snsTemplate;

    @BeforeEach
    void init() {
//        MappingJackson2MessageConverter converter = new MappingJackson2MessageConverter();
//        converter.setSerializedPayloadClass(String.class);
        snsTemplate = new SnsTemplate(snsClient);

        when(snsClient.createTopic(CreateTopicRequest.builder().name("topic name").build()))
            .thenReturn(CreateTopicResponse.builder().topicArn(TOPIC_ARN).build());
    }


    @Nested
    class sendNotification_테스트 {


        @Test
        void sendTextMessage() throws InterruptedException {
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
