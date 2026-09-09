package com.example.taskapp.repository;

import com.example.taskapp.model.Task;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Repository // ★「データアクセスの部品です」という目印
public class TaskRepository {

    // 複数のリクエストが同時に来るため、スレッドセーフなMapを使う（Java③のセクション5）
    private final Map<Long, Task> store = new ConcurrentHashMap<>();
    private final AtomicLong sequence = new AtomicLong(0);

    public List<Task> findAll() {
        return new ArrayList<>(store.values());
    }

    public Optional<Task> findById(Long id) { // Java③で学んだOptional
        return Optional.ofNullable(store.get(id));
    }

    public Task save(Task task) {
        Long id = (task.id() == null) ? sequence.incrementAndGet() : task.id();
        Task saved = new Task(id, task.title(), task.done(), task.dueDate());
        store.put(id, saved);
        return saved;
    }

    public boolean deleteById(Long id) {
        return store.remove(id) != null;
    }
}