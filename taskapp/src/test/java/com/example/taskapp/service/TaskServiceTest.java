package com.example.taskapp.service;

import com.example.taskapp.entity.TaskEntity;
import com.example.taskapp.exception.BusinessRuleViolationException;
import com.example.taskapp.exception.TaskNotFoundException;
import com.example.taskapp.repository.TaskJpaRepository;
import com.example.taskapp.model.TaskCategory;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class) // ★Mockitoを有効にする
class TaskServiceTest {

    @Mock // ★偽のRepositoryを作る
    private TaskJpaRepository repository;

    @InjectMocks // ★偽物を注入したServiceを作る
    private TaskService service;

    private TaskEntity sampleTask;

    @BeforeEach // ★各テストの前に毎回実行される
    void setUp() {
        sampleTask = new TaskEntity("設計書をレビューする", LocalDate.of(2026, 12, 31), TaskCategory.WORK);
    }

    @Test
    @DisplayName("タイトルが空の場合は業務例外が発生する")
    void createWithBlankTitle() {
        assertThatThrownBy(() -> service.create("   ", LocalDate.of(2026, 12, 31), TaskCategory.WORK))
                .isInstanceOf(BusinessRuleViolationException.class)
                .hasMessage("タイトルは必須です");

        // 保存処理が呼ばれていないことも確認する
        verify(repository, never()).save(any());
    }

    @Test
    @DisplayName("期限が過去の日付の場合は業務例外が発生する")
    void createWithPastDueDate() {
        assertThatThrownBy(() -> service.create("タスク", LocalDate.now().minusDays(1), TaskCategory.WORK))
                .isInstanceOf(BusinessRuleViolationException.class)
                .hasMessage("期限に過去の日付は指定できません");
    }

    @Test
    @DisplayName("正常な入力の場合はタスクが保存される")
    void createSuccessfully() {
        // Arrange: 偽Repositoryの振る舞いを決める
        when(repository.save(any(TaskEntity.class))).thenReturn(sampleTask);

        // Act
        TaskEntity result = service.create("設計書をレビューする", LocalDate.of(2026, 12, 31), TaskCategory.WORK);

        // Assert
        assertThat(result.getTitle()).isEqualTo("設計書をレビューする");
        verify(repository, times(1)).save(any(TaskEntity.class)); // 1回だけ呼ばれた
    }

    @Test
    @DisplayName("存在しないIDを指定すると TaskNotFoundException が発生する")
    void getByIdNotFound() {
        when(repository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.getById(999L))
                .isInstanceOf(TaskNotFoundException.class)
                .hasMessageContaining("999");
    }

    @Test
    @DisplayName("完了状態を切り替えると done が反転する")
    void toggleDone() {
        when(repository.findById(1L)).thenReturn(Optional.of(sampleTask));

        assertThat(sampleTask.isDone()).isFalse(); // 事前確認

        TaskEntity result = service.toggleDone(1L);

        assertThat(result.isDone()).isTrue(); // 反転した
    }

    @Test
    @DisplayName("一覧取得は期限の昇順で返る")
    void findAll() {
        when(repository.findAllByOrderByDueDateAsc()).thenReturn(List.of(sampleTask));

        List<TaskEntity> result = service.findAll();

        assertThat(result).hasSize(1);
        verify(repository).findAllByOrderByDueDateAsc();
    }
}