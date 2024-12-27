package com.woory.backend.utils;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.http.util.TextUtils;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import com.sksamuel.scrimage.ImmutableImage;
import com.sksamuel.scrimage.ScaleMethod;
import com.sksamuel.scrimage.webp.WebpWriter;
import com.woory.backend.error.CustomException;
import com.woory.backend.error.ErrorCode;

public class LinkImageConverter implements ImageConverter {
	private final String URI_PATTERN = "^https?://[^\\s/$.?#].\\S*.(png|jpeg|jpg)$";
	private final Pattern PATTERN = Pattern.compile(URI_PATTERN);

	@Override
	public boolean supports(String image) {
		Matcher m = PATTERN.matcher(image);
		return m.matches();
	}

	@Override
	public MultipartFile convert(String image) {
		if (TextUtils.isEmpty(image)) {
			return null;
		}

		validateExtension(getFileExtension(image));
		// webp 파일로 변환
		String fileName = generateFileName();
		try (InputStream is = new URL(image).openStream()) {
			byte[] bytes = is.readAllBytes();
			ImmutableImage immutableImage = ImmutableImage.loader()
				.type(BufferedImage.TYPE_4BYTE_ABGR)
				.fromBytes(bytes);
			bytes = immutableImage.bound(MAX_PIXEL, MAX_PIXEL, ScaleMethod.Lanczos3)
				.bytes(WebpWriter.DEFAULT);
			return new MockMultipartFile(fileName, fileName, IMAGE_FORMAT_WEBP, bytes);

		} catch (IOException e) {
			throw new CustomException(ErrorCode.ERROR_SAVING_FILE);
		}
	}

	@Override
	public String getFileExtension(String image) {
		String[] split = image.split("\\.");
		return split[split.length - 1];
	}

}
