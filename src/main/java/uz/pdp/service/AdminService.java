package uz.pdp.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uz.pdp.dto.UserResponseDto;
import uz.pdp.entity.UserEntity;
import uz.pdp.repository.UserRepository;

import java.util.ArrayList;
import java.util.List;

@Service
public class AdminService {

    private final UserRepository userRepository;

    public AdminService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public List<UserResponseDto> getAllUsers() {
        List<UserEntity> users = userRepository.findAll();
        List<UserResponseDto> result = new ArrayList<>();

        for (UserEntity u : users) {
            UserResponseDto dto = new UserResponseDto();
            dto.setId(u.getId());
            dto.setUsername(u.getUsername());
            dto.setEmail(u.getEmail());
            dto.setRole(u.getRole().name());
            result.add(dto);
        }
        return result;
    }
}
