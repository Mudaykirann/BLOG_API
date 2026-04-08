package com.API.BlogV2.Exception;

public class ApiResponse<T> {
    private String status;
    private String message;
    private T data;

    // Constructors
    public ApiResponse(String status, String message, T data) {
        this.status = status;
        this.message = message;
        this.data = data;
    }

    // Getters and Setters are MANDATORY  to see the data
    public String getStatus() { return status; }
    public String getMessage() { return message; }
    public T getData() { return data; }
}
