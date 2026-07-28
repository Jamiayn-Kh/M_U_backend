package mn.mungunurlal.user.dto;

import mn.mungunurlal.user.domain.User;
import mn.mungunurlal.user.domain.UserRole;

import java.time.LocalDateTime;

public record UserResponse(
        Long id,
        String username,
        String fullName,
        String phone,
        UserRole role,
        boolean active,
        LocalDateTime createdAt
) {

    public static UserResponse from(User user) {
        return new UserResponse(
                user.getId(),
                user.getUsername(),
                user.getFullName(),
                user.getPhone(),
                user.getRole(),
                user.isActive(),
                user.getCreatedAt()
        );
    }
}