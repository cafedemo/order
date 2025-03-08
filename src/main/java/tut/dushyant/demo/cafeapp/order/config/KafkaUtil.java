package tut.dushyant.demo.cafeapp.order.config;

import java.util.Properties;

import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.AdminClientConfig;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import lombok.extern.slf4j.Slf4j;

@Configuration
@Slf4j
public class KafkaUtil {

    private KafkaConfig kafkaConfig;

    public KafkaUtil(KafkaConfig kafkaConfig) {
        this.kafkaConfig = kafkaConfig;
    }

    @Bean
    AdminClient createAdminClient() {
        Properties props = new Properties();
        props.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaConfig.getBootstrapServers());
        props.put("auto.create.topics.enable", "true");
        return AdminClient.create(props);
    }

    @Bean
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
