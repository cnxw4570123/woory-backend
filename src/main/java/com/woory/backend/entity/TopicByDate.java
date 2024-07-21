package com.woory.backend.entity;

import java.text.ParseException;
import java.time.LocalDate;

import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
public class TopicByDate {

	private int topicIndex;
	private LocalDate date;

	public TopicByDate(String row) throws ParseException {
		String[] idxAndDate = row.split(" ");
		this.topicIndex = Integer.parseInt(idxAndDate[0]);
		this.date = LocalDate.parse(idxAndDate[1]);
	}

	public TopicByDate(int index) {
		this.topicIndex = index;
		this.date = LocalDate.now();
	}

	public String toString() {
		return topicIndex + " " + date.toString();
	}
}
