package software.plusminus.tenant.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import software.plusminus.context.Context;
import software.plusminus.tenant.service.TenantProvider;

import java.util.List;
import java.util.Objects;

@ComponentScan("software.plusminus.tenant")
@Configuration
public class TenantAutoconfig {

    @Bean
    Context<String> tenantContext(List<TenantProvider> providers) {
        return Context.of(() -> providers.stream()
                .map(TenantProvider::currentTenant)
                .filter(Objects::nonNull)
                .findFirst()
                .orElse(null));
    }
}
