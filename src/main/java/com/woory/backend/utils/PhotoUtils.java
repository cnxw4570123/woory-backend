package com.woory.backend.utils;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Base64;
import java.util.Set;
import java.util.UUID;
import java.util.function.Function;

import org.apache.http.entity.ContentType;
import org.apache.http.util.TextUtils;
import org.apache.logging.log4j.util.Strings;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import com.sksamuel.scrimage.ImmutableImage;
import com.sksamuel.scrimage.ScaleMethod;
import com.sksamuel.scrimage.format.FormatDetector;
import com.sksamuel.scrimage.webp.WebpWriter;
import com.woory.backend.error.CustomException;
import com.woory.backend.error.ErrorCode;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class PhotoUtils {

	private static final long MAX_FILE_SIZE = 5 * 1024 * 1024;
	public static final String IMAGE_WEBP = "webp";
	public static final int MAX_PIXEL = 375;
	private static Set<String> allowedImageForm = Set.of("image/png", "image/jpeg", "image/jpg");

	private static String validateFileExtension(String images, Function<String, String> extractFileExtension) {

		String fileExtension = extractFileExtension.apply(images);
		// String fileExtension = getFileExtension(images);
		if (!allowedImageForm.contains(fileExtension)) {
			throw new CustomException(ErrorCode.FILE_IS_NOT_IMAGE);
		}
		return fileExtension.split("/")[1];
	}

	// // 파일 확장자 추출 메서드
	private static String getFileExtensionFromBase64(String base64File) {
		int colon = base64File.indexOf(":");
		int semicolon = base64File.indexOf(";");
		if (colon == -1 || semicolon == -1) {
			return "";
		}

		return base64File.substring(colon + 1, semicolon);
	}

	private static String getFileExtensionFromURL(String imagePath) {
		String[] split = imagePath.split("\\.");
		return "image/" + split[split.length - 1];
	}

	public static MultipartFile base64ToMultipartFile(String base64File) {
		if (TextUtils.isEmpty(base64File)) {
			return null;
		}

		String fileWithoutHeader = base64File.split(",")[1];

		byte[] bytes = Base64.getDecoder().decode(fileWithoutHeader);

		if (bytes.length > MAX_FILE_SIZE) {
			throw new CustomException(ErrorCode.FILE_SIZE_EXCEED);
		}
		// webp로 변환
		validateFileExtension(base64File, PhotoUtils::getFileExtensionFromBase64);

		String name = generateRandomFilename(IMAGE_WEBP);
		try {
			ImmutableImage immutableImage = ImmutableImage.loader().type(BufferedImage.TYPE_4BYTE_ABGR)
				.fromBytes(bytes);
			bytes = immutableImage.bound(MAX_PIXEL, MAX_PIXEL, ScaleMethod.FastScale)
				.bytes(WebpWriter.DEFAULT);
		} catch (IOException e) {
			throw new CustomException(ErrorCode.ERROR_SAVING_FILE);
		}

		return new MockMultipartFile(name, name, IMAGE_WEBP, bytes);
	}

	private static String generateRandomFilename(String extension) {
		return UUID.randomUUID() + "." + extension;
	}

	public static MultipartFile urlToFile(String image) {
		if (TextUtils.isEmpty(image)) {
			return null;
		}

		validateFileExtension(image, PhotoUtils::getFileExtensionFromURL);
		// webp 파일로 변환
		String fileName = generateRandomFilename(IMAGE_WEBP);
		try (InputStream is = new URL(image).openStream()) {
			byte[] bytes = is.readAllBytes();
			ImmutableImage immutableImage = ImmutableImage.loader().type(BufferedImage.TYPE_4BYTE_ABGR).fromBytes(bytes);
			bytes = immutableImage.bound(MAX_PIXEL, MAX_PIXEL, ScaleMethod.FastScale).bytes(WebpWriter.DEFAULT);
			return new MockMultipartFile(fileName, fileName, IMAGE_WEBP, bytes);

		} catch (IOException e) {
			throw new CustomException(ErrorCode.ERROR_SAVING_FILE);
		}
	}
}
