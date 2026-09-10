package com.example.taskapp.service;

import com.example.taskapp.entity.TaskEntity;
import com.example.taskapp.exception.BusinessRuleViolationException;
import com.example.taskapp.exception.TaskNotFoundException;
import com.example.taskapp.repository.TaskJpaRepository;
import com.example.taskapp.model.TaskCategory;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
@Transactional(readOnly = true) // ★既定は読み取り専用
public class TaskService {

    private final TaskJpaRepository repository;

    public TaskService(TaskJpaRepository repository) {
        this.repository = repository;
    }

    public List<TaskEntity> findAll() {
        return repository.findAllByOrderByDueDateAsc();
    }

    public Optional<TaskEntity> findById(Long id) {
        return repository.findById(id);
    }

    public List<TaskEntity> findByDone(boolean done) {
        return repository.findByDone(done);
    }

    private static final Logger log = LoggerFactory.getLogger(TaskService.class);

    @Transactional // ★更新系は書き込み可能にする
    public TaskEntity create(String title, LocalDate dueDate, TaskCategory category) {
        if (title == null || title.isBlank()) {
            throw new BusinessRuleViolationException("タイトルは必須です");
        }
        if (dueDate != null && dueDate.isBefore(LocalDate.now())) {
            throw new BusinessRuleViolationException("期限に過去の日付は指定できません");
        }
        if (category == null) {
            throw new BusinessRuleViolationException("カテゴリは必須です");
        }
        log.info("タスクを登録します: title={}, dueDate={}, category={}", title, dueDate, category);
        TaskEntity saved = repository.save(new TaskEntity(title.trim(), dueDate, category));
        log.info("タスクを登録しました: id={}", saved.getId());
        return saved;
    }

    public TaskEntity getById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new TaskNotFoundException(id));
    }

    @Transactional
    public TaskEntity toggleDone(Long id) {
        TaskEntity task = getById(id);
        task.setDone(!task.isDone());
        return task;
    }

    @Transactional
    public void delete(Long id) {
        if (!repository.existsById(id)) {
            throw new TaskNotFoundException(id);
        }
        repository.deleteById(id);
    }

    public List<TaskEntity> findByCategory(TaskCategory category) {
        return repository.findByCategory(category);
    }

    public List<TaskEntity> findByTitleContainingOrderByDueDateAsc(String keyword) {
        return repository.findByTitleContainingOrderByDueDateAsc(keyword);
    }
}