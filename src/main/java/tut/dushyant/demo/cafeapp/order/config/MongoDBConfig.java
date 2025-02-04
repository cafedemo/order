package tut.dushyant.demo.cafeapp.order.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

import lombok.Data;

@ConfigurationProperties(prefix = "mongodb")
@Data
public class MongoDBConfig {
    private String host;
    private int port;
    private String database;
    private String collection;
    private String username;
    private String password;
}
