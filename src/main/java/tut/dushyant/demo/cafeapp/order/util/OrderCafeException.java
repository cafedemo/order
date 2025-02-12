package tut.dushyant.demo.cafeapp.order.util;

public class OrderCafeException extends RuntimeException {
    public OrderCafeException(String message) {
        super(message);
    }

    public OrderCafeException(String message, Throwable cause) {
        super(message, cause);
    }
}
