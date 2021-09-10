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
    private List<TenantService> providers;

    @PostConstruct
    private void init() {
        providers.remove(this);
    }

    @Nullable
    public String currentTenant() {
        return providers.stream()
                .map(TenantService::currentTenant)
                .filter(Objects::nonNull)
                .findFirst()
                .orElse(null);
    }

}
