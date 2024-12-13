package com.woory.backend.service;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import com.amazonaws.services.s3.AmazonS3;
import com.woory.backend.error.CustomException;
import com.woory.backend.error.ErrorCode;

import io.findify.s3mock.S3Mock;

@ActiveProfiles("test")
@SpringBootTest(classes = {AwsService.class, S3MockConfig.class})
public class AwsServiceTest {
	@Autowired
	private AmazonS3 amazonS3;

	@Autowired
	private S3Mock s3Mock;

	@Autowired
	private AwsService awsService = new AwsService(amazonS3);

	private static final String BUCKET_NAME = "test-bucket";

	@BeforeAll
	static void setUp(@Autowired S3Mock s3Mock, @Autowired AmazonS3 amazonS3) {
		s3Mock.start();
		amazonS3.createBucket(BUCKET_NAME);
	}

	@AfterAll
	static void destroy(@Autowired S3Mock s3Mock, @Autowired AmazonS3 amazonS3) {
		amazonS3.shutdown();
		s3Mock.stop();
	}

	@Test
	void 이미지_저장_base64_테스트() {
		String image =
			"data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAQAAAAEACAIAAADTED8xAAADMElEQVR4nOzVwQnAIBQFQYXff81RUkQCOyDj1YOPnbXWPmeTRef+/3O/OyBjzh3CD95BfqICMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMO0TAAD//2Anhf4QtqobAAAAAElFTkSuQmCC";

		assertThat(awsService.saveFile(image)).contains(
			"http://127.0.0.1:8081/" + BUCKET_NAME);
	}

	@Test
	void 이미지_저장_url_테스트() {
		String url = "https://upload.wikimedia.org/wikipedia/commons/thumb/9/95/Font_Awesome_5_brands_github.svg/220px-Font_Awesome_5_brands_github.svg.png";
		assertThat(awsService.saveFileFromUrl(url)).contains(
			"http://127.0.0.1:8081/" + BUCKET_NAME);
	}

	@Test
	void 빈_문자열로_이미지_저장시_null() {
		String url = "";
		String base64Image = "";

		assertAll(() -> assertThat(awsService.saveFileFromUrl(url)).isNull(),
			() -> assertThat(awsService.saveFile(base64Image)).isNull());
	}

	@Test
	void 이미지_없을_때_삭제_테스트() {
		String url = "http://127.0.0.1:8081/test/220px-Font_Awesome_5_brands_github.svg.png";
		assertThatThrownBy(() -> awsService.deleteImage(url))
			.isInstanceOf(CustomException.class)
			.hasMessageContaining(ErrorCode.FILE_DOES_NOT_EXIST.getMessage());
	}

	@Test
	void 이미지_업로드_후_삭제_테스트() {
		String url = "https://upload.wikimedia.org/wikipedia/commons/thumb/9/95/Font_Awesome_5_brands_github.svg/220px-Font_Awesome_5_brands_github.svg.png";

		String filePath = awsService.saveFileFromUrl(url);
		assertDoesNotThrow(() -> awsService.deleteImage(filePath));
	}
}
