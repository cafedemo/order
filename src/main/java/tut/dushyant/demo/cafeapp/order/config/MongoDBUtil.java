package tut.dushyant.demo.cafeapp.order.config;

import com.mongodb.client.MongoDatabase;
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
public class MongoDBUtil {

    private MongoDBConfig mongoDBConfig;

    public MongoDBUtil(MongoDBConfig mongoDBConfig) {
        this.mongoDBConfig = mongoDBConfig;
    }

    @Bean
    MongoClient createMongoClient() {
        return MongoClients.create(
            "mongodb://" + mongoDBConfig.getUsername() + ":" + mongoDBConfig.getPassword() + "@"
                + mongoDBConfig.getHost() + ":" + mongoDBConfig.getPort() + "/" + mongoDBConfig.getDatabase()
                + "?authSource=admin"
        );
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
