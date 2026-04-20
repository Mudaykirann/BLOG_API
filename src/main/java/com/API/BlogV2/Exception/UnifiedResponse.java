package com.API.BlogV2.Exception;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.time.LocalDateTime;

@JsonInclude(JsonInclude.Include.NON_NULL) // Hides null fields (like 'data' during an error)
public class UnifiedResponse<T> {
    private boolean success;
    private String message;
    private T data;
    private LocalDateTime timestamp;
    private Integer errorCode; // Optional: for specific internal error codes

    // Static helper for Success
    public static <T> UnifiedResponse<T> ok(String message, T data) {
        UnifiedResponse<T> res = new UnifiedResponse<>();
        res.setSuccess(true);
        res.setMessage(message);
        res.setData(data);
        res.setTimestamp(LocalDateTime.now());
        return res;
    }

    // Static helper for Errors
    public static <T> UnifiedResponse<T> error(int status, String message) {
        UnifiedResponse<T> res = new UnifiedResponse<>();
        res.setSuccess(false);
        res.setErrorCode(status);
        res.setMessage(message);
        res.setTimestamp(LocalDateTime.now());
        return res;
    }

    // Standard Getters and Setters
    public boolean isSuccess() { return success; }
    public void setSuccess(boolean success) { this.success = success; }
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
    public T getData() { return data; }
    public void setData(T data) { this.data = data; }
    public LocalDateTime getTimestamp() { return timestamp; }
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }
    public Integer getErrorCode() { return errorCode; }
    public void setErrorCode(Integer errorCode) { this.errorCode = errorCode; }
}