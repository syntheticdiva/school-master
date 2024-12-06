package school.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class SchoolOnCreateDto {
    private Long schoolId;
    private String name;
    private String address;
    private LocalDateTime createDate;
}