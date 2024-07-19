package com.woory.backend.repository2;

import java.util.List;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSourceUtils;
import org.springframework.stereotype.Repository;

import com.woory.backend.dto.TopicDto;

@Repository
public class BatchTopicRepositoryImpl implements BatchTopicRepository{

	private NamedParameterJdbcTemplate jdbcTemplate;

	@Autowired
	public BatchTopicRepositoryImpl(DataSource dataSource) {
		this.jdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
	}

	@Override
	public int[] saveAll(List<TopicDto> topics) {
		return jdbcTemplate.batchUpdate(
			"INSERT INTO TOPIC(topic_content, issue_date, group_id, topic_byte) VALUES(:topicContent, :issueDate, :groupId, :topicByte)",
			SqlParameterSourceUtils.createBatch(topics)
		);
	}
}
