package com.woory.backend.repository;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import com.woory.backend.dto.TopicDto;
import com.woory.backend.entity.Group;
import com.woory.backend.entity.TopicSet;

@SpringBootTest
@ActiveProfiles("test")
public class BatchTopicRepositoryTest {

	@Autowired
	private TopicRepository topicRepository;

	@Autowired
	private GroupRepository groupRepository;
	List<Group> groupIndex;

	@BeforeEach
	public void setUp() {
		List<Group> groups = new ArrayList<>();
		for (int i = 0; i < 100_000; i++) {
			groups.add(new Group());
		}
		long start = System.currentTimeMillis();
		groupIndex = groupRepository.saveAll(groups);
		long end = System.currentTimeMillis();

		System.out.println("10만건 단건 삽입(그룹) = " + (end - start));
	}

	@Test
	@DisplayName("배치 저장 테스트")
	void insertTopicToAllGroups() {
		// given
		TopicSet topicSet = new TopicSet(1L, "일상 공유", "가장 최근에 본 영화", 19);
		Date now = new Date();
		List<TopicDto> list = groupIndex.stream()
			.map(group ->
				TopicDto.fromTopicSetWithGroupIdAndDate(topicSet, now, group.getGroupId())
			).toList();
		long start = System.currentTimeMillis();
		// when
		int[] ints = topicRepository.saveAll(list);
		long end = System.currentTimeMillis();

		System.out.println("10만건 배치 삽입(토픽) = " + (end - start));
		long count = topicRepository.count();

		// then
		Assertions.assertEquals(groupIndex.size(), count);

	}
}
