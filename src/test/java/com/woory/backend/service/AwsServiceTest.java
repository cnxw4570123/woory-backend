package com.woory.backend.service;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.UUID;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.BDDMockito;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.AmazonS3Exception;
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

	@ParameterizedTest(name = "타입 : {0}")
	@CsvSource(value = {
		"Base64 data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAQAAAAEACAIAAADTED8xAAADMElEQVR4nOzVwQnAIBQFQYXff81RUkQCOyDj1YOPnbXWPmeTRef+/3O/OyBjzh3CD95BfqICMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMO0TAAD//2Anhf4QtqobAAAAAElFTkSuQmCC",
		"URL https://upload.wikimedia.org/wikipedia/commons/thumb/9/95/Font_Awesome_5_brands_github.svg/220px-Font_Awesome_5_brands_github.svg.png"
	}, delimiter = ' ')
	void 이미지_저장_base64_테스트(String type, String image) {
		assertThat(awsService.saveImage(image)).contains(
			"http://127.0.0.1:8081/" + BUCKET_NAME);
	}

	@ParameterizedTest(name = "빈문자열")
	@ValueSource(strings = {"", ""})
	void 빈_문자열로_이미지_저장시_null(String image) {
		assertThat(awsService.saveImage(image)).isNull();
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
		UUID filename = UUID.randomUUID();
		try (MockedStatic<UUID> uuid = Mockito.mockStatic(UUID.class)) {
			BDDMockito.given(UUID.randomUUID()).willReturn(filename);
			String url = "https://upload.wikimedia.org/wikipedia/commons/thumb/9/95/Font_Awesome_5_brands_github.svg/220px-Font_Awesome_5_brands_github.svg.png";

			String filePath = awsService.saveImage(url);
			String bucketPath = BUCKET_NAME + "/test", key = filename + ".webp";
			assertThat(amazonS3.getObject(bucketPath, key)).isNotNull();

			awsService.deleteImage(filePath);
			assertThatThrownBy(
				() -> amazonS3.getObject(bucketPath, key)
			)
				.isInstanceOf(AmazonS3Exception.class)
				.hasMessageContaining("The resource you requested does not exist");
		}

	}

	@Test
	void 큰_이미지_업로드_시_FILE_SIZE_EXCEPTION_발생() {
		assertThatThrownBy(
			() -> {
				try (FileInputStream fileInputStream = new FileInputStream(
					"src/test/resources/Base64_Encoded_Image_6.3MB.txt")) {
					String image = new String(fileInputStream.readAllBytes());
					awsService.saveImage(image);
				} catch (IOException e) {
				}
			}
		).isInstanceOf(CustomException.class)
			.hasMessage(ErrorCode.FILE_SIZE_EXCEED.getMessage());
	}
}
