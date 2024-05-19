package awsspringmessaginglab.sj.support;

import static org.mockito.ArgumentMatchers.argThat;

import java.util.function.Consumer;
import software.amazon.awssdk.services.sns.model.PublishRequest;

public class Matchers {
    public static PublishRequest requestMatches(final Consumer<PublishRequest> consumer) {
        return argThat(it -> {
            consumer.accept(it);
            return true;
        });
    }
}
