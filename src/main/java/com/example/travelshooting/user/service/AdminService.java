package com.example.travelshooting.user.service;

import com.example.travelshooting.enums.UserRole;
import com.example.travelshooting.file.service.S3Service;
import com.example.travelshooting.user.dto.UserResDto;
import com.example.travelshooting.user.entity.User;
import com.example.travelshooting.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AdminService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final S3Service s3Service;

    @Transactional
    public UserResDto adminSignup(String email, String password, String name, MultipartFile file) {
        Optional<User> optionalUser = userRepository.findByEmail(email);

        if (optionalUser.isPresent()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "이미 존재하는 이메일입니다.");
        }

        String encodedPassword = passwordEncoder.encode(password);
        String fileUrl = "";

        if (file != null) {
            try {
                fileUrl = s3Service.uploadFile(file);
            } catch (Exception e) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
            }
        }

        User user = new User(email, encodedPassword, name, UserRole.ADMIN, fileUrl);
        User savedUser = userRepository.save(user);

        return new UserResDto(
                savedUser.getId(),
                savedUser.getEmail(),
                savedUser.getName(),
                savedUser.getRole(),
                savedUser.getImageUrl()
        );
    }
}
