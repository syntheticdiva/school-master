package school.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import school.dto.SubscriberDto;
import school.entity.SubscriberEntity;

import java.util.List;

public interface SubscriberRepository extends JpaRepository<SubscriberEntity, Long> {
    List<SubscriberEntity> findByEntity(String entity);


}
