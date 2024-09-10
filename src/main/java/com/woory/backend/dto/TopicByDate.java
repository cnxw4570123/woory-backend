package com.woory.backend.dto;

import java.text.ParseException;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;

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
		this.date = ZonedDateTime.now(ZoneId.of("Asia/Seoul")).toLocalDate();
	}

	public String toString() {
		return topicIndex + " " + date.toString();
	}
}
