package school.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import school.entity.SchoolEntity;

public interface SchoolRepository extends JpaRepository<SchoolEntity, Long> {
}
