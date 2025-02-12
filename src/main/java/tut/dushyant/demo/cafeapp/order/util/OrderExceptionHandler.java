package tut.dushyant.demo.cafeapp.order.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
@Slf4j
public class OrderExceptionHandler {

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<String> handleRuntimeException(RuntimeException ex) {
        // Log the exception (optional)
        log.error("RuntimeException: ", ex);

        // Return a response entity with a custom message and HTTP status
        return new ResponseEntity<>("An unexpected error occurred: " + ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(OrderCafeException.class)
    public ResponseEntity<String> handleOrderCafeException(OrderCafeException ex) {
        // Log the exception (optional)
        log.error("OrderCafeException: ", ex);

        // Return a response entity with a custom message and HTTP status
        return new ResponseEntity<>("Order processing error: " + ex.getMessage(), HttpStatus.BAD_REQUEST);
    }
}