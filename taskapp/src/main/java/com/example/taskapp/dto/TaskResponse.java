package com.example.taskapp.dto;

import com.example.taskapp.entity.TaskEntity;
import java.time.LocalDate;

/** APIの応答として返すデータ */
public record TaskResponse(
                Long id,
                String title,
                boolean done,
                LocalDate dueDate,
                boolean overdue) {
        /** Entity から DTO へ変換する */
        public static TaskResponse from(TaskEntity entity) {
                boolean overdue = entity.getDueDate() != null
                                && !entity.isDone()
                                && entity.getDueDate().isBefore(LocalDate.now());

                return new TaskResponse(
                                entity.getId(),
                                entity.getTitle(),
                                entity.isDone(),
                                entity.getDueDate(),
                                overdue);
        }
}