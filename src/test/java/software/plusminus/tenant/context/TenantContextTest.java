package software.plusminus.tenant.context;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import software.plusminus.tenant.exception.TenantException;
import software.plusminus.tenant.model.TenantAction;
import software.plusminus.tenant.service.TenantService;

import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class TenantContextTest {

    @Mock
    private TenantService tenantService;
    @InjectMocks
    private TenantContext tenantContext;

    @Test
    public void accessAllowed() {
        String tenant = "my tenant";
        when(tenantService.currentTenant()).thenReturn(tenant);

        tenantContext.checkAccess(TenantAction.READ, tenant);
    }

    @Test(expected = TenantException.class)
    public void accessDenied() {
        tenantContext.checkAccess(TenantAction.READ, "some unknown tenant");
    }

    @Test(expected = TenantException.class)
    public void enable() {
        tenantContext.disable();
        tenantContext.enable();
        tenantContext.checkAccess(TenantAction.READ, "some unknown tenant");
    }

    @Test
    public void disable() {
        tenantContext.disable();
        tenantContext.checkAccess(TenantAction.READ, "some unknown tenant");
    }
}