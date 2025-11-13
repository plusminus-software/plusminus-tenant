package software.plusminus.tenant.service;

import lombok.AllArgsConstructor;
import org.springframework.core.annotation.Order;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;
import software.plusminus.context.Context;
import software.plusminus.security.Security;

@Order(1)
@AllArgsConstructor
@Component
public class SecurityContextTenantService implements TenantProvider {

    private Context<Security> securityContext;
    
    @Nullable
    @Override
    public String currentTenant() {
        return securityContext.optional()
                .map(value -> value.getOthers().get("tenant"))
                .orElse(null);
    }
}
