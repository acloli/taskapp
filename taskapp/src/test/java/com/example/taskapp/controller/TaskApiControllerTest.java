package com.example.taskapp.controller;

import com.example.taskapp.entity.TaskEntity;
import com.example.taskapp.exception.TaskNotFoundException;
import com.example.taskapp.service.TaskService;
import com.example.taskapp.model.TaskCategory;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;

import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(TaskApiController.class) // ★Web層だけを起動する（軽量）
class TaskApiControllerTest {

        @Autowired
        private MockMvc mockMvc;

        @MockitoBean // ★DIコンテナに偽のServiceを登録する
        private TaskService service;

        @Test
        @DisplayName("GET /api/v1/tasks で一覧がJSONで返る")
        void list() throws Exception {
                TaskEntity task = new TaskEntity("設計書をレビューする", LocalDate.of(2026, 12, 31), TaskCategory.WORK);
                when(service.findAll()).thenReturn(List.of(task));

                mockMvc.perform(get("/api/v1/tasks"))
                                .andExpect(status().isOk()) // 200
                                .andExpect(jsonPath("$").isArray())
                                .andExpect(jsonPath("$[0].title").value("設計書をレビューする"))
                                .andExpect(jsonPath("$[0].done").value(false));
        }

        @Test
        @DisplayName("存在しないIDを指定すると404が返る")
        void detailNotFound() throws Exception {
                when(service.getById(anyLong())).thenThrow(new TaskNotFoundException(999L));

                mockMvc.perform(get("/api/v1/tasks/999"))
                                .andExpect(status().isNotFound()) // 404
                                .andExpect(jsonPath("$.status").value(404))
                                .andExpect(jsonPath("$.message").value("指定されたタスクが見つかりません（id=999）"));
        }

        @Test
        @DisplayName("正しいJSONでPOSTすると201が返る")
        void create() throws Exception {
                TaskEntity saved = new TaskEntity("新しいタスク", LocalDate.of(2026, 12, 31), TaskCategory.WORK);
                when(service.create(any(), any(), any())).thenReturn(saved);

                mockMvc.perform(post("/api/v1/tasks")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                                                {"title":"新しいタスク","dueDate":"2026-12-31","category":"WORK"}
                                                """)) // Java① のテキストブロック
                                .andExpect(status().isCreated()) // 201
                                .andExpect(jsonPath("$.title").value("新しいタスク"));
        }

        @Test
        @DisplayName("タイトルが空のJSONでPOSTすると400とエラー詳細が返る")
        void createValidationError() throws Exception {
                mockMvc.perform(post("/api/v1/tasks")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                                                {"title":"","dueDate":"2026-12-31"}
                                                """))
                                .andExpect(status().isBadRequest()) // 400
                                .andExpect(jsonPath("$.message").value("入力内容に誤りがあります"))
                                .andExpect(jsonPath("$.details[0]").value("title: タイトルは必須です"));
        }

        @Test
        @DisplayName("カテゴリが未指定のJSONでPOSTすると400とエラー詳細が返る")
        void createCategoryValidationError() throws Exception {
                mockMvc.perform(post("/api/v1/tasks")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                                                {"title":"新しいタスク","dueDate":"2026-12-31"}
                                                """))
                                .andExpect(status().isBadRequest()) // 400
                                .andExpect(jsonPath("$.message").value("入力内容に誤りがあります"))
                                .andExpect(jsonPath("$.details[0]").value("category: カテゴリは必須です"));
        }

        @Test
        @DisplayName("DELETE で 204 が返る")
        void delete204() throws Exception {
                mockMvc.perform(delete("/api/v1/tasks/1"))
                                .andExpect(status().isNoContent()); // 204
        }
}