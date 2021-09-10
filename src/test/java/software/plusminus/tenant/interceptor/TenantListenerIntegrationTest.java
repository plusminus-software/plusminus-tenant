package software.plusminus.tenant.interceptor;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import software.plusminus.check.util.JsonUtils;
import software.plusminus.tenant.TestEntity;
import software.plusminus.tenant.TransactionalService;
import software.plusminus.tenant.exception.TenantException;
import software.plusminus.tenant.service.TenantService;

import javax.persistence.EntityManager;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class TenantListenerIntegrationTest {

    @Autowired
    private EntityManager entityManager;
    @Autowired
    private TransactionalService transactionalService;

    @MockBean
    private TenantService tenantService;

    @Test
    public void onRead_WithNullTenant_DoesntPopulateTenant() {
        TestEntity entity = readTestEntity();
        entity.setId(null);
        entity.setTenant(null);
        persist(entity);
        when(tenantService.currentTenant()).thenReturn(null, "TestTenant");

        TestEntity read = entityManager.find(TestEntity.class, entity.getId());

        assertThat(read.getTenant()).isNull();
    }
    
    @Test
    public void onCreate_WithNullTenant() {
        TestEntity entity = readTestEntity();
        entity.setId(null);
        entity.setTenant(null);
        when(tenantService.currentTenant()).thenReturn("TestTenant");
        
        persist(entity);
        
        assertThat(entity.getId()).isNotNull();
        assertThat(entity.getTenant()).isEqualTo("TestTenant");
    }

    @Test
    public void onCreate_WithAllowedTenant() {
        TestEntity entity = readTestEntity();
        entity.setId(null);
        entity.setTenant("AllowedTenant");
        when(tenantService.currentTenant()).thenReturn("AllowedTenant");

        persist(entity);

        assertThat(entity.getId()).isNotNull();
        assertThat(entity.getTenant()).isEqualTo("AllowedTenant");
    }

    @Test(expected = TenantException.class)
    public void onCreate_WithNotAllowedTenant() {
        TestEntity entity = readTestEntity();
        entity.setId(null);
        entity.setTenant("TestTenant");
        when(tenantService.currentTenant()).thenReturn("SomeOtherTenant");

        persist(entity);
    }
    
    @Test
    public void onDelete_WithAllowedTenant() {
        TestEntity entity = createTestEntity();
        
        remove(entity);
        
        assertThat(entityManager.find(TestEntity.class, entity.getId())).isNull();
    }
    
    @Test(expected = TenantException.class)
    public void onDelete_WithNotAllowedTenant() {
        TestEntity entity = createTestEntity();
        when(tenantService.currentTenant()).thenReturn("SomeOtherTenant");
        
        remove(entity);
    }

    private TestEntity readTestEntity() {
        return JsonUtils.fromJson("/json/test-entity.json", TestEntity.class);
    }
    
    private TestEntity createTestEntity() {
        TestEntity entity = readTestEntity();
        entity.setId(null);
        entity.setTenant("TestTenant");
        when(tenantService.currentTenant()).thenReturn("TestTenant");
        persist(entity);
        return entity;
    }
    
    private void persist(TestEntity entity) {
        transactionalService.inTransaction(() -> entityManager.persist(entity));
    }
    
    private void remove(TestEntity entity) {
        transactionalService.inTransaction(() -> {
            TestEntity toRemove = entityManager.find(TestEntity.class, entity.getId());
            entityManager.remove(toRemove);
        });
    }
    
}