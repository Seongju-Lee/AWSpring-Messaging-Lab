package awsspringmessaginglab.sj.sns.core;

import static org.assertj.core.api.Assertions.assertThat;
import static org.testcontainers.containers.localstack.LocalStackContainer.Service.SNS;

import awsspringmessaginglab.sj.IntegrationTest;
import io.awspring.cloud.sns.core.SnsTemplate;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.sns.SnsClient;
import software.amazon.awssdk.services.sns.model.CreateTopicRequest;
import software.amazon.awssdk.services.sns.model.CreateTopicResponse;
import software.amazon.awssdk.services.sns.model.PublishRequest;
import software.amazon.awssdk.services.sns.model.PublishResponse;


public class SnsTemplateIntegrationTest extends IntegrationTest {

    private static SnsTemplate snsTemplate;
    private static SnsClient snsClient;

    @BeforeEach
    void setUp() {
        snsClient = SnsClient.builder()
            .endpointOverride(LOCAL_STACK_CONTAINER.getEndpointOverride(SNS))
            .region(Region.of(LOCAL_STACK_CONTAINER.getRegion()))
            .credentialsProvider(
                StaticCredentialsProvider.create(AwsBasicCredentials.create("noop", "noop")))
            .build();
        snsTemplate = new SnsTemplate(snsClient);
    }

    @AfterEach
    public void purgeQueue() {
        LOCAL_STACK_CONTAINER.stop();
    }

    @Test
    void sendTextMessage() {
        // given
        final CreateTopicResponse createTopicResponse = snsClient.createTopic(CreateTopicRequest.builder()
            .name("test-topic")
            .build());
        final String topicArn = createTopicResponse.topicArn();

        // when
        snsTemplate.sendNotification(topicArn, "message content", "subject");

        // then
        final PublishResponse publishResponse = snsClient.publish(PublishRequest.builder()
            .topicArn(topicArn)
            .message("message content")
            .subject("subject")
            .build());
        assertThat(publishResponse).isNotNull();
    }
}
