package com.woory.backend.utils;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import com.woory.backend.error.CustomException;
import com.woory.backend.error.ErrorCode;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class PhotoUtils {

	private static final List<ImageConverter> converters = List.of(new LinkImageConverter(),
		new Base64ImageConverter());

	public static MultipartFile convertImage(String image) {
		for (ImageConverter converter : converters) {
			if (!converter.supports(image)) {
				continue;
			}
			return converter.convert(image);
		}
		throw new CustomException(ErrorCode.FILE_IS_NOT_IMAGE);
	}
}
