package tut.dushyant.demo.cafeapp.order.web;

import org.springframework.web.bind.annotation.*;
import tut.dushyant.demo.cafeapp.order.dto.OrderDetails;
import tut.dushyant.demo.cafeapp.order.svc.OrderService;

import java.util.List;

@RestController
@RequestMapping("/order")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @GetMapping(path = "", produces = "application/json")
    public List<OrderDetails> getOrders() {
        // get orders from service
        return orderService.getOrders();
    }

    @GetMapping(path = "/{orderId}", produces = "application/json")
    public OrderDetails getOrderDetails(@PathVariable String orderId) {
        // get order details from service
        return orderService.getOrderDetails(orderId);
    }

    @PostMapping(path = "/", consumes = "application/json", produces = "application/json")
    public OrderDetails placeOrder(@RequestBody OrderDetails orderDetails) {
        // place order
        return orderService.placeOrder(orderDetails);
    }
}
