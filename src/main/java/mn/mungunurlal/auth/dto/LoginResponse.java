package mn.mungunurlal.auth.dto;

import mn.mungunurlal.user.domain.UserRole;

public record LoginResponse(
        String token,
        UserInfo user
) {
    public record UserInfo(
            Long id,
            String username,
            String fullName,
            UserRole role
    ) {
    }
}