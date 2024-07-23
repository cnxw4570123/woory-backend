package com.woory.backend.utils;

import java.io.File;

import com.woory.backend.error.CustomException;
import com.woory.backend.error.ErrorCode;

public class PhotoUtils {

	public static String validateFileExtension(String originalFilename) {
		String fileExtension = getFileExtension(originalFilename);
		if (!fileExtension.equalsIgnoreCase("png") && !fileExtension.equalsIgnoreCase("jpg")) {
			throw new CustomException(ErrorCode.FILE_IS_NOT_IMAGE);
		}
		return fileExtension;
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
