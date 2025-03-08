package tut.dushyant.demo.cafeapp.order.config;

import java.util.List;

import org.springframework.boot.context.properties.ConfigurationProperties;

import lombok.Data;

@ConfigurationProperties(prefix = "kafka")
@Data
public class KafkaConfig {
    private List<String> bootstrapServers;
    private Producer producer;
    private boolean enabled = false;

    @Data
    public static class Producer {
        private String clientId;
        private String keySerializer;
        private String valueSerializer;
    }
}