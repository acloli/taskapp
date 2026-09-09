package com.example.taskapp.dto;

import com.example.taskapp.model.TaskCategory;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

import io.swagger.v3.oas.annotations.media.Schema;

public record TaskRequest(

                @Schema(description = "タスクのタイトル", example = "設計書をレビューする") @NotBlank(message = "タイトルは必須です") @Size(max = 100) String title,

                @Schema(description = "期限（今日以降の日付）", example = "2026-08-31") @FutureOrPresent(message = "期限に過去の日付は指定できません") LocalDate dueDate,

                @Schema(description = "カテゴリ", example = "WORK") @NotNull(message = "カテゴリは必須です") TaskCategory category) {
}