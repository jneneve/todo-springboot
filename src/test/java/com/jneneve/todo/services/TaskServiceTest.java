package com.jneneve.todo.services;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import java.time.Instant;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.jneneve.todo.entities.Task;
import com.jneneve.todo.entities.User;
import com.jneneve.todo.entities.enums.TaskStatus;
import com.jneneve.todo.repositories.TaskRepository;
import com.jneneve.todo.services.exceptions.ResourceFoundException;
import com.jneneve.todo.services.exceptions.ResourceNotFoundException;

@ExtendWith(MockitoExtension.class)
public class TaskServiceTest {

	@Mock
	private TaskRepository taskRepository;

	private TaskService underTest;
	
	private static final long idTask = 1L;
	private static final long idUser = 1L;

	private Task task;
	private User user;

	@BeforeEach
	public void setUp() {
		underTest = new TaskService(taskRepository);

		task = new Task(idTask, "Make a call on Saturday", "We need to do a call with the team", TaskStatus.NOT_STARTED,
				Instant.parse("2022-06-22T16:00:07Z"), null);
		user = new User(idUser, "Josue Neneve", "josue.neneve@hotmail.com", "Senior Developer");
	}

	@Test
	@DisplayName("it should get all tasks")
	public void canGetAllTasks() {
		underTest.getAllTasks();
		verify(taskRepository).findAll();
	}

	@Test
	@DisplayName("it should get one task")
	public void canGetOneTask() {
		Task expected = task;
		given(taskRepository.existsById(idTask)).willReturn(true);
		given(taskRepository.findById(idTask)).willReturn(Optional.of(expected));

		underTest.getTask(idTask);

		verify(taskRepository).findById(idTask);
	}

	@Test
	@DisplayName("it should not get one task that was not created")
	public void canNotGetOneTaskThatWasNotCreated() {
		given(taskRepository.existsById(idTask)).willReturn(false);

		assertThatThrownBy(() -> underTest.getTask(idTask)).isInstanceOf(ResourceNotFoundException.class)
				.hasMessageContaining("Task of id " + idTask + " not found.");
		verify(taskRepository, never()).findById(any());
	}

	@Test
	@DisplayName("it should add one task")
	public void canAddOneTask() {
		underTest.addTask(task);

		ArgumentCaptor<Task> taskArgumentCaptor = ArgumentCaptor.forClass(Task.class);
		verify(taskRepository).save(taskArgumentCaptor.capture());
		Task capturedTask = taskArgumentCaptor.getValue();
		assertThat(capturedTask).isEqualTo(task);
	}

	@Test
	@DisplayName("it should delete one task")
	public void canDeleteOneTask() {
		given(taskRepository.existsById(idTask)).willReturn(true);
		given(taskRepository.findById(idTask)).willReturn(Optional.of(task));

		underTest.deleteTask(idTask);

		verify(taskRepository).deleteById(idTask);
	}

	@Test
	@DisplayName("it should not delete one task that was not created")
	public void canNotDeleteOneTaskThatWasNotCreated() {
		given(taskRepository.existsById(idTask)).willReturn(false);

		assertThatThrownBy(() -> underTest.deleteTask(idTask)).isInstanceOf(ResourceNotFoundException.class)
				.hasMessageContaining("Task of id " + idTask + " not found.");
		verify(taskRepository, never()).deleteById(any());
	}

	@Test
	@DisplayName("it should not delete one task that is associated with many users")
	public void canNotDeleteOneTaskThatIsAssosciatedWithManyUsers() {
		task.getUsers().add(user);
		given(taskRepository.existsById(idTask)).willReturn(true);
		given(taskRepository.findById(idTask)).willReturn(Optional.of(task));
		
		assertThatThrownBy(() -> underTest.deleteTask(idTask)).isInstanceOf(ResourceFoundException.class)
				.hasMessageContaining("Task of id " + idTask + " is associated with one or more users.");
		verify(taskRepository, never()).deleteById(any());
	}

	@Test
	@DisplayName("it should update one task")
	public void canUpdateOneTask() {
		Task request = new Task(idTask, "Make a call on Saturday", "We need to do a call with the team",
				TaskStatus.NOT_STARTED, Instant.parse("2022-06-22T16:00:07Z"), null);
		given(taskRepository.existsById(idTask)).willReturn(true);
		given(taskRepository.findById(idTask)).willReturn(Optional.of(task));

		underTest.updateTask(idTask, request);

		ArgumentCaptor<Task> requestArgumentCaptor = ArgumentCaptor.forClass(Task.class);
		verify(taskRepository).save(requestArgumentCaptor.capture());
		Task capturedTask = requestArgumentCaptor.getValue();
		assertThat(capturedTask).isEqualTo(request);
	}

	@Test
	@DisplayName("it should not update one task that was not created")
	public void canNotUpdateOneTaskThatWasNotCreated() {
		given(taskRepository.existsById(idTask)).willReturn(false);

		assertThatThrownBy(() -> underTest.updateTask(idTask, task)).isInstanceOf(ResourceNotFoundException.class)
				.hasMessageContaining("Task of id " + idTask + " not found.");
		verify(taskRepository, never()).save(any());
	}
}
