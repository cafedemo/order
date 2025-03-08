package tut.dushyant.demo.cafeapp.order;

import tut.dushyant.demo.cafeapp.order.config.KafkaConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.data.mongo.MongoRepositoriesAutoConfiguration;
import org.springframework.boot.autoconfigure.kafka.KafkaAutoConfiguration;
import org.springframework.boot.autoconfigure.mongo.MongoAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

import tut.dushyant.demo.cafeapp.order.config.MongoDBConfig;

@EnableConfigurationProperties({MongoDBConfig.class, KafkaConfig.class})
@EnableAutoConfiguration(exclude = {MongoAutoConfiguration.class, KafkaAutoConfiguration.class, MongoRepositoriesAutoConfiguration.class})
@SpringBootApplication
public class OrderApplication {

    public static void main(String[] args) {
        SpringApplication.run(OrderApplication.class, args);
    }

}
