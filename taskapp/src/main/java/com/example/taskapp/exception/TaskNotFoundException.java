package com.example.taskapp.exception;

public class TaskNotFoundException extends RuntimeException {

    private final Long taskId;

    public TaskNotFoundException(Long taskId) {
        super("指定されたタスクが見つかりません（id=" + taskId + "）");
        this.taskId = taskId;
    }

    public Long getTaskId() {
        return taskId;
    }
}