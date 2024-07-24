package com.woory.backend.utils;

import java.io.File;
import java.util.Base64;
import java.util.Set;
import java.util.UUID;

import org.apache.http.util.TextUtils;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import com.woory.backend.error.CustomException;
import com.woory.backend.error.ErrorCode;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class PhotoUtils {

	private static final long MAX_FILE_SIZE = 5 * 1024 * 1024;
	private static Set<String> allowedImageForm = Set.of("image/png", "image/jpeg", "image/jpg");

	private static String validateFileExtension(String base64File) {
		String fileExtension = getFileExtension(base64File);
		if (!allowedImageForm.contains(fileExtension)) {
			throw new CustomException(ErrorCode.FILE_IS_NOT_IMAGE);
		}
		return fileExtension.split("/")[1];
	}

	// // 파일 확장자 추출 메서드
	private static String getFileExtension(String base64File) {
		int colon = base64File.indexOf(":");
		int semicolon = base64File.indexOf(";");
		if (colon == -1 || semicolon == -1) {
			return "";
		}

		return base64File.substring(colon + 1, semicolon);
	}

	public static MultipartFile base64ToMultipartFile(String base64File) {
		if(TextUtils.isEmpty(base64File)){
			return null;
		}

		String fileWithoutHeader = base64File.split(",")[1];

		byte[] bytes = Base64.getDecoder().decode(fileWithoutHeader);

		if(bytes.length > MAX_FILE_SIZE){
			throw new CustomException(ErrorCode.FILE_SIZE_EXCEED);
		}

		String extension = validateFileExtension(base64File);

		String name = generateRandomFilename(extension);

		return new MockMultipartFile(name, name, extension, bytes);
	}

	private static String generateRandomFilename(String extension) {
		return UUID.randomUUID() + "." + extension;
	}
}
