package com.jneneve.todo.services;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.jneneve.todo.entities.Task;
import com.jneneve.todo.entities.User;
import com.jneneve.todo.repositories.TaskRepository;
import com.jneneve.todo.repositories.UserRepository;
import com.jneneve.todo.services.exceptions.ResourceFoundException;
import com.jneneve.todo.services.exceptions.ResourceNotFoundException;

@Service
public class UserService {

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private TaskRepository taskRepository;

	@Autowired
	private EmailSenderService emailSenderService;

	public List<User> findAll() {
		return userRepository.findAll();
	}

	public User findById(Long id) {
		Optional<User> user = userRepository.findById(id);
		if (!user.isPresent()) {
			throw new ResourceNotFoundException("User of id " + id + " not found.");
		}
		return user.get();
	}

	public User insert(User obj) {
		Optional<User> user = userRepository.findByEmail(obj.getEmail());
		if (user.isPresent()) {
			throw new ResourceNotFoundException("An user with this email has already been created.");
		}
		return userRepository.save(obj);
	}

	public void delete(Long id) {
		Optional<User> user = userRepository.findById(id);
		if (!user.isPresent()) {
			throw new ResourceNotFoundException("User of id " + id + " not found.");
		}
		userRepository.deleteById(id);
	}

	public User update(Long id, User obj) {
		Optional<User> user = userRepository.findById(id);
		if (!user.isPresent()) {
			throw new ResourceNotFoundException("User of id " + id + " not found.");
		}

		User userRecord = user.get();

		userRecord.setName(obj.getName());

		if (!userRecord.getEmail().equals(obj.getEmail())) {
			user = userRepository.findByEmail(obj.getEmail());
			if (user.isPresent())
				throw new ResourceFoundException("An user with this email has already been created.");
			userRecord.setEmail(obj.getEmail());
		}

		userRecord.setEmail(obj.getEmail());
		userRecord.setJobTitle(obj.getJobTitle());

		return userRepository.save(userRecord);
	}

	public User addTask(Long id, Task obj) {
		Optional<User> user = userRepository.findById(id);
		if (!user.isPresent()) {
			throw new ResourceNotFoundException("User of id " + id + " not found.");
		}

		User userRecord = user.get();

		Task taskExists = taskRepository.findById(obj.getId()).orElseThrow(() -> new IllegalArgumentException());

		List<Task> taskRecord = userRecord.getTasks().stream().filter(task -> task.getId().equals(taskExists.getId()))
				.collect(Collectors.toList());
		if (!taskRecord.isEmpty()) {
			throw new ResourceFoundException("The task has already been add.");
		}

		userRecord.getTasks().add(obj);

		emailSenderService.sendEmail(userRecord, obj);

		return userRepository.save(userRecord);
	}

	public void deleteTask(Long user_id, Long task_id) {
		Optional<User> user = userRepository.findById(user_id);
		if (!user.isPresent()) {
			throw new ResourceNotFoundException("User of id " + user_id + " not found.");
		}

		User userRecord = user.get();

		Task task = taskRepository.findById(task_id)
				.orElseThrow(() -> new ResourceNotFoundException("Task of id " + task_id + " not found."));

		userRecord.getTasks().remove(task);

		userRepository.save(userRecord);
	}
}
