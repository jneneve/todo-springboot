package com.jneneve.todo.services;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
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
import com.jneneve.todo.repositories.UserRepository;
import com.jneneve.todo.services.exceptions.ResourceFoundException;
import com.jneneve.todo.services.exceptions.ResourceNotFoundException;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

	@Mock
	private UserRepository userRepository;

	@Mock
	private TaskRepository taskRepostiory;

	private UserService underTest;

	@Mock
	private EmailSenderService emailSenderService;

	private static final long idUser = 1L;
	private static final long idTask = 1L;

	private User user;
	private Task task;

	@BeforeEach
	public void setUp() {
		underTest = new UserService(userRepository, taskRepostiory, emailSenderService);

		user = new User(idUser, "Josue Neneve", "josue.neneve@hotmail.com", "Senior Developer");
		task = new Task(idTask, "Make a call on Saturday", "We need to do a call with the team", TaskStatus.NOT_STARTED,
				Instant.parse("2022-06-22T16:00:07Z"), null);
	}

	@Test
	@DisplayName("it should get all users")
	public void canGetAllUsers() {
		underTest.getAllUsers();
		verify(userRepository).findAll();
	}

	@Test
	@DisplayName("it should get one user")
	public void canGetOneUser() {
		User expected = user;
		given(userRepository.existsById(idUser)).willReturn(true);
		given(userRepository.findById(idUser)).willReturn(Optional.of(expected));

		underTest.getUser(idUser);

		verify(userRepository).findById(idUser);
	}

	@Test
	@DisplayName("it should not get one user that was not created")
	public void canNotGetOneUserThatWasNotCreated() {
		given(userRepository.existsById(idUser)).willReturn(false);

		assertThatThrownBy(() -> underTest.getUser(idUser)).isInstanceOf(ResourceNotFoundException.class)
				.hasMessageContaining("User of id " + idUser + " not found.");
		verify(userRepository, never()).findById(any());
	}

	@Test
	@DisplayName("it should add one user")
	public void canAddOneUser() {
		underTest.addUser(user);

		ArgumentCaptor<User> userArgumentCaptor = ArgumentCaptor.forClass(User.class);
		verify(userRepository).save(userArgumentCaptor.capture());
		User capturedUser = userArgumentCaptor.getValue();
		assertThat(capturedUser).isEqualTo(user);
	}

	@Test
	@DisplayName("it should not add the user with an email has already used")
	public void canNotAddTheUserWithAnEmailHasAlreadyUsed() {
		given(userRepository.findByEmail(anyString())).willReturn(true);

		assertThatThrownBy(() -> underTest.addUser(user)).isInstanceOf(ResourceFoundException.class)
				.hasMessageContaining("An user with this email " + user.getEmail() + " has already been created.");
		verify(userRepository, never()).save(any());
	}

	@Test
	@DisplayName("it should delete one user")
	public void canDeleteOneUser() {
		given(userRepository.existsById(idUser)).willReturn(true);

		underTest.deleteUser(idUser);

		verify(userRepository).deleteById(idUser);
	}

	@Test
	@DisplayName("it should not delete one user that was not created")
	public void canNotDeleteOneUserThatWasNotCreated() {
		given(userRepository.existsById(idUser)).willReturn(false);

		assertThatThrownBy(() -> underTest.deleteUser(idUser)).isInstanceOf(ResourceNotFoundException.class)
				.hasMessageContaining("User of id " + idUser + " not found.");
		verify(userRepository, never()).deleteById(any());
	}

	@Test
	@DisplayName("it should update one user")
	public void canUpdateOneUser() {
		User request = new User(idUser, "Josue Neneve", "josueneneve@hotmail.com", "Senior Developer");
		given(userRepository.existsById(idUser)).willReturn(true);
		given(userRepository.findById(idUser)).willReturn(Optional.of(user));

		underTest.updateUser(idUser, request);

		ArgumentCaptor<User> requestArgumentCaptor = ArgumentCaptor.forClass(User.class);
		verify(userRepository).save(requestArgumentCaptor.capture());
		User capturedUser = requestArgumentCaptor.getValue();
		assertThat(capturedUser).isEqualTo(request);
	}

	@Test
	@DisplayName("it should not update one user that was not created")
	public void canNotUpdateOneUserThatWasNotCreated() {
		given(userRepository.existsById(idUser)).willReturn(false);

		assertThatThrownBy(() -> underTest.updateUser(idUser, user)).isInstanceOf(ResourceNotFoundException.class)
				.hasMessageContaining("User of id " + idUser + " not found.");
		verify(userRepository, never()).save(any());
	}

	@Test
	@DisplayName("it should not update email from user to an email has already used")
	public void canNotUpdateEmailFromUserToAnEmailHasAlreadyUsed() {
		given(userRepository.existsById(idUser)).willReturn(true);
		given(userRepository.findById(idUser)).willReturn(Optional.of(user));
		given(userRepository.findByEmail(anyString())).willReturn(true);

		assertThatThrownBy(() -> underTest.updateUser(idUser, user)).isInstanceOf(ResourceFoundException.class)
				.hasMessageContaining("An user with this email " + user.getEmail() + " has already been created.");
		verify(userRepository, never()).save(any());
	}

	@Test
	@DisplayName("it should add task to the user")
	public void canAddTaskToTheUser() {
		given(userRepository.existsById(idUser)).willReturn(true);
		given(userRepository.findById(idUser)).willReturn(Optional.of(user));
		given(taskRepostiory.existsById(idTask)).willReturn(true);
		given(taskRepostiory.findById(idTask)).willReturn(Optional.of(task));

		underTest.addTaskToUser(idUser, idTask);

		assertThat(user.getTasksQuantity()).isEqualTo(1);
		verify(userRepository).save(user);
	}

	@Test
	@DisplayName("it should not add task to the user that was not created")
	public void canNotAddTaskToTheUserThatWasNotCreated() {
		given(userRepository.existsById(idUser)).willReturn(false);

		assertThatThrownBy(() -> underTest.addTaskToUser(idUser, idTask)).isInstanceOf(ResourceNotFoundException.class)
				.hasMessageContaining("User of id " + idUser + " not found.");
		verify(userRepository, never()).save(any());
	}

	@Test
	@DisplayName("it should not add task that does not exist to the user")
	public void canNotAddTaskThatDoesNotExistToTheUser() {
		given(userRepository.existsById(idUser)).willReturn(true);
		given(userRepository.findById(idUser)).willReturn(Optional.of(user));
		given(taskRepostiory.existsById(idTask)).willReturn(false);

		assertThatThrownBy(() -> underTest.addTaskToUser(idUser, idTask)).isInstanceOf(ResourceNotFoundException.class)
				.hasMessageContaining("Task of id " + idTask + " not found.");
		verify(userRepository, never()).save(any());
	}

	@Test
	@DisplayName("it should send email when add task to the user")
	public void canSendEmailWhenAddTaskToTheUser() {
		given(userRepository.existsById(idUser)).willReturn(true);
		given(userRepository.findById(idUser)).willReturn(Optional.of(user));
		given(taskRepostiory.existsById(idTask)).willReturn(true);
		given(taskRepostiory.findById(idTask)).willReturn(Optional.of(task));

		underTest.addTaskToUser(idUser, idTask);

		verify(emailSenderService).sendEmail(user, task);
	}

	@Test
	@DisplayName("it should not add task that has already existed in the user")
	public void canNotAddTaskThatHasAlreadyExistedInTheUser() {
		given(userRepository.existsById(idUser)).willReturn(true);
		given(userRepository.findById(idUser)).willReturn(Optional.of(user));
		given(taskRepostiory.existsById(idTask)).willReturn(true);
		given(taskRepostiory.findById(idTask)).willReturn(Optional.of(task));

		underTest.addTaskToUser(idUser, idTask);

		assertThatThrownBy(() -> underTest.addTaskToUser(idUser, idTask)).isInstanceOf(ResourceFoundException.class)
				.hasMessageContaining("The task has already been add.");
		assertThat(user.getTasksQuantity()).isEqualTo(1);
		verify(emailSenderService).sendEmail(user, task);
		verify(userRepository).save(user);
	}

	@Test
	@DisplayName("it should delete one task from the user")
	public void canDeleteOneTask() {
		given(userRepository.existsById(idUser)).willReturn(true);
		given(userRepository.findById(idUser)).willReturn(Optional.of(user));
		given(taskRepostiory.existsById(idTask)).willReturn(true);
		given(taskRepostiory.findById(idTask)).willReturn(Optional.of(task));

		underTest.addTaskToUser(idUser, idTask);
		underTest.deleteTaskFromUser(idUser, idTask);

		assertThat(user.getTasksQuantity()).isEqualTo(0);
		verify(userRepository, times(2)).save(user);
	}

	@Test
	@DisplayName("it should not delete task from the user that was not created")
	public void canNotDeleteTaskFromTheUserThatWasNotCreated() {
		given(userRepository.existsById(idUser)).willReturn(false);

		assertThatThrownBy(() -> underTest.deleteTaskFromUser(idUser, idTask))
				.isInstanceOf(ResourceNotFoundException.class)
				.hasMessageContaining("User of id " + idUser + " not found.");
		verify(userRepository, never()).save(any());
	}

	@Test
	@DisplayName("it should not delete task that does not exist from the user")
	public void canNotDeleteTaskThatDoesNotExistCreatedFromTheUser() {
		given(userRepository.existsById(idUser)).willReturn(true);
		given(userRepository.findById(idUser)).willReturn(Optional.of(user));
		given(taskRepostiory.existsById(idTask)).willReturn(false);

		assertThatThrownBy(() -> underTest.deleteTaskFromUser(idUser, idTask))
				.isInstanceOf(ResourceNotFoundException.class)
				.hasMessageContaining("Task of id " + idTask + " not found.");
		verify(userRepository, never()).save(any());
	}
}
