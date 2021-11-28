package software.plusminus.tenant.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;
import javax.annotation.PostConstruct;

@Primary
@Component
public class PrimaryTenantService implements TenantService {

    @Autowired
    private List<TenantService> services;

    @PostConstruct
    private void init() {
        services.remove(this);
    }

    @Nullable
    public String currentTenant() {
        return services.stream()
                .map(TenantService::currentTenant)
                .filter(Objects::nonNull)
                .findFirst()
                .orElse(null);
    }

}
