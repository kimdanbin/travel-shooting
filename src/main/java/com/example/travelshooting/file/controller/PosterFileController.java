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
@RequestMapping("/posters/{posterId}/attachments")
public class PosterFileController {

    private final PosterFileService posterFileService;

    // 파일 업로드
    @PostMapping
    public ResponseEntity<CommonListResDto<FileResDto>> uploadFile(
            @PathVariable Long posterId,
            @RequestParam List<MultipartFile> files
    ) {

        List<FileResDto> fileResDtos = posterFileService.uploadFile(posterId, files);

        return new ResponseEntity<>(new CommonListResDto<>("파일 업로드 완료", fileResDtos), HttpStatus.OK);
    }

    // 파일 삭제
    @DeleteMapping("/{attachmentId}")
    public ResponseEntity<Void> deleteFile(
            @PathVariable Long posterId,
            @PathVariable Long attachmentId
    ) {

        posterFileService.deleteFile(posterId, attachmentId);

        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
