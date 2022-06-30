package com.jneneve.todo.entities.enums;

public enum TaskStatus {

	NOT_STARTED(1), IN_PROGRESS(2), WAITING(3), COMPLETED(4), CANCELLED(5);
	
	private int code;
	
	private TaskStatus(int code) {
		this.code = code;
	}

	public int getCode() {
		return code;
	}
	
	public static TaskStatus valueOf(int code) {
		for (TaskStatus value : TaskStatus.values()) {
			if (value.getCode() == code) {
				return value;
			}
		}
		throw new IllegalArgumentException("Invalid TaskStatus code.");
	}
}
