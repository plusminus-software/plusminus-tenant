package software.plusminus.tenant.service;

import org.springframework.lang.Nullable;

public interface TenantService {
    
    @Nullable
    String currentTenant();
    
}
