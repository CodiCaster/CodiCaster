package com.ll.codicaster.boundedContext.aws.s3.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AmazonS3ImageDto {

	private String cdnUrl;
	private String originUrl;


}
