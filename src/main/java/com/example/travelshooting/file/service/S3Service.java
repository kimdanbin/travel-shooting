package com.example.travelshooting.file.service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class S3Service {

    private final AmazonS3 amazonS3;

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    // 파일 업로드
    @Transactional
    public String uploadFile(MultipartFile multipartFile) throws IOException {
        String originalFilename = multipartFile.getOriginalFilename();
        String uniqueFilename = UUID.randomUUID() + "_" + originalFilename;

        ObjectMetadata metadata = new ObjectMetadata(); // S3에 저장될 파일의 메타데이터를 설정하기 위한 객체
        metadata.setContentLength(multipartFile.getSize()); // 파일의 크기를 설정
        metadata.setContentType(multipartFile.getContentType()); // 파일의 타입을 설정

        amazonS3.putObject(bucket, uniqueFilename, multipartFile.getInputStream(), metadata);

        return amazonS3.getUrl(bucket, uniqueFilename).toString();
    }

    @Transactional
    public void deleteFile(String fileUrl) {
        amazonS3.deleteObject(bucket, fileUrl);
    }
}