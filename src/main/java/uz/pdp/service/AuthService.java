package uz.pdp.service;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import uz.pdp.dto.AuthResponseDto;
import uz.pdp.dto.LoginDto;
import uz.pdp.dto.RegisterDto;
import uz.pdp.entity.Role;
import uz.pdp.entity.UserEntity;
import uz.pdp.repository.UserRepository;
import uz.pdp.security.JwtService;

@Service

public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtService jwtService, AuthenticationManager authenticationManager) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.authenticationManager = authenticationManager;
    }

    public String register(RegisterDto dto) {
        if (dto.getUsername() == null || dto.getUsername().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Username bo'sh bo'lmasligi kerak");
        }
        if (dto.getEmail() == null || !dto.getEmail().contains("@")) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Email noto'g'ri");
        }
        if (dto.getPassword() == null || dto.getPassword().length() < 3) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Parol kamida 3 ta belgi bo'lishi kerak");
        }
        if (userRepository.existsByUsername(dto.getUsername()) || userRepository.existsByEmail(dto.getEmail())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Username yoki email allaqachon band");
        }

        UserEntity user = new UserEntity();
        user.setUsername(dto.getUsername());
        user.setEmail(dto.getEmail());
        user.setPassword(passwordEncoder.encode(dto.getPassword()));
        user.setRole(Role.ROLE_USER);

        userRepository.save(user);
        return "Muvaffaqiyatli ro'yxatdan o'tdingiz";
    }

    public AuthResponseDto login(LoginDto dto) {
        if (dto.getUsername() == null || dto.getPassword() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Login va parol kiritilishi shart");
        }
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(dto.getUsername(), dto.getPassword())
            );
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Login yoki parol noto'g'ri");
        }

        UserEntity user = userRepository.findByUsername(dto.getUsername()).orElseThrow();
        String token = jwtService.generateToken(user);
        return new AuthResponseDto(token);
    }

}
