package software.plusminus.tenant.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import software.plusminus.tenant.fixtures.TestEntity;
import software.plusminus.tenant.fixtures.TestRepository;
import software.plusminus.test.IntegrationTest;
import software.plusminus.test.util.TestRestTemplate;

import static org.mockito.Mockito.when;
import static software.plusminus.check.Checks.check;

class TenantFilterTest extends IntegrationTest {

    @Autowired
    private TenantProvider firstProvider;
    @Autowired
    private TestRepository repository;
    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    void filteredByTenant() {
        TestEntity entity1 = new TestEntity();
        entity1.setMyField("first");
        entity1.setTenant("firstTenant");
        repository.save(entity1);
        TestEntity entity2 = new TestEntity();
        entity2.setMyField("second");
        entity2.setTenant("secondTenant");
        repository.save(entity2);
        when(firstProvider.currentTenant()).thenReturn("firstTenant");

        Page<TestEntity> page = restTemplate.getPage(url() + "/test", TestEntity.class);

        check(page).isNotNull();
        check(page.getTotalElements()).is(1L);
        check(page.getContent().get(0).getMyField()).is("first");
    }
}
