package tut.dushyant.demo.cafeapp.order.config;

import com.mongodb.client.MongoDatabase;
import lombok.extern.slf4j.Slf4j;
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
public class MongoDBUtil {

    private MongoDBConfig mongoDBConfig;

    public MongoDBUtil(MongoDBConfig mongoDBConfig) {
        this.mongoDBConfig = mongoDBConfig;
    }

    @Bean
    MongoClient createMongoClient() {
        log.atInfo().log(mongoDBConfig.getProtocol()+"://" + mongoDBConfig.getCredentials() + "@"
                + mongoDBConfig.getHost() + "/perfsbcp3_sbcp_order" + mongoDBConfig.getOptions());  
        return MongoClients.create(
            mongoDBConfig.getProtocol()+"://" + mongoDBConfig.getCredentials() + "@"
                + mongoDBConfig.getHost() + "/perfsbcp3_sbcp_order" + mongoDBConfig.getOptions()
        );
    }

    @Bean
    MongoTemplate createMongoTemplate() {
        return new MongoTemplate(createMongoClient(), "perfsbcp3_sbcp_order");
    }

    @Bean
    MongoDatabase getDatabase() {
        return createMongoClient().getDatabase("perfsbcp3_sbcp_order");
    }

    @Bean
    MongoDatabaseFactory getMongoDatabaseFactory() {
        return new SimpleMongoClientDatabaseFactory(createMongoClient(), "perfsbcp3_sbcp_order");
    }

    @Bean
    PlatformTransactionManager transactionManager(MongoDatabaseFactory dbFactory) {
        return new MongoTransactionManager(dbFactory);
    }
}
