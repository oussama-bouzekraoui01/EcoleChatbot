package com.example.ecolechatbot.models;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class Response {
    private String message;
    private LocalDateTime dateTime;
}
