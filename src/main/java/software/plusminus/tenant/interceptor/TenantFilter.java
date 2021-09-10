package software.plusminus.tenant.interceptor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import software.plusminus.hibernate.HibernateFilter;
import software.plusminus.tenant.service.TenantService;

import java.util.Collections;
import java.util.Map;

@Component
public class TenantFilter implements HibernateFilter {

    @Autowired
    private TenantService tenantService;

    @Override
    public String filterName() {
        return "tenantFilter";
    }

    @Override
    public Map<String, Object> parameters() {
        return Collections.singletonMap("tenant", tenantService.currentTenant());
    }
}
