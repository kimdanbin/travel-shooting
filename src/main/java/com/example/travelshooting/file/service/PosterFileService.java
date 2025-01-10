package com.example.travelshooting.file.service;

import com.example.travelshooting.file.dto.FileResDto;
import com.example.travelshooting.file.entity.PosterFile;
import com.example.travelshooting.file.repository.PosterFileRepository;
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
public class PosterFileService {

    private final S3Service s3Service;
    private final PosterFileRepository posterFileRepository;
    // 주석은 포스터 서비스 만들어지면 추가 예정

    // 파일 업로드
    @Transactional
    public List<FileResDto> uploadFile(Long posterId, List<MultipartFile> files) {

        List<FileResDto> savedFiles = new ArrayList<>();

        try {
            for (MultipartFile file : files) {

                String originalFilename = file.getOriginalFilename();
                String contentType = file.getContentType();
                String fileUrl = s3Service.uploadFile(file);

//                PosterFile savedPosterFile = posterFileRepository.save(new PosterFile(poster, originalFilename, fileUrl, contentType));
                PosterFile savedPosterFile = posterFileRepository.save(new PosterFile(originalFilename, fileUrl, contentType));

                savedFiles.add(new FileResDto(savedPosterFile));
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
    public void deleteFile(Long posterId, Long attachmentId) {
//        posterRepository.findById(posterId); 포스터 서비스 만들어지면 추가 예정
        posterFileRepository.findById(attachmentId).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        posterFileRepository.deleteById(attachmentId);
    }

    // 최신 shorts 5개 조회
    public List<FileResDto> getNewFiveShorts() {

        List<PosterFile> newFiveShorts = posterFileRepository.getNewFiveShorts();
        List<FileResDto> newFiveShortsDtos = new ArrayList<>();

        for (PosterFile posterFile : newFiveShorts) {
            newFiveShortsDtos.add(new FileResDto(posterFile));
        }

        return newFiveShortsDtos;
    }
}
