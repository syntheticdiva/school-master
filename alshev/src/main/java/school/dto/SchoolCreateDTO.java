package school.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Data
public class SchoolCreateDTO {
    @NotBlank(message = "Название школы обязательно для заполнения")
    @Size(min = 2, max = 100, message = "Название школы должно содержать от 2 до 100 символов")
    protected String name;

    @NotBlank(message = "Адрес школы обязателен для заполнения")
    @Size(max = 200, message = "Адрес школы не должен превышать 200 символов")
    protected String address;
}