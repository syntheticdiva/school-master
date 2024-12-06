package school.mapper;

import org.mapstruct.Mapper;
import school.dto.SubscriberDto;
import school.entity.SubscriberEntity;

@Mapper(componentModel = "spring")
public interface SubscriberMapper {
    SubscriberDto toDto(SubscriberEntity entity);
    SubscriberEntity toEntity(SubscriberDto dto);

}
