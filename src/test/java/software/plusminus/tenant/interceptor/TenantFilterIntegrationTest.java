package software.plusminus.tenant.interceptor;

import org.hamcrest.core.Is;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import software.plusminus.check.util.JsonUtils;
import software.plusminus.jwt.service.JwtAuthenticationService;
import software.plusminus.security.Security;
import software.plusminus.tenant.TestEntity;
import software.plusminus.tenant.TransactionalService;
import software.plusminus.tenant.service.TenantService;

import javax.persistence.EntityManager;
import javax.servlet.http.Cookie;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class TenantFilterIntegrationTest {

    @Autowired
    private MockMvc mvc;
    @Autowired
    private JwtAuthenticationService authenticationService;
    @Autowired
    private TransactionalService transactionalService;
    @Autowired
    private EntityManager entityManager;

    @MockBean
    private TenantService tenantService;

    private TestEntity entity1;
    private TestEntity entity2;
    private TestEntity entityWithUnknownTenant;
    private TestEntity entityWithNullTenant;
    private String cookie;

    @Before
    public void before() {
        entity1 = readTestEntity();
        entity1.setId(null);
        entity1.setTenant("localhost");

        entity2 = readTestEntity();
        entity2.setId(null);
        entity2.setTenant("localhost");

        entityWithUnknownTenant = readTestEntity();
        entityWithUnknownTenant.setId(null);
        entityWithUnknownTenant.setTenant("Unknown tenant");

        entityWithNullTenant = readTestEntity();
        entityWithNullTenant.setId(null);
        entityWithNullTenant.setTenant(null);
        
        transactionalService.inTransaction(() -> {
            when(tenantService.currentTenant()).thenReturn("localhost");
            entityManager.persist(entity1);
            entityManager.persist(entity2);
            when(tenantService.currentTenant()).thenReturn("Unknown tenant");
            entityManager.persist(entityWithUnknownTenant);
            when(tenantService.currentTenant()).thenReturn(null);
            entityManager.persist(entityWithNullTenant);
        });

        cookie = authenticationService.provideToken(Security.builder().username("test-user").build());
    }

    @Test
    public void filteredByTest() throws Exception {
        when(tenantService.currentTenant()).thenReturn("localhost");
        
        mvc.perform(get("/test?page=0&size=100")
                .cookie(new Cookie("JWT-TOKEN", cookie)))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.content.length()", Is.is(2)))
                .andExpect(jsonPath("$.content[0].id", Is.is(1)))
                .andExpect(jsonPath("$.content[1].id", Is.is(2)));
    }
    
    private TestEntity readTestEntity() {
        return JsonUtils.fromJson("/json/test-entity.json", TestEntity.class);
    }

}