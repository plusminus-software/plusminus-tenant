package software.plusminus.tenant.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;
import software.plusminus.security.context.SecurityContext;

@Order(1)
@Component
public class SecurityContextTenantService implements TenantService {

    @Autowired
    private SecurityContext securityContext;
    
    @Nullable
    @Override
    public String currentTenant() {
        return securityContext.getParameter("tenant");
    }
}
