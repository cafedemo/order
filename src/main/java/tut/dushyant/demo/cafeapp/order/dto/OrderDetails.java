package tut.dushyant.demo.cafeapp.order.dto;

import java.util.List;

import lombok.Data;
import org.bson.Document;
import org.bson.types.ObjectId;

@Data
public class OrderDetails {
    private ObjectId id;
    private String customerId;
    private String orderId;
    private String orderStatus;
    private List<OrderItem> orderItems;
    private String baristaId;

    @Data
    public static class OrderItem {
        private String itemId;
        private String itemName;
        private int quantity;
        private double price;
    }

    public Document toDocument() {
        Document document = new Document();
        if (this.id != null) {
            document.put("_id", this.id);
        }
        document.put("customerId", this.customerId);
        document.put("orderId", this.orderId);
        document.put("orderStatus", this.orderStatus);
        document.put("baristaId", this.baristaId);
        if (this.orderItems != null) {
            document.put("orderItems", this.orderItems.stream()
                .map(orderItem -> new Document()
                        .append("itemId", orderItem.itemId)
                        .append("itemName", orderItem.itemName)
                        .append("quantity", orderItem.quantity)
                        .append("price", orderItem.price))
                .toList());
        }
        return document;
    }

    public static OrderDetails fromDocument(Document document) {
        OrderDetails orderDetails = new OrderDetails();
        orderDetails.setId(document.getObjectId("_id"));
        orderDetails.setCustomerId(document.getString("customerId"));
        orderDetails.setOrderId(document.getString("orderId"));
        orderDetails.setOrderStatus(document.getString("orderStatus"));
        orderDetails.setBaristaId(document.getString("baristaId"));
        if (document.getList("orderItems", Document.class) != null) {
            orderDetails.setOrderItems(document.getList("orderItems", Document.class).stream()
                .map(orderItemDoc -> {
                    OrderItem orderItem = new OrderItem();
                    orderItem.setItemId(orderItemDoc.getString("itemId"));
                    orderItem.setItemName(orderItemDoc.getString("itemName"));
                    orderItem.setQuantity(orderItemDoc.getInteger("quantity"));
                    orderItem.setPrice(orderItemDoc.getDouble("price"));
                    return orderItem;
                })
                .toList());
        }
        return orderDetails;
    }

}
