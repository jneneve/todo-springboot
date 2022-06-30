package com.jneneve.todo.config;

import java.time.Instant;
import java.util.Arrays;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import com.jneneve.todo.entities.Task;
import com.jneneve.todo.entities.User;
import com.jneneve.todo.entities.enums.TaskStatus;
import com.jneneve.todo.repositories.TaskRepository;
import com.jneneve.todo.repositories.UserRepository;

@Configuration
@Profile("test")
public class TestConfig implements CommandLineRunner {

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private TaskRepository taskRepository;

	@Override
	public void run(String... args) throws Exception {
		User u1 = new User(null, "Maria Brown", "maria@gmail.com", "Junior Developer");
		User u2 = new User(null, "Alex Green", "alex@gmail.com", "Senior Developer");

		userRepository.saveAll(Arrays.asList(u1, u2));

		Task t1 = new Task(null, "Make a call on Monday", "We need to do a call with the team", TaskStatus.IN_PROGRESS,
				Instant.parse("2022-06-21T16:00:07Z"), null);
		Task t2 = new Task(null, "Make a call on Saturday", "We need to do a call with the team",
				TaskStatus.NOT_STARTED, Instant.parse("2022-06-22T16:00:07Z"), null);
		Task t3 = new Task(null, "Make a call on Thursday", "We need to do a call with the team",
				TaskStatus.NOT_STARTED, Instant.parse("2022-06-23T16:00:07Z"), null);

		taskRepository.saveAll(Arrays.asList(t1, t2, t3));

		u1.getTasks().add(t1);
		u1.getTasks().add(t3);
		u2.getTasks().add(t2);

		userRepository.saveAll(Arrays.asList(u1, u2));
	}

}
