package software.plusminus.tenant;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import software.plusminus.context.Context;
import software.plusminus.security.Security;

@SpringBootApplication
public class TestApplication {
    public static void main(String[] args) {
        SpringApplication.run(TestApplication.class, args);
    }

    @Bean
    Context<Security> securityContext() {
        return Context.of(() -> Security.builder()
                .username("test-username")
                .build());
    }
}
