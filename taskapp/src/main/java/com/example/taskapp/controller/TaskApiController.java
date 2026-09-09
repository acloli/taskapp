package com.example.taskapp.controller;

import com.example.taskapp.dto.TaskRequest;
import com.example.taskapp.dto.TaskResponse;
import com.example.taskapp.service.TaskService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/v1/tasks")
@Tag(name = "タスク管理", description = "タスクの登録・取得・更新・削除を行うAPI")
public class TaskApiController {

    private final TaskService service;

    public TaskApiController(TaskService service) {
        this.service = service;
    }

    /** 一覧取得: GET /api/v1/tasks?done=false */
    @Operation(summary = "タスク一覧の取得", description = "登録されているタスクを期限が近い順に返します。done パラメータで絞り込めます。")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "取得に成功")
    })
    @GetMapping
    public List<TaskResponse> list(@RequestParam(required = false) Boolean done) {
        var entities = (done == null) ? service.findAll() : service.findByDone(done);
        return entities.stream()
                .map(TaskResponse::from) // Java③ の Stream API
                .toList();
    }

    /** 1件取得: GET /api/v1/tasks/1 */
    @GetMapping("/{id}")
    public TaskResponse detail(@PathVariable Long id) {
        // 見つからない場合は例外を投げる（セクション2で処理する）
        return TaskResponse.from(service.getById(id));
    }

    /** 新規作成: POST /api/v1/tasks */
    @Operation(summary = "タスクの新規作成")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "作成に成功"),
            @ApiResponse(responseCode = "400", description = "入力内容が不正")
    })
    @PostMapping
    public ResponseEntity<TaskResponse> create(@Valid @RequestBody TaskRequest request) {
        TaskResponse created = TaskResponse.from(
                service.create(request.title(), request.dueDate(), request.category()));

        // 201 Created + Location ヘッダー（作成されたリソースの場所）
        return ResponseEntity
                .created(URI.create("/api/v1/tasks/" + created.id()))
                .body(created);
    }

    /** 完了切り替え: PATCH /api/v1/tasks/1/done */
    @PatchMapping("/{id}/done")
    public TaskResponse toggleDone(@PathVariable Long id) {
        return TaskResponse.from(service.toggleDone(id));
    }

    /** 削除: DELETE /api/v1/tasks/1 */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build(); // 204
    }
}