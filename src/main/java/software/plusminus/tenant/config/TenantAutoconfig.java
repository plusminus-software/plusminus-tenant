package software.plusminus.tenant.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@ComponentScan({"software.plusminus.tenant", "software.plusminus.security", "software.plusminus.jwt"})
@Configuration
public class TenantAutoconfig {
}
