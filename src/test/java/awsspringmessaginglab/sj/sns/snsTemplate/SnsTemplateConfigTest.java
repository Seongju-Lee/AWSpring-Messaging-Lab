package awsspringmessaginglab.sj.sns.snsTemplate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.testcontainers.containers.localstack.LocalStackContainer.Service.SNS;

import awsspringmessaginglab.sj.IntegrationTest;
import io.awspring.cloud.sns.core.SnsTemplate;
import java.net.URI;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.sns.SnsClient;
import software.amazon.awssdk.services.sns.model.CreateTopicRequest;
import software.amazon.awssdk.services.sns.model.CreateTopicResponse;
import software.amazon.awssdk.services.sns.model.PublishRequest;
import software.amazon.awssdk.services.sns.model.PublishResponse;


public class SnsTemplateConfigTest extends IntegrationTest {

    private SnsTemplate snsTemplate;
    private SnsClient snsClient;

    private Region defaultRegion;
    private URI defaultEndpoint;


    @Nested
    class SnsClient_설정 {

        @BeforeEach
        void setup() {
            defaultRegion = Region.of(LOCAL_STACK_CONTAINER.getRegion());
            defaultEndpoint = LOCAL_STACK_CONTAINER.getEndpointOverride(SNS);
        }


        @Test
        void 기본값() {
            // given
            snsClient = SnsClient.builder()
                .endpointOverride(defaultEndpoint)
                .credentialsProvider(
                    StaticCredentialsProvider.create(AwsBasicCredentials.create("test", "test")))
                .build();
            final CreateTopicResponse createTopicResponse = snsClient.createTopic(CreateTopicRequest.builder()
                .name("test-topic")
                .build());
            final String topicArn = createTopicResponse.topicArn();
            snsTemplate = new SnsTemplate(snsClient);

            // when
            final PublishResponse publishResponse = snsClient.publish(PublishRequest.builder()
                .topicArn(topicArn)
                .message("message content")
                .subject("subject")
                .build());

            // then
            final Region region = snsClient.serviceClientConfiguration().region();
            assertThat(region).isEqualTo(defaultRegion);
            assertThat(publishResponse).isNotNull();
            assertThat(publishResponse.messageId()).isNotEmpty();
            assertThat(publishResponse.sdkHttpResponse().isSuccessful()).isTrue();
        }


        @Test
        void Region_변경() {
            // given
            snsClient = SnsClient.builder()
                .endpointOverride(defaultEndpoint)
                .region(Region.AP_NORTHEAST_2)
                .credentialsProvider(
                    StaticCredentialsProvider.create(AwsBasicCredentials.create("test", "test")))
                .build();
            final CreateTopicResponse createTopicResponse = snsClient.createTopic(CreateTopicRequest.builder()
                .name("test-topic")
                .build());
            final String topicArn = createTopicResponse.topicArn();
            snsTemplate = new SnsTemplate(snsClient);

            // when
            final PublishResponse publishResponse = snsClient.publish(PublishRequest.builder()
                .topicArn(topicArn)
                .message("message content")
                .subject("subject")
                .build());

            // then
            Region region = snsClient.serviceClientConfiguration().region();
            assertThat(region).isEqualTo(Region.AP_NORTHEAST_2);
            assertThat(publishResponse).isNotNull();
            assertThat(publishResponse.messageId()).isNotEmpty();
            assertThat(publishResponse.sdkHttpResponse().isSuccessful()).isTrue();
        }
    }
}
