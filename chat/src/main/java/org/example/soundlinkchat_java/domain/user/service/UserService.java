package org.example.soundlinkchat_java.domain.user.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.example.soundlinkchat_java.domain.user.entity.User;
import org.example.soundlinkchat_java.domain.user.repository.UserRepository;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public User getUserDataWithoutRedis(Long userId) {
        ObjectMapper objectMapper = new ObjectMapper();

        return userRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    // RequestUserId, ResponseUserId, Msg, CreatedAt
    // -> ChatRoomId, RequestUserId, Msg, CreatedAt
}
