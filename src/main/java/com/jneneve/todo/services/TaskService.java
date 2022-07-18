package com.jneneve.todo.services;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.jneneve.todo.entities.Task;
import com.jneneve.todo.repositories.TaskRepository;
import com.jneneve.todo.services.exceptions.ResourceNotFoundException;

import lombok.AllArgsConstructor;

@AllArgsConstructor
@Service
public class TaskService {

	private final TaskRepository repository;

	public List<Task> findAll() {
		return repository.findAll();
	}

	public Task findById(Long id) {
		Optional<Task> task = repository.findById(id);
		if (!task.isPresent()) {
			throw new ResourceNotFoundException("Task of id " + id + " not found.");
		}
		return task.get();
	}

	public Task insert(Task obj) {
		return repository.save(obj);
	}

	public void delete(Long id) {
		Optional<Task> task = repository.findById(id);
		if (!task.isPresent()) {
			throw new ResourceNotFoundException("Task of id " + id + " not found.");
		}
		repository.deleteById(id);
	}

	public Task update(Long id, Task obj) {
		Optional<Task> task = repository.findById(id);
		if (!task.isPresent()) {
			throw new ResourceNotFoundException("Task of id " + id + " not found.");
		}

		Task taskRecord = task.get();
		taskRecord.setTitle(obj.getTitle());
		taskRecord.setDescription(obj.getDescription());
		taskRecord.setTaskStatus(obj.getTaskStatus());
		taskRecord.setClosedDate(obj.getClosedDate());

		return repository.save(taskRecord);
	}
}
