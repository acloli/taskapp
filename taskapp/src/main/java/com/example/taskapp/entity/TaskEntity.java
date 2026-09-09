package com.example.taskapp.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.LocalDate;

@Entity // ★「これはテーブルに対応するクラスです」
@Table(name = "tasks") // 対応するテーブル名
public class TaskEntity {

    @Id // ★主キー
    @GeneratedValue(strategy = GenerationType.IDENTITY) // ★自動採番
    private Long id;

    @Column(nullable = false, length = 100) // NOT NULL, VARCHAR(100)
    private String title;

    @Column(nullable = false)
    private boolean done;

    @Column(name = "due_date") // 列名を指定
    private LocalDate dueDate;

    /** JPAが内部で使うため、引数なしコンストラクタが必須 */
    protected TaskEntity() {
    }

    public TaskEntity(String title, LocalDate dueDate) {
        this.title = title;
        this.done = false;
        this.dueDate = dueDate;
    }

    // ===== getter / setter =====
    public Long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public boolean isDone() {
        return done;
    }

    public void setDone(boolean done) {
        this.done = done;
    }

    public LocalDate getDueDate() {
        return dueDate;
    }

    public void setDueDate(LocalDate dueDate) {
        this.dueDate = dueDate;
    }
}