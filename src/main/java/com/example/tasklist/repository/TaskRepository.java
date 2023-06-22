package com.example.tasklist.repository;

import com.example.tasklist.domain.task.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface TaskRepository extends JpaRepository<Task, Long> {

    @Query(value = """
            SELECT * FROM tasks t
            JOIN users_tasks ut ON ut.task_id = t.id
            WHERE ut.user_id = :userId
            """, nativeQuery = true)
    List<Task> findAllByUserId(@Param("userId") Long userId);

    @Modifying
    @Query(value = """
            INSERT INTO tasks (title, description, expiration_date, status)
            VALUES (:#{#task.title}, :#{#task.description}, :#{#task.expirationDateEpochSecond()}, :#{#task.status.name()})
            """, nativeQuery = true)
    void create(@Param("task") Task task);

    @Modifying
    @Query(value = """
            INSERT INTO users_tasks (task_id, user_id)
            VALUES (:taskId, :userId)
            """, nativeQuery = true)
    void assignToUserById(@Param("taskId") Long taskId, @Param("userId") Long userId);

    Optional<Task> findTopByOrderByIdDesc();
}
