package tut.dushyant.demo.cafeapp.order.util;


import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.admin.AdminClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.KafkaAdmin;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Set;
import java.util.concurrent.ExecutionException;

/**
 * This class acts as health check with kubernetes readiness probe.
 * The check is done in environment variable app.healthcheck is true
 * and the kafka is up and running.
 */
@Slf4j
@RestController
@RequestMapping("/check")
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
        //get list of topics from Kadka.
        //if the list is not empty, return true
        //else return false
        if (!checkFlag) {
            return false;
        }

        try (AdminClient client = AdminClient.create(kafkaAdmin.getConfigurationProperties())) {
            client.listTopics().names().get();
            return true;
        } catch (Exception e) {
            log.error("Error while checking kafka health", e);
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
