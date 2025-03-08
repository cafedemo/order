package tut.dushyant.demo.cafeapp.order.util;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.TopicDescription;
import org.apache.kafka.common.KafkaFuture;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.KafkaAdmin;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ExecutionException;

/**
 * This class acts as health check with kubernetes readiness probe.
 * The check is done in environment variable app.healthcheck is true
 * and the kafka is up and running.
 */
@Slf4j
@RestController
@RequestMapping("/check")
@ConditionalOnProperty(prefix = "app", name = "healthcheck", havingValue = "true")
public class HealthCheckKafka {

    private final boolean checkFlag;
    private final KafkaAdmin kafkaAdmin;

    public HealthCheckKafka(
            @Value("${app.healthcheck:false}") String checkFlag,
            KafkaAdmin kafkaAdmin) {
        this.kafkaAdmin = kafkaAdmin;
        if (StringUtils.hasLength(checkFlag) && Boolean.parseBoolean(checkFlag))
            this.checkFlag = true;
        else
            this.checkFlag = false;
    }

    private boolean isKafkaUp() {
        //describe topic from kafka
        //if unable to describe topic return false
        //else return true
        if (!checkFlag) {
            return false;
        }

        try (AdminClient client = AdminClient.create(kafkaAdmin.getConfigurationProperties())) {
            Map<String, KafkaFuture<TopicDescription>> topicMap = client.describeTopics(List.of("order-topic")).topicNameValues();
            return Objects.equals(topicMap.get("order-topic").get().name(), "order-topic");
        } catch (InterruptedException | ExecutionException ex) {
            log.error("Error while checking kafka health", ex);
            return false;
        }
    }

    @GetMapping(path = "", produces = "text/plain")
    public ResponseEntity<String> checkKafka() {
        if (isKafkaUp()) {
            return ResponseEntity.ok("UP");
        } else {
            return ResponseEntity.status(500).body("DOWN");
        }
    }

}
