package com.kh.replay.global.s3;

import java.io.IOException;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetUrlRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

@Slf4j
@Service
@RequiredArgsConstructor
public class S3Service {

    private final S3Client s3Client;

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    public String uploadFile(MultipartFile multipartFile) throws IOException {
        String originalFileName = multipartFile.getOriginalFilename();
        
        // 1. 파일명 중복 방지 (UUID)
        String fileName = UUID.randomUUID() + "_" + originalFileName;

        // 2. 업로드 요청 객체 생성 (SDK v2 방식)
        PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                .bucket(bucket)
                .key(fileName)
                .contentType(multipartFile.getContentType())
                .contentLength(multipartFile.getSize())
                .build();

        // 3. S3에 업로드 (RequestBody 사용)
        s3Client.putObject(putObjectRequest, 
                RequestBody.fromInputStream(multipartFile.getInputStream(), multipartFile.getSize()));
        
        // 4. 업로드된 파일의 URL 가져오기
        return s3Client.utilities().getUrl(GetUrlRequest.builder()
                .bucket(bucket)
                .key(fileName)
                .build()).toExternalForm();
    }
}