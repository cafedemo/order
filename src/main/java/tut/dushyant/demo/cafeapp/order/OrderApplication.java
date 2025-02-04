package tut.dushyant.demo.cafeapp.order;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

import tut.dushyant.demo.cafeapp.order.config.KafkaConfig;
import tut.dushyant.demo.cafeapp.order.config.MongoDBConfig;

@EnableConfigurationProperties({KafkaConfig.class, MongoDBConfig.class})
@SpringBootApplication
public class OrderApplication {

    public static void main(String[] args) {
        SpringApplication.run(OrderApplication.class, args);
    }

}
