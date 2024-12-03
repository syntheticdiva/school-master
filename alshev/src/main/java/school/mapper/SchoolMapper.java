package school.mapper;

import org.mapstruct.*;
import school.dto.SchoolCreateDTO;
import school.dto.SchoolEntityDTO;
import school.entity.SchoolEntity;

@Mapper(componentModel = "spring")
public interface SchoolMapper {
    SchoolEntity toEntity(SchoolCreateDTO createDTO);

    @Mapping(source = "id", target = "id")
    SchoolEntityDTO toDto(SchoolEntity entity);

    SchoolEntity toEntity(SchoolEntityDTO entityDTO);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateEntityFromDto(SchoolEntityDTO entityDTO, @MappingTarget SchoolEntity entity);
}