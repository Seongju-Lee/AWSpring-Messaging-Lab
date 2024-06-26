package awsspringmessaginglab.sj;

import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.testcontainers.containers.localstack.LocalStackContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@Testcontainers
public abstract class IntegrationTest {

    @Container
    protected static final LocalStackContainer LOCAL_STACK_CONTAINER = new LocalStackContainer(
        DockerImageName.parse("localstack/localstack:3.2.0"))
        .withServices(LocalStackContainer.Service.SQS, LocalStackContainer.Service.SNS)
        .withEnv("AWS_ACCESS_KEY_ID", "test")
        .withEnv("AWS_SECRET_ACCESS_KEY", "test");
}
