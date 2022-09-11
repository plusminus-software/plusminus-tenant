package software.plusminus.tenant.util;

import lombok.experimental.UtilityClass;
import org.hibernate.Filter;
import org.hibernate.Session;

import java.util.concurrent.Callable;
import javax.persistence.EntityManager;

@UtilityClass
public class TenantUtils {

    public void runWithTenant(EntityManager entityManager, String tenant, Runnable runnable) {
        callWithTenant(entityManager, tenant, () -> {
            runnable.run();
            return null;
        });
    }

    @SuppressWarnings({"PMD.CloseResource", "squid:S00112"})
    public <T> T callWithTenant(EntityManager entityManager, String tenant, Callable<T> callable) {
        try {
            enableTenantFilter(entityManager, tenant);
            return callable.call();
        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            disableTenantFilter(entityManager);
        }
    }

    @SuppressWarnings("PMD.CloseResource")
    public void enableTenantFilter(EntityManager entityManager, String tenant) {
        Session session = entityManager.unwrap(Session.class);
        Filter filter = session.enableFilter("tenantFilter");
        filter.setParameter("tenant", tenant);
    }

    @SuppressWarnings("PMD.CloseResource")
    public void disableTenantFilter(EntityManager entityManager) {
        Session session = entityManager.unwrap(Session.class);
        session.disableFilter("tenantFilter");
    }
}
