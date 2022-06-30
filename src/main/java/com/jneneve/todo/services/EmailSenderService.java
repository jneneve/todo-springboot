package com.jneneve.todo.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import com.jneneve.todo.entities.Task;
import com.jneneve.todo.entities.User;

@Service
public class EmailSenderService {

	@Autowired
	private JavaMailSender mailSender;

	public void sendEmail(User user, Task task) {
		SimpleMailMessage message = new SimpleMailMessage();

		String subject = "New task add to you.";
		String body = "Task: " + task.getTitle() + "\n" + "Description: " + task.getDescription() + "\n";

		message.setFrom("wilfat.qboa@gmail.com");
		message.setTo(user.getEmail());
		message.setText(body);
		message.setSubject(subject);

		mailSender.send(message);
	}
}
