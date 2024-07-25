package com.woory.backend.repository;

import java.util.List;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSourceUtils;
import org.springframework.stereotype.Repository;

import com.woory.backend.dto.TopicDto;

@Repository
public class BatchTopicRepositoryImpl implements BatchTopicRepository{

	private static final Logger log = LoggerFactory.getLogger(BatchTopicRepositoryImpl.class);
	private NamedParameterJdbcTemplate jdbcTemplate;

	@Autowired
	public BatchTopicRepositoryImpl(DataSource dataSource) {
		this.jdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
	}

	@Override
	public int[] saveAll(List<TopicDto> topics) {
		log.info("토픽 배치 저장 실행");
		return jdbcTemplate.batchUpdate(
			"INSERT INTO TOPIC(topic_content, issue_date, group_id, topic_byte) VALUES(:topicContent, :issueDate, :groupId, :topicByte)",
			SqlParameterSourceUtils.createBatch(topics)
		);
	}
}
