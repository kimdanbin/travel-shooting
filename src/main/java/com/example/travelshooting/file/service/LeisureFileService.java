package com.example.travelshooting.file.service;

import com.example.travelshooting.file.dto.FileResDto;
import com.example.travelshooting.file.entity.LeisureFile;
import com.example.travelshooting.file.repository.LeisureFileRepository;
import com.example.travelshooting.s3.S3Service;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class LeisureFileService {

    private final S3Service s3Service;
    private final LeisureFileRepository leisureFileRepository;

    // 나중에 서비스로 대체 예정
//    private final ProductRepository productRepository;

    // 파일 업로드
    @Transactional
    public List<FileResDto> uploadFile(Long leisureId, List<MultipartFile> files) {

//        Product product = productRepository.findById(leisureId)
//                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        List<FileResDto> savedFiles = new ArrayList<>();

        try {
            for (MultipartFile file : files) {

                String originalFilename = file.getOriginalFilename();
                String contentType = file.getContentType();
                String fileUrl = s3Service.uploadFile(file);

                // 서비스 만들어지면 교체
//                LeisureFile savedLeisureFile = leisureFileRepository.save(new LeisureFile(product, originalFilename, fileUrl, contentType));
                LeisureFile savedLeisureFile = leisureFileRepository.save(new LeisureFile(originalFilename, fileUrl, contentType));

                savedFiles.add(new FileResDto(savedLeisureFile));
            }

        } catch (Exception e) {
            // 만약 파일 저장에 실패하면 s3에 업로드된 파일들 삭제
            for (FileResDto file : savedFiles) {
                s3Service.deleteFile(file.getUrl());
            }
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }

        return savedFiles;
    }

    // 파일 삭제
    @Transactional
    public void deleteFile(Long leisureId, Long attachmentId) {
//        leisureRepository.findById(leisureId); 레저서비스 만들어지면 추가 예정
        leisureFileRepository.findById(attachmentId).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        leisureFileRepository.deleteById(attachmentId);
    }
}
