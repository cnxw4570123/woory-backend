package com.woory.backend.utils;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Base64;
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

public class Base64ImageConverter implements ImageConverter {
	private final String DATA_URI_PATTERN = "^data:image/(jpeg|jpg|png);base64,[A-Za-z0-9+/=]+$";
	private final Pattern PATTERN = Pattern.compile(DATA_URI_PATTERN);

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

		String fileWithoutHeader = image.split(",")[1];
		byte[] bytes = Base64.getDecoder().decode(fileWithoutHeader);

		if (bytes.length >= MAX_FILE_SIZE) {
			throw new CustomException(ErrorCode.FILE_SIZE_EXCEED);
		}

		validateExtension(getFileExtension(image));
		String name = generateFileName();

		try {
			ImmutableImage immutableImage = ImmutableImage.loader()
				.type(BufferedImage.TYPE_4BYTE_ABGR)
				.fromBytes(bytes);
			bytes = immutableImage.bound(MAX_PIXEL, MAX_PIXEL, ScaleMethod.Lanczos3)
				.bytes(WebpWriter.DEFAULT);
			return new MockMultipartFile(name, name, IMAGE_FORMAT_WEBP, bytes);

		} catch (IOException e) {
			throw new CustomException(ErrorCode.ERROR_SAVING_FILE);
		}
	}

	@Override
	public String getFileExtension(String image) {
		int slash = image.indexOf("/");
		int semicolon = image.indexOf(";");
		if (slash == -1 || semicolon == -1) {
			return "";
		}

		return image.substring(slash + 1, semicolon);
	}
}
