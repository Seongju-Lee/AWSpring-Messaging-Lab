package awsspringmessaginglab.sj.sns.snsTemplate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import awsspringmessaginglab.sj.support.Matchers;
import awsspringmessaginglab.sj.support.PersonFixture;
import awsspringmessaginglab.sj.support.PersonFixture.Person;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.awspring.cloud.sns.core.SnsTemplate;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.messaging.MessageHeaders;
import software.amazon.awssdk.services.sns.SnsClient;
import software.amazon.awssdk.services.sns.model.CreateTopicRequest;
import software.amazon.awssdk.services.sns.model.CreateTopicResponse;

@Slf4j
public class StandardSnsTemplateTest {

    private static final String TOPIC_ARN = "arn:aws:sns:ap-northeast-2:123456789012:test-topic";

    private final SnsClient snsClient = mock(SnsClient.class);
    private ObjectMapper objectMapper;
    private SnsTemplate snsTemplate;

    @BeforeEach
    void init() {
        snsTemplate = new SnsTemplate(snsClient);
        objectMapper = new ObjectMapper();
        when(snsClient.createTopic(CreateTopicRequest.builder().name("test-topic").build()))
            .thenReturn(CreateTopicResponse.builder().topicArn(TOPIC_ARN).build());
    }


    @Nested
    @DisplayName("sendNotification(String destinationName, Object message, @Nullable String subject)")
    class sendNotification_1 {

        @Test
        void text_전송() throws InterruptedException {
            snsTemplate.sendNotification("test-topic", "message content", "subject");

            verify(snsClient).publish(Matchers.requestMatches(r -> {
                assertThat(r.topicArn()).isEqualTo(TOPIC_ARN);
                assertThat(r.message()).isEqualTo("message content");
                assertThat(r.subject()).isEqualTo("subject");
                assertThat(r.messageAttributes().keySet()).contains(MessageHeaders.ID);
                assertThat(r.messageAttributes().keySet()).contains(MessageHeaders.TIMESTAMP);
            }));
        }


        @Test
        void 객체_그대로_전송() throws InterruptedException, JsonProcessingException {
            final Person person = PersonFixture.person("lee", 27);
            final String expectedMessage = objectMapper.writeValueAsString(person);
            snsTemplate.sendNotification("test-topic", person, "subject");

            verify(snsClient).publish(Matchers.requestMatches(r -> {
                // 의도와는 다르게 toString() 메서드가 적용된 모습이다.
                assertThat(r.message()).isNotEqualTo(expectedMessage);
                assertThat(r.message()).isEqualTo("Person[name=lee, age=27]");
                assertThat(expectedMessage).isEqualTo("{\"name\":\"lee\",\"age\":27}");
            }));
        }

        @Test
        void 객체_json형식_전송() throws InterruptedException, JsonProcessingException {
            final Person person = PersonFixture.person("lee", 27);
            final String expectedMessage = objectMapper.writeValueAsString(person);
            // Person 객체를 JSON 형식으로 변환하여 payload로써 전달한다.
            snsTemplate.sendNotification("test-topic", expectedMessage, "subject");

            verify(snsClient).publish(Matchers.requestMatches(r -> {
                assertThat(r.message()).isEqualTo(expectedMessage);
                assertThat(r.message()).isEqualTo("{\"name\":\"lee\",\"age\":27}");
                assertThat(expectedMessage).isEqualTo("{\"name\":\"lee\",\"age\":27}");
            }));
        }
    }
}
