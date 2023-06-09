package com.ll.codicaster.service;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.util.Assert;
import org.springframework.web.multipart.MultipartFile;

import com.ll.codicaster.utils.MimeTypeUtils;

public class AmazonS3ServiceTest {

	@Test
	public void testIsImage() throws IOException {
		File imageFile = new File("C:\\testImage\\KakaoTalk_20230530_102400599.jpg"); // 이미지 파일 경로
		File notImageFile = new File("C:\\testImage\\service1.pem"); // 이미지가 아닌 파일 경로

		MultipartFile multipartImageFile = new MockMultipartFile("test_image", new FileInputStream(imageFile));
		MultipartFile multipartNotImageFile = new MockMultipartFile("test_not_image", new FileInputStream(notImageFile));

		String imageMimeType = MimeTypeUtils.getMimeType(multipartImageFile);
		String notImageMimeType = MimeTypeUtils.getMimeType(multipartNotImageFile);

		Assert.isTrue(MimeTypeUtils.isImage(imageMimeType), "Failed: The file was an image but the method returned false.");
		Assert.isTrue(!MimeTypeUtils.isImage(notImageMimeType), "Failed: The file was not an image but the method returned true.");
	}
}
