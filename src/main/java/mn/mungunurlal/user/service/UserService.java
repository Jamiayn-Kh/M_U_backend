package mn.mungunurlal.user.service;

import mn.mungunurlal.user.domain.User;
import mn.mungunurlal.user.domain.UserRole;
import mn.mungunurlal.user.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
}