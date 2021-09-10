package software.plusminus.tenant.service;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class PrimaryTenantServiceTest {
    
    @Spy
    private List<TenantService> providers = new ArrayList<>();
    @InjectMocks
    private PrimaryTenantService primaryTenantProvider;
    
    @Before
    public void setUp() {
        providers.clear();
    }
    
    @Test
    public void currentTenant() {
        TenantService provider1 = mock(TenantService.class);
        TenantService provider2 = mock(TenantService.class);
        TenantService provider3 = mock(TenantService.class);
        when(provider2.currentTenant()).thenReturn("my-tenant");
        providers.add(provider1);
        providers.add(provider2);
        providers.add(provider3);
        
        String result = primaryTenantProvider.currentTenant();
        
        assertThat(result).isEqualTo("my-tenant");
        verify(provider1).currentTenant();
        verify(provider2).currentTenant();
        verify(provider3, never()).currentTenant();
    }

    @Test
    public void currentTenant_ReturnsNull_IfAllProvidersReturnNull() {
        TenantService provider1 = mock(TenantService.class);
        TenantService provider2 = mock(TenantService.class);
        TenantService provider3 = mock(TenantService.class);
        providers.add(provider1);
        providers.add(provider2);
        providers.add(provider3);

        String result = primaryTenantProvider.currentTenant();

        assertThat(result).isNull();
        verify(provider1).currentTenant();
        verify(provider2).currentTenant();
        verify(provider3).currentTenant();
    }
    
    @Test
    public void currentTenant_ReturnsNull_IfNoProviders() {
        String result = primaryTenantProvider.currentTenant();
        assertThat(result).isNull();
    }

}