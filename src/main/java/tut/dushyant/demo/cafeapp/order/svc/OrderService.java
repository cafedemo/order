package tut.dushyant.demo.cafeapp.order.svc;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;

import lombok.extern.slf4j.Slf4j;

import org.apache.kafka.clients.producer.ProducerRecord;
import org.bson.Document;
import org.springframework.context.ApplicationContext;
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
import java.util.Optional;
import java.util.concurrent.ExecutionException;

@Service
@Slf4j
public class OrderService {

    private KafkaTemplate<String, String> kafkaTemplate = null;
    private MongoDatabase mongoDatabase = null;
    @SuppressWarnings("unused")
    private TransactionTemplate transactionTemplate = null;
    @SuppressWarnings("unused")
    private final ApplicationContext applicationContext;
    private final MongoDBConfig mongoDBConfig;
    private final KafkaConfig kafkaConfig;

    @SuppressWarnings("unchecked")
    public OrderService(KafkaConfig kafkaConfig,
                        MongoDBConfig mongoDBConfig,
                        ApplicationContext applicationContext) {
        this.kafkaConfig = kafkaConfig;
        this.mongoDBConfig = mongoDBConfig;
        this.applicationContext = applicationContext;

        if(kafkaConfig.isEnabled() && kafkaTemplate == null) {
            this.kafkaTemplate = Optional.ofNullable(applicationContext.getBean(KafkaTemplate.class))
                    .orElseThrow(() -> new OrderCafeException("Kafka template not found"));
        }
        if(mongoDBConfig.isEnabled() && mongoDatabase == null) {
            this.mongoDatabase = Optional.ofNullable(applicationContext.getBean(MongoDatabase.class))
                    .orElseThrow(() -> new OrderCafeException("Mongo database not found"));
            this.transactionTemplate = new TransactionTemplate(
                    Optional.ofNullable(applicationContext.getBean(PlatformTransactionManager.class))
                            .orElseThrow(() -> new OrderCafeException("Transaction manager not found"))
            );
        }
    }

    /**
     * This service uses outbox pattern to first add order to mongo
     * and then send message to kafka for places order.
     * If any of mongo or Kafka fails, it will rollback the transaction.
     */
    public OrderDetails placeOrder(OrderDetails orderDetails) {
        if (!kafkaConfig.isEnabled() && !mongoDBConfig.isEnabled()) {
            throw new OrderCafeException("None of the databases are enabled");
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

         if (mongoDBConfig.isEnabled()) {
            // add order to mongo
            orderDetails.setId(
                Objects.requireNonNull(
                        mongoDatabase.getCollection("orders")
                                .insertOne(orderDetails.toDocument())
                                .getInsertedId()
                ).asObjectId().getValue());
        }

        return orderDetails;
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