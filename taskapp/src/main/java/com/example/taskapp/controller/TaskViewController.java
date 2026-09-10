package com.example.taskapp.controller;

import com.example.taskapp.model.TaskForm;
import com.example.taskapp.service.TaskService;
import com.example.taskapp.exception.TaskNotFoundException;

import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/tasks")
public class TaskViewController {

    private final TaskService service;

    public TaskViewController(TaskService service) {
        this.service = service;
    }

    /** 一覧＋登録フォーム */
    @GetMapping
    public String list(@RequestParam(required = false) String filter,
            @RequestParam(required = false) String keyword,
            Model model) {

        if (keyword != null && !keyword.isBlank()) {
            model.addAttribute("tasks", service.findByTitleContainingOrderByDueDateAsc(keyword));
        } else {
            model.addAttribute("tasks", switch (filter == null ? "all" : filter) {
                case "done" -> service.findByDone(true);
                case "todo" -> service.findByDone(false);
                default -> service.findAll();
            });
        }
        model.addAttribute("filter", filter);
        if (!model.containsAttribute("taskForm")) {
            model.addAttribute("taskForm", new TaskForm());
        }
        return "task/list";
    }

    /** 登録 */
    @PostMapping
    public String create(@Valid @ModelAttribute TaskForm taskForm,
            BindingResult bindingResult,
            Model model,
            RedirectAttributes redirectAttributes) {

        if (bindingResult.hasErrors()) {
            model.addAttribute("tasks", service.findAll());
            return "task/list";
        }

        service.create(taskForm.getTitle(), taskForm.getDueDate(), taskForm.getCategory());
        redirectAttributes.addFlashAttribute("message", "タスクを登録しました");
        return "redirect:/tasks";
    }

    /** 完了状態の切り替え */
    @PostMapping("/{id}/toggle")
    public String toggle(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        service.toggleDone(id);
        redirectAttributes.addFlashAttribute("message", "状態を変更しました");
        return "redirect:/tasks";
    }

    /** 削除 */
    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        service.delete(id);
        redirectAttributes.addFlashAttribute("message", "タスクを削除しました");
        return "redirect:/tasks";
    }

    @ExceptionHandler(TaskNotFoundException.class)
    public String handleNotFound(TaskNotFoundException e, RedirectAttributes redirectAttributes) {
        redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        return "redirect:/tasks";
    }
}