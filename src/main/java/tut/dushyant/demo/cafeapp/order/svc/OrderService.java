package tut.dushyant.demo.cafeapp.order.svc;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;

import lombok.extern.slf4j.Slf4j;

import org.apache.kafka.clients.producer.ProducerRecord;
import org.bson.Document;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

import tut.dushyant.demo.cafeapp.order.config.KafkaConfig;
import tut.dushyant.demo.cafeapp.order.config.MongoDBConfig;
import tut.dushyant.demo.cafeapp.order.dto.OrderDetails;
import tut.dushyant.demo.cafeapp.order.util.OrderCafeException;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.ExecutionException;

@Service
@Slf4j
public class OrderService {

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final MongoDatabase mongoDatabase;
    private final TransactionTemplate transactionTemplate;
    private final MongoDBConfig mongoDBConfig;
    private final KafkaConfig kafkaConfig;

    public OrderService(KafkaTemplate<String, String> kafkaTemplate,
                        MongoDatabase mongoDatabase,
                        PlatformTransactionManager transactionManager,
                        KafkaConfig kafkaConfig,
                        MongoDBConfig mongoDBConfig) {
        this.kafkaTemplate = kafkaTemplate;
        this.mongoDatabase = mongoDatabase;
        this.transactionTemplate = new TransactionTemplate(transactionManager);
        this.transactionTemplate.setTimeout(30);
        this.mongoDBConfig = mongoDBConfig;
        this.kafkaConfig = kafkaConfig;
    }

    /**
     * This service uses outbox pattern to first add order to mongo
     * and then send message to kafka for places order.
     * If any of mongo or Kafka fails, it will rollback the transaction.
     */
    public OrderDetails placeOrder(OrderDetails orderDetails) {
        return this.transactionTemplate.execute(status -> {

            if (!kafkaConfig.isEnabled() && !mongoDBConfig.isEnabled()) {
                throw new OrderCafeException("None of the databases are enabled");
            }

            //If mongoDB is enabled, then call the below method
            if (mongoDBConfig.isEnabled()) {
                // add order to mongo
                orderDetails.setId(
                    Objects.requireNonNull(
                            mongoDatabase.getCollection("orders")
                                    .insertOne(orderDetails.toDocument())
                                    .getInsertedId()
                    ).asObjectId().getValue());
            }

            if (kafkaConfig.isEnabled()) {
                // send message to kafka
                try {
                    // convert to json
                    String orderDetailsJson = new com.fasterxml.jackson.databind.ObjectMapper().writeValueAsString(orderDetails);
                    kafkaTemplate.send(new ProducerRecord<>(
                            "order-topic",
                            orderDetails.getOrderId(),
                            orderDetailsJson
                    )).thenAcceptAsync(result -> {
                        if (result != null) {
                            log.atInfo().log("Message sent to kafka");
                        } else {
                            // this should not happen. If message send is failed, kafka should throw exception
                            throw new OrderCafeException("Failed to send message to kafka ");
                        }
                    }).exceptionally(ex -> {
                        throw new OrderCafeException("Failed to send message to kafka", ex);
                    }).get();
                } catch (ExecutionException | JsonProcessingException | InterruptedException e) {
                    throw new OrderCafeException("Failed to send message to kafka", e);
                }
            }
            
            return orderDetails;
        });
    }

    public OrderDetails getOrderDetails(String orderId) {
        // fetch order from mongo
        MongoCollection<Document> collection = mongoDatabase.getCollection("orders");
        return OrderDetails.fromDocument(
                Objects.requireNonNull(
                        collection.find(new Document("orderId", orderId)).first()
                )
        );
    }

    /**
     * This services fetches all orders from mongo database.
     * @return list of orders
     */
    public List<OrderDetails> getOrders() {
        // fetch all orders from mongo
        MongoCollection<Document> collection = mongoDatabase.getCollection("orders");
        List<OrderDetails> orders = new java.util.ArrayList<>();
        try (MongoCursor<Document> cursor = collection.find().iterator()) {
            while (cursor.hasNext()) {
                orders.add(OrderDetails.fromDocument(cursor.next()));
            }
        }

        return orders;
    }
}