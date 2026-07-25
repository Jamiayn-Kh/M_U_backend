package mn.mungunurlal.config;

import mn.mungunurlal.user.service.UserService;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

@Configuration
public class AdminInitializer {

    @Bean
    ApplicationRunner initializeAdmin(
            UserService userService,
            Environment environment
    ) {
        return args -> {
            String username = environment.getProperty("app.admin.username");
            String password = environment.getProperty("app.admin.password");
            String fullName = environment.getProperty(
                    "app.admin.full-name",
                    "System Admin"
            );

            if (username == null || password == null) {
                return;
            }

            userService.createInitialAdmin(
                    username,
                    password,
                    fullName
            );
        };
    }
}