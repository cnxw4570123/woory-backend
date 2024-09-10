package com.woory.backend.entity;

public enum GroupStatus {
	MEMBER(1),
	GROUP_LEADER(3);

	private final int value;

	GroupStatus(int value) {
		this.value = value;
	}

	public int getValue() {
		return value;
	}

	public static GroupStatus fromValue(int value) {
		for (GroupStatus status : values()) {
			if (status.value == value) {
				return status;
			}
		}
		throw new IllegalArgumentException("Unknown value: " + value);
	}
}
