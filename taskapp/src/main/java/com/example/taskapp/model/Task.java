package com.example.taskapp.model;

import java.time.LocalDate;

public record Task(
        Long id,
        String title,
        boolean done,
        LocalDate dueDate) {
}
