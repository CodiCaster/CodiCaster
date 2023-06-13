// package com.ll.codicaster.boundedContext.aws.s3.service;
//
//
// import lombok.RequiredArgsConstructor;
// import org.springframework.stereotype.Service;
// import org.springframework.web.multipart.MultipartFile;
//
// import com.ll.codicaster.boundedContext.aws.s3.dto.AmazonS3ImageDto;
// import com.ll.codicaster.boundedContext.aws.s3.properties.AmazonS3Properties;
// import com.ll.codicaster.boundedContext.aws.s3.repository.AmazonS3Repository;
// import com.ll.codicaster.utils.MimeTypeUtils;
//
// @Service
// @RequiredArgsConstructor
// public class AmazonS3Service {
//
//     private final static String IMAGE_FOLDER_NAME = "i/";
//
//     private final AmazonS3Properties amazonS3Properties;
//
//     private final AmazonS3Repository amazonRepository;
//
//     public AmazonS3ImageDto imageUpload(MultipartFile file, String name) {
//
//         String mimeType = MimeTypeUtils.getMimeType(file);
//
//         if (!MimeTypeUtils.isImage(mimeType)) {
//             throw new IllegalArgumentException("잘못된 경로입니다.");
//         }
//
//         String fileExtension = MimeTypeUtils.extractFileExtension(mimeType);
//
//         String objectName = IMAGE_FOLDER_NAME + name + "." + fileExtension;
//
//         amazonRepository.upload(amazonS3Properties.getBucketName(), objectName, file, mimeType);
//
//         String cndUrl = amazonS3Properties.getCdnEndPoint() + objectName + "?type=u&w=1080&h=1350&quality=90";
//         String originUrl = amazonS3Properties.getEndPoint() + "/" + amazonS3Properties.getBucketName() + "/" + objectName;
//
//         return AmazonS3ImageDto.builder()
//             .cdnUrl(cndUrl)
//             .originUrl(originUrl)
//             .build();
//
//     }
// }
