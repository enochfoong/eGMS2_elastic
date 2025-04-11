package hk.com.asl.egms2.util;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.Nullable;

public class ResponseUtil {

    // Generic method to return a response with a message and data
    public static <T> ResponseEntity<ResponseWrapper<T>> ok(String message, @Nullable T data) {
        ResponseWrapper<T> response = new ResponseWrapper<>(message, data);
        return ResponseEntity.ok(response);
    }

    // Overloaded method for default success message
    public static <T> ResponseEntity<ResponseWrapper<T>> ok(@Nullable T data) {
        return ok("Operation successful", data);
    }

    // For error responses
    public static <T> ResponseEntity<ResponseWrapper<T>> error(HttpStatus status, String message, @Nullable T data) {
        ResponseWrapper<T> response = new ResponseWrapper<>(message, data);
        return ResponseEntity.status(status).body(response);
    }
}