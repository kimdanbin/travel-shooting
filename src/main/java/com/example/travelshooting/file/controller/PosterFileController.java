package com.example.travelshooting.file.controller;

import com.example.travelshooting.common.CommonListResDto;
import com.example.travelshooting.file.dto.FileResDto;
import com.example.travelshooting.file.service.PosterFileService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class PosterFileController {

    private final PosterFileService posterFileService;

    // 파일 업로드
    @PostMapping("/posters/{posterId}/attachments")
    public ResponseEntity<CommonListResDto<FileResDto>> uploadFile(
            @PathVariable Long posterId,
            @RequestParam List<MultipartFile> files
    ) {

        List<FileResDto> fileResDtos = posterFileService.uploadFile(posterId, files);

        return new ResponseEntity<>(new CommonListResDto<>("파일 업로드 완료", fileResDtos), HttpStatus.OK);
    }

    // 파일 삭제
    @DeleteMapping("/posters/{posterId}/attachments/{attachmentId}")
    public ResponseEntity<Void> deleteFile(
            @PathVariable Long posterId,
            @PathVariable Long attachmentId
    ) {

        posterFileService.deleteFile(posterId, attachmentId);

        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    // 최신 shorts 5개 조회
    @GetMapping("/attachments")
    public ResponseEntity<CommonListResDto<FileResDto>> getNewFiveShorts() {

        List<FileResDto> newFiveShorts = posterFileService.getNewFiveShorts();

        return new ResponseEntity<>(new CommonListResDto<>("최신 shorts 5개 조회 완료", newFiveShorts), HttpStatus.OK);
    }
}