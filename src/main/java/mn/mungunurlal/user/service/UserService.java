package mn.mungunurlal.user.service;

import mn.mungunurlal.user.domain.User;
import mn.mungunurlal.user.domain.UserRole;
import mn.mungunurlal.user.dto.CreateUserRequest;
import mn.mungunurlal.user.dto.UserResponse;
import mn.mungunurlal.user.exception.UsernameAlreadyExistsException;
import mn.mungunurlal.user.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder
    ) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public void createInitialAdmin(
            String username,
            String password,
            String fullName
    ) {
        if (userRepository.existsByUsername(username)) {
            return;
        }

        User admin = new User(
                username,
                passwordEncoder.encode(password),
                fullName,
                null,
                UserRole.ADMIN
        );

        userRepository.save(admin);
    }

    @Transactional
    public UserResponse createUser(CreateUserRequest request) {
        String username = request.username().trim();

        if (userRepository.existsByUsername(username)) {
            throw new UsernameAlreadyExistsException(username);
        }

        User user = new User(
                username,
                passwordEncoder.encode(request.password()),
                request.fullName().trim(),
                normalizePhone(request.phone()),
                request.role()
        );

        User savedUser = userRepository.save(user);

        return UserResponse.from(savedUser);
    }

    @Transactional(readOnly = true)
    public List<UserResponse> getAllUsers() {
        return userRepository.findAll()
                .stream()
                .map(UserResponse::from)
                .toList();
    }

    private String normalizePhone(String phone) {
        if (phone == null || phone.isBlank()) {
            return null;
        }

        return phone.trim();
    }
}