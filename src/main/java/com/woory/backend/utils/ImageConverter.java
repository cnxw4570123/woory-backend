package com.woory.backend.utils;

import java.util.Set;
import java.util.UUID;

import org.springframework.web.multipart.MultipartFile;

import com.woory.backend.error.CustomException;
import com.woory.backend.error.ErrorCode;

public interface ImageConverter {
	static final String IMAGE_FORMAT_WEBP = "webp";
	static final long MAX_FILE_SIZE = 5 * 1024 * 1024;
	static final int MAX_PIXEL = 375;
	static final Set<String> ALLOWED_IMAGE_FORM = Set.of("png", "jpeg", "jpg");

	default void validateExtension(String extension) {
		if (!ALLOWED_IMAGE_FORM.contains(extension)) {
			throw new CustomException(ErrorCode.FILE_IS_NOT_IMAGE);
		}
	}

	default String generateFileName() {
		return UUID.randomUUID() + "." + IMAGE_FORMAT_WEBP;
	}

	boolean supports(String image);

	MultipartFile convert(String image);

	String getFileExtension(String image);
}
