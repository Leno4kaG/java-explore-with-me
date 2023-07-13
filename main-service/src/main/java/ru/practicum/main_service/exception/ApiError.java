package ru.practicum.main_service.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ApiError {
    private String status;
    private String reason;
    private String message;
    private String errors;
    private String timestamp;

}
