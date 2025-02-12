package tut.dushyant.demo.cafeapp.order.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

import lombok.Data;

@ConfigurationProperties(prefix = "mongodb")
@Data
public class MongoDBConfig {
    private String host;
    private String options;
    private String protocol;
    private String credentials;
}
