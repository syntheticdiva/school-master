package school.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import school.dto.SubscriberDto;
import school.entity.SubscriberEntity;

@Mapper(componentModel = "spring")
public interface SubscriberMapper {
    @Mapping(target = "id", source = "id")
    SubscriberDto toDto(SubscriberEntity entity);

    @Mapping(target = "id", source = "id")
    SubscriberEntity toEntity(SubscriberDto dto);

}
