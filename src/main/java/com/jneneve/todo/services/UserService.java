package com.jneneve.todo.services;

import java.util.List;

import org.springframework.stereotype.Service;

import com.jneneve.todo.entities.Task;
import com.jneneve.todo.entities.User;
import com.jneneve.todo.repositories.TaskRepository;
import com.jneneve.todo.repositories.UserRepository;
import com.jneneve.todo.services.exceptions.ResourceFoundException;
import com.jneneve.todo.services.exceptions.ResourceNotFoundException;

import lombok.AllArgsConstructor;

@AllArgsConstructor
@Service
public class UserService {

	private final UserRepository userRepository;

	private final TaskRepository taskRepository;

	private final EmailSenderService emailSenderService;

	public List<User> getAllUsers() {
		return userRepository.findAll();
	}

	public User getUser(Long id) {
		if (!userRepository.existsById(id)) {
			throw new ResourceNotFoundException("User of id " + id + " not found.");
		}
		return userRepository.findById(id).get();
	}

	public User addUser(User obj) {
		Boolean existsEmail = userRepository.findByEmail(obj.getEmail());
		if (existsEmail) {
			throw new ResourceFoundException(
					"An user with this email " + obj.getEmail() + " has already been created.");
		}
		return userRepository.save(obj);
	}

	public void deleteUser(Long id) {
		if (!userRepository.existsById(id)) {
			throw new ResourceNotFoundException("User of id " + id + " not found.");
		}
		userRepository.deleteById(id);
	}

	public User updateUser(Long id, User obj) {
		if (!userRepository.existsById(id)) {
			throw new ResourceNotFoundException("User of id " + id + " not found.");
		}

		User user = userRepository.findById(id).get();

		user.setName(obj.getName());
		Boolean existsEmail = userRepository.findByEmail(obj.getEmail());
		if (existsEmail) {
			throw new ResourceFoundException(
					"An user with this email " + user.getEmail() + " has already been created.");
		}
		user.setEmail(obj.getEmail());
		user.setJobTitle(obj.getJobTitle());

		return userRepository.save(user);
	}

	public User addTaskToUser(Long idUser, Long idTask) {
		if (!userRepository.existsById(idUser)) {
			throw new ResourceNotFoundException("User of id " + idUser + " not found.");
		}
		User user = userRepository.findById(idUser).get();

		if (!taskRepository.existsById(idTask)) {
			throw new ResourceNotFoundException("Task of id " + idTask + " not found.");
		}
		Task task = taskRepository.findById(idTask).get();

		Boolean existsTask = user.getTasks().stream().anyMatch(t -> t.getId().equals(idTask));
		if (existsTask) {
			throw new ResourceFoundException("The task has already been add.");
		}

		user.getTasks().add(task);

		emailSenderService.sendEmail(user, task);

		return userRepository.save(user);
	}

	public void deleteTaskFromUser(Long idUser, Long idTask) {
		if (!userRepository.existsById(idUser)) {
			throw new ResourceNotFoundException("User of id " + idUser + " not found.");
		}
		User user = userRepository.findById(idUser).get();

		if (!taskRepository.existsById(idTask)) {
			throw new ResourceNotFoundException("Task of id " + idTask + " not found.");
		}
		Task task = taskRepository.findById(idTask).get();

		user.getTasks().remove(task);

		userRepository.save(user);
	}
}
