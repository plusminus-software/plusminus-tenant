package software.plusminus.tenant.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
    void page() {
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

    @Test
    void byId() {
        TestEntity entity = new TestEntity();
        entity.setMyField("first");
        entity.setTenant("firstTenant");
        repository.save(entity);
        when(firstProvider.currentTenant()).thenReturn("firstTenant");

        TestEntity response = restTemplate.getForObject(
                url() + "/test/" + entity.getId(),
                TestEntity.class
        );

        check(response).isNotNull();
        check(response.getId()).is(1L);
        check(response.getTenant()).is("firstTenant");
        check(response.getMyField()).is("first");
    }

    @Test
    void notFound() {
        TestEntity entity = new TestEntity();
        entity.setMyField("first");
        entity.setTenant("firstTenant");
        repository.save(entity);
        when(firstProvider.currentTenant()).thenReturn("secondTenant");

        ResponseEntity<TestEntity> response = restTemplate.getForEntity(
                url() + "/test/" + entity.getId(),
                TestEntity.class
        );

        check(response.getStatusCode()).is(HttpStatus.NOT_FOUND);
    }
}
