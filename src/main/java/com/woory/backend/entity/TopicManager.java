package com.woory.backend.entity;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.text.ParseException;
import java.time.LocalDate;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

@Component
public class TopicManager {
	private static final Random randomTopic = new Random(System.currentTimeMillis());
	private static final Path TOPIC_PATH = Paths.get("src/main/resources/topics.txt");
	private static final Integer TOPIC_COUNT = 80;
	private static Deque<TopicByDate> topicOfLast7Days = init();

	private TopicManager() {
	}

	private static Deque<TopicByDate> init() {
		List<TopicByDate> topics;
		try {
			topics = Files.readAllLines(TOPIC_PATH).stream()
				.filter(s -> !s.isEmpty())
				.map(s -> {
					try {
						return new TopicByDate(s);
					} catch (ParseException e) {
						throw new RuntimeException(e);
					}
				})
				.toList();
		} catch (IOException e) {
			topics = new ArrayList<>();
		}

		LocalDate now = LocalDate.now();

		Deque<TopicByDate> topicIndex = new ArrayDeque<>(topics);
		if (topicIndex.isEmpty() || topicIndex.peekLast().getDate().isBefore(now)) {
			// 만약 7개 이상 가지고 있으면 6개만 남김
			while (topicIndex.size() >= 7) {
				topicIndex.poll();
			}

			topicIndex.add(new TopicByDate(randomTopic.nextInt(TOPIC_COUNT)));
		}

		try {
			Files.writeString(TOPIC_PATH, topicIndex.stream().map(String::valueOf).collect(Collectors.joining("\n")),
				StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING, StandardOpenOption.WRITE,
				StandardOpenOption.SYNC);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		return topicIndex;
	}

	public static int getTopicOfToday() {
		return topicOfLast7Days.peekLast().getTopicIndex();
	}

	public static int pollTopicOfToday() {
		if (topicOfLast7Days.size() >= 7) {
			topicOfLast7Days.poll();
		}
		int topic;
		do {
			topic = randomTopic.nextInt(TOPIC_COUNT);
		} while (topicOfLast7Days.contains(topic));

		topicOfLast7Days.add(new TopicByDate(topic));
		writeTopicsAsFile();
		return topic;
	}

	private static void writeTopicsAsFile() {
		try {
			Files.writeString(TOPIC_PATH,
				topicOfLast7Days.stream().map(String::valueOf).collect(Collectors.joining("\n")),
				StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING, StandardOpenOption.WRITE,
				StandardOpenOption.SYNC);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
}
