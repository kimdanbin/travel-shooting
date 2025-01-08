package com.example.travelshooting.part.service;

import com.example.travelshooting.part.Part;
import com.example.travelshooting.part.repository.PartRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PartService {

    private final PartRepository partRepository;

    public Part getPartById(Long partId) {
        return partRepository.findById(partId)
                .orElseThrow(() -> new IllegalArgumentException("아이디 " + partId + "에 해당하는 레저/티켓 일정을 찾을 수 없습니다."));
    }
}
