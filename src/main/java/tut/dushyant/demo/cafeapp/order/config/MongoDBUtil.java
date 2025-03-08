package tut.dushyant.demo.cafeapp.order.config;

import com.mongodb.client.MongoDatabase;
import lombok.extern.slf4j.Slf4j;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.MongoDatabaseFactory;
import org.springframework.data.mongodb.MongoTransactionManager;
import org.springframework.data.mongodb.core.MongoTemplate;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import org.springframework.data.mongodb.core.SimpleMongoClientDatabaseFactory;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
@Slf4j
@ConditionalOnProperty(prefix = "mongodb", name = "enabled", havingValue = "true")
public class MongoDBUtil {

    private MongoDBConfig mongoDBConfig;

    public MongoDBUtil(MongoDBConfig mongoDBConfig) {
        this.mongoDBConfig = mongoDBConfig;
    }

    @Bean
    MongoClient createMongoClient() {
        String connStr = mongoDBConfig.getProtocol()+"://" + mongoDBConfig.getCredentials() + "@"
        + mongoDBConfig.getHost() + "/" + mongoDBConfig.getDatabase() + "?" + mongoDBConfig.getOptions();
        log.atInfo().log(connStr);
        return MongoClients.create(connStr);
    }

    @Bean
    MongoTemplate createMongoTemplate() {
        return new MongoTemplate(createMongoClient(), mongoDBConfig.getDatabase());
    }

    @Bean
    MongoDatabase getDatabase() {
        return createMongoClient().getDatabase(mongoDBConfig.getDatabase());
    }

    @Bean
    MongoDatabaseFactory getMongoDatabaseFactory() {
        return new SimpleMongoClientDatabaseFactory(createMongoClient(), mongoDBConfig.getDatabase());
    }

    @Bean
    PlatformTransactionManager transactionManager(MongoDatabaseFactory dbFactory) {
        return new MongoTransactionManager(dbFactory);
    }
}
