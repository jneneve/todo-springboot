package com.jneneve.todo.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.jneneve.todo.entities.Task;

public interface TaskRepository extends JpaRepository<Task, Long> {

}
