package tut.dushyant.demo.cafeapp.order.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

import lombok.Data;

@ConfigurationProperties(prefix = "kafka")
@Data
public class KafkaConfig {

    private String bootstrapServers;
    private String securityProtocol;
    private Consumer consumer;
    private Producer producer;

    @Data
    public static class Consumer {
        private String groupId;
        private String autoOffsetReset;
        private String keyDeserializer;
        private String valueDeserializer;
    }

    @Data
    public static class Producer {
        private String keySerializer;
        private String valueSerializer;
    }
    
}
