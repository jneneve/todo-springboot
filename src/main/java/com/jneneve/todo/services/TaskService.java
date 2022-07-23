package com.jneneve.todo.services;

import java.util.List;

import org.springframework.stereotype.Service;

import com.jneneve.todo.entities.Task;
import com.jneneve.todo.repositories.TaskRepository;
import com.jneneve.todo.services.exceptions.ResourceFoundException;
import com.jneneve.todo.services.exceptions.ResourceNotFoundException;

import lombok.AllArgsConstructor;

@AllArgsConstructor
@Service
public class TaskService {

	private final TaskRepository taskRepository;

	public List<Task> getAllTasks() {
		return taskRepository.findAll();
	}

	public Task getTask(Long id) {
		if (!taskRepository.existsById(id)) {
			throw new ResourceNotFoundException("Task of id " + id + " not found.");
		}
		return taskRepository.findById(id).get();
	}

	public Task addTask(Task obj) {
		return taskRepository.save(obj);
	}

	public void deleteTask(Long id) {
		if (!taskRepository.existsById(id)) {
			throw new ResourceNotFoundException("Task of id " + id + " not found.");
		}
		Task task = taskRepository.findById(id).get();

		if (!task.getUsers().isEmpty()) {
			throw new ResourceFoundException("Task of id " + id + " is associated with one or more users.");
		}

		taskRepository.deleteById(id);
	}

	public Task updateTask(Long id, Task obj) {
		if (!taskRepository.existsById(id)) {
			throw new ResourceNotFoundException("Task of id " + id + " not found.");
		}
		Task task = taskRepository.findById(id).get();

		task.setTitle(obj.getTitle());
		task.setDescription(obj.getDescription());
		task.setTaskStatus(obj.getTaskStatus());
		task.setClosedDate(obj.getClosedDate());

		return taskRepository.save(task);
	}
}
