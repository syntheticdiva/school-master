package school.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SchoolOnDeleteDto {
    private Long schoolId;
    private String name;
    private String address;
}
