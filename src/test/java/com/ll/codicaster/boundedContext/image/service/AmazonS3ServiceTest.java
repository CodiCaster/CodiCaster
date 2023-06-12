package com.ll.codicaster.boundedContext.image.service;

import static org.mockito.Mockito.*;

import org.junit.jupiter.api.BeforeEach;
import org.mockito.Mock;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.mockito.InjectMocks;
import org.springframework.util.Assert;
import org.springframework.web.multipart.MultipartFile;

import com.ll.codicaster.aws.s3.dto.AmazonS3ImageDto;
import com.ll.codicaster.aws.s3.properties.AmazonS3Properties;
import com.ll.codicaster.aws.s3.repository.AmazonS3Repository;
import com.ll.codicaster.aws.s3.service.AmazonS3Service;
import com.ll.codicaster.utils.MimeTypeUtils;

@ExtendWith(SpringExtension.class)
@SpringBootTest
public class AmazonS3ServiceTest {

	@Mock
	private AmazonS3Repository amazonS3Repository;


	@Mock
	private AmazonS3Properties amazonS3Properties;
	@InjectMocks
	private AmazonS3Service amazonS3Service;

	@BeforeEach
	public void setUp() {
		// Assume a bucket name for test
		String testBucketName = "test-bucket";
		when(amazonS3Properties.getBucketName()).thenReturn(testBucketName);
	}


	@Test
	@DisplayName("이미지 업로드")
	public void testImageUpload() throws IOException {

		File imageFile = new File("C:\\testImage\\KakaoTalk_20230530_102400599.jpg");
		MultipartFile multipartImageFile = new MockMultipartFile("test_image", new FileInputStream(imageFile));


		when(amazonS3Repository.upload(any(), any(), any(), any())).thenReturn(null);

		AmazonS3ImageDto result = amazonS3Service.imageUpload(multipartImageFile, "test_image");

		Assert.notNull(result, "null이 될 수 없습니다.");
	}


	@Test
	@DisplayName("이미지 타입 외의 파일을 업로드 하려고 할때 제한")
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
