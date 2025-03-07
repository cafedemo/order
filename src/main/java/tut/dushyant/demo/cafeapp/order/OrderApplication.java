package tut.dushyant.demo.cafeapp.order;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

import tut.dushyant.demo.cafeapp.order.config.MongoDBConfig;

@EnableConfigurationProperties({MongoDBConfig.class})
@SpringBootApplication
public class OrderApplication {

    public static void main(String[] args) {
        SpringApplication.run(OrderApplication.class, args);
    }

}
