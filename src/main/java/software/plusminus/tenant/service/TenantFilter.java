package software.plusminus.tenant.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import software.plusminus.context.Context;
import software.plusminus.hibernate.HibernateFilter;

import java.util.Collections;
import java.util.Map;

@AllArgsConstructor
@Component
public class TenantFilter implements HibernateFilter {

    private Context<String> tenantContext;

    @Override
    public String filterName() {
        return "tenantFilter";
    }

    @Override
    public Map<String, Object> parameters() {
        String tenant = tenantContext.get();
        return Collections.singletonMap("tenant", tenant == null ? "" : tenant);
    }
}
