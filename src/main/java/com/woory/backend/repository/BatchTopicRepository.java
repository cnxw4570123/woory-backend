package com.woory.backend.repository;

import java.util.List;

import com.woory.backend.dto.TopicDto;

public interface BatchTopicRepository {
	public int[] saveAll(List<TopicDto> topics);
}
