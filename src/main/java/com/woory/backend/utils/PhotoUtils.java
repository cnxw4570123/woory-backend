package com.woory.backend.utils;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

import org.springframework.web.multipart.MultipartFile;

import com.woory.backend.error.CustomException;
import com.woory.backend.error.ErrorCode;

public class PhotoUtils {

	private static final String folderPath = new File("src/main/resources/images/").getAbsolutePath() + "/";

	public static String handlePhoto(MultipartFile groupPhoto) throws IOException {
		if (groupPhoto != null && !groupPhoto.isEmpty()) {
			return savePhoto(groupPhoto);
		}
		return null;
	}

	private static String savePhoto(MultipartFile photo) throws IOException {

		// 확장자 체크
		String originalFilename = photo.getOriginalFilename();
		if (originalFilename == null) {
			throw new CustomException(ErrorCode.INVALID_FILE_NAME);
		}

		String fileExtension = getFileExtension(originalFilename);
		if (!fileExtension.equalsIgnoreCase("png") && !fileExtension.equalsIgnoreCase("jpg")) {
			throw new CustomException(ErrorCode.FILE_IS_NOT_IMAGE);
		}

		String fileName = UUID.randomUUID() + "_" + originalFilename;
		File file = new File(folderPath + fileName);
		photo.transferTo(file); // 파일 저장

		return file.getAbsolutePath(); // 사진 경로 반환
	}

	// 파일 확장자 추출 메서드
	private static String getFileExtension(String filename) {
		int lastIndexOfDot = filename.lastIndexOf('.');
		if (lastIndexOfDot == -1) {
			return ""; // 확장자가 없는 경우 빈 문자열 반환
		}
		return filename.substring(lastIndexOfDot + 1);
	}
}
