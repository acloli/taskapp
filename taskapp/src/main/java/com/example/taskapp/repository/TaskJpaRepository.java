package com.example.taskapp.repository;

import com.example.taskapp.entity.TaskEntity;
import com.example.taskapp.model.TaskCategory;

import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface TaskJpaRepository extends JpaRepository<TaskEntity, Long> {
    // ↑Entity ↑主キーの型

    // ★メソッド名を書くだけで、SQLが自動生成される
    List<TaskEntity> findByDone(boolean done);

    List<TaskEntity> findByTitleContaining(String keyword);

    List<TaskEntity> findByDueDateBeforeAndDoneFalse(LocalDate date);

    List<TaskEntity> findAllByOrderByDueDateAsc();

    List<TaskEntity> findByCategory(TaskCategory category);

    List<TaskEntity> findByTitleContainingOrderByDueDateAsc(String keyword);
}