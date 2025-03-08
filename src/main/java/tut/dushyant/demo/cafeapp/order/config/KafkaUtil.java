package tut.dushyant.demo.cafeapp.order.config;

import java.util.Properties;

import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.AdminClientConfig;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;

import lombok.extern.slf4j.Slf4j;
import tut.dushyant.demo.cafeapp.order.util.OrderCafeException;

@Configuration
@Slf4j
public class KafkaUtil {

    private KafkaConfig kafkaConfig;

    public KafkaUtil(KafkaConfig kafkaConfig) {
        this.kafkaConfig = kafkaConfig;
    }

    @Bean
    @ConditionalOnProperty(prefix = "kafka", name = "enabled", havingValue = "true")
    KafkaProperties autoConfigure() {
        if (kafkaConfig.isEnabled()) {
            try {
                log.info("Kafka is enabled");
                KafkaProperties kafkaProperties = new KafkaProperties();
                kafkaProperties.setBootstrapServers(kafkaConfig.getBootstrapServers());
                kafkaProperties.setClientId(kafkaConfig.getProducer().getClientId());
                kafkaProperties.getProducer().setKeySerializer(Class.forName(kafkaConfig.getProducer().getKeySerializer()));
                kafkaProperties.getProducer().setValueSerializer(Class.forName(kafkaConfig.getProducer().getValueSerializer()));
                return kafkaProperties;
            } catch (ClassNotFoundException e) {
                throw new OrderCafeException("Class not found", e);
            }
        } else {
            log.info("Kafka is disabled");
            return null;
        }
    }

    @Bean
    @ConditionalOnProperty(prefix = "kafka", name = "enabled", havingValue = "true")
    KafkaTemplate<String, String> createKafkaTemplate(KafkaProperties kafkaProperties) {
        if (kafkaProperties != null) {
            return new KafkaTemplate<String, String>(new DefaultKafkaProducerFactory<>(kafkaProperties.buildProducerProperties()));
        } else {
            throw new OrderCafeException("Kafka is disabled. Still I here!!. Why?");
        }
    }

    @Bean
    @ConditionalOnProperty(prefix = "kafka", name = "enabled", havingValue = "true")
    AdminClient createAdminClient() {
        if (kafkaConfig.isEnabled()) {
            Properties props = new Properties();
            props.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaConfig.getBootstrapServers());
            props.put("auto.create.topics.enable", "true");
            return AdminClient.create(props);
        } else {
            throw new OrderCafeException("Kafka is disabled. Still I here!!. Why?");
        }
        
    }

    @Bean
    @ConditionalOnProperty(prefix = "kafka", name = "enabled", havingValue = "true")
    KafkaProducer<String, String> createKafkaProducer() {
        Properties props = new Properties();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaConfig.getBootstrapServers());
        props.put(ProducerConfig.CLIENT_ID_CONFIG, kafkaConfig.getProducer().getClientId());
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, kafkaConfig.getProducer().getKeySerializer());
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, kafkaConfig.getProducer().getValueSerializer());
        props.put(ProducerConfig.ENABLE_IDEMPOTENCE_CONFIG, "false");
        props.put(ProducerConfig.ACKS_CONFIG, "1");
        props.put(ProducerConfig.MAX_IN_FLIGHT_REQUESTS_PER_CONNECTION, "5");
        return new KafkaProducer<>(props);
    }

}
