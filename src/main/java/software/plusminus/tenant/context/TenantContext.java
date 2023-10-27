package software.plusminus.tenant.context;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;
import software.plusminus.tenant.exception.TenantException;
import software.plusminus.tenant.model.TenantAction;
import software.plusminus.tenant.service.TenantService;

@Component
public class TenantContext {

    @Autowired
    private TenantService tenantService;
    @SuppressWarnings("java:S5164")
    private final ThreadLocal<Boolean> enabled = ThreadLocal.withInitial(() -> true);

    public void checkAccess(TenantAction action, @Nullable String tenant) {
        if (Boolean.FALSE.equals(enabled.get())) {
            return;
        }
        boolean hasAccess = ObjectUtils.nullSafeEquals(tenantService.currentTenant(), tenant);
        if (!hasAccess) {
            throw new TenantException("User has no " + action + " access for tenant " + tenant);
        }
    }

    public void enable() {
        enabled.set(true);
    }

    public void disable() {
        enabled.set(false);
    }
}
