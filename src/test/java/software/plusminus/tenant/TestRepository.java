package software.plusminus.tenant;

import org.springframework.context.annotation.Profile;
import org.springframework.data.repository.PagingAndSortingRepository;

@Profile("test")
public interface TestRepository extends PagingAndSortingRepository<TestEntity, Long> {
}