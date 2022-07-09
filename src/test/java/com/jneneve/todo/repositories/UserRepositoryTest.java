package com.jneneve.todo.repositories;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import com.jneneve.todo.entities.User;

@DataJpaTest
public class UserRepositoryTest {

	@Autowired
	private UserRepository underTest;

	@AfterEach
	public void tearDown() {
		underTest.deleteAll();
	}

	@Test
	@DisplayName("it should check when user email exists")
	public void checkIfEmailExists() {
		String email = "josue.neneve@hotmail.com";
		User user = new User(1L, "Josue Neneve", email, "Senior Develop");
		underTest.save(user);

		Boolean expected = underTest.findByEmail(email);

		assertThat(expected).isTrue();
	}

	@Test
	@DisplayName("it should check when user email does not exists")
	public void checkIfEmailDoesNotExists() {
		String email = "josue.neneve@hotmail.com";

		Boolean expected = underTest.findByEmail(email);

		assertThat(expected).isFalse();
	}
}
