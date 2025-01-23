package com.example.travelshooting.file.controller;

import com.example.travelshooting.common.CommonListResDto;
import com.example.travelshooting.file.dto.FileResDto;
import com.example.travelshooting.file.service.LeisureFileService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/leisure/{leisureId}/attachments")
@RequiredArgsConstructor
public class LeisureFileController {

    private final LeisureFileService leisureFileService;

    // 레저/티켓 파일 업로드
    @PostMapping
    public ResponseEntity<CommonListResDto<FileResDto>> uploadFile(
            @PathVariable Long leisureId,
            @RequestParam List<MultipartFile> files
    ) {

        List<FileResDto> fileResDtos = leisureFileService.uploadFile(leisureId, files);

        return new ResponseEntity<>(new CommonListResDto<>("파일 업로드 완료", fileResDtos), HttpStatus.OK);
    }

    // 레저/티켓 파일 삭제
    @DeleteMapping("/{attachmentId}")
    public ResponseEntity<Void> deleteFile(
            @PathVariable Long leisureId,
            @PathVariable Long attachmentId
    ) {

        leisureFileService.deleteFile(leisureId, attachmentId);

        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}