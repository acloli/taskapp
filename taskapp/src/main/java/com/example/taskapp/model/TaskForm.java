package com.example.taskapp.model;

import com.example.taskapp.model.TaskCategory;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

import org.springframework.format.annotation.DateTimeFormat;

public class TaskForm {
    @NotBlank(message = "タイトルは必須です")
    @Size(max = 100, message = "タイトルは100文字以内で入力してください")
    private String title;

    @FutureOrPresent(message = "期限に過去の日付は指定できません")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate dueDate;

    @NotNull(message = "カテゴリは必須です")
    private TaskCategory category;

    private boolean done;

    public TaskForm() {
        this.done = false;
    }

    public TaskForm(String title, LocalDate dueDate, TaskCategory category, boolean done) {
        this.title = title;
        this.dueDate = dueDate;
        this.category = category;
        this.done = done;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public LocalDate getDueDate() {
        return dueDate;
    }

    public void setDueDate(LocalDate dueDate) {
        this.dueDate = dueDate;
    }

    public TaskCategory getCategory() {
        return category;
    }

    public void setCategory(TaskCategory category) {
        this.category = category;
    }

    public boolean isDone() {
        return done;
    }

    public void setDone(boolean done) {
        this.done = done;
    }
}