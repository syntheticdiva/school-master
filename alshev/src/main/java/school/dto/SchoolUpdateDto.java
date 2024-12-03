package school.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class SchoolUpdateDto {
    private long schoolId;
    private String newName;
    private String oldName;
    private String newAddress;
    private String oldAddress;
    private LocalDateTime updateDate;

    @Override
    public String toString() {
        return "SchoolUpdateDto{" +
                "schoolId=" + schoolId +
                ", newName='" + newName + '\'' +
                ", oldName='" + oldName + '\'' +
                ", newAddress='" + newAddress + '\'' +
                ", oldAddress='" + oldAddress + '\'' +
                ", updateDate=" + updateDate +
                '}';
    }

    public SchoolUpdateDto(SchoolEntityDTO oldVal, SchoolEntityDTO newVal) {
        this.schoolId = newVal.getId();
        this.newName = newVal.getName();
        this.newAddress = newVal.getAddress();
        this.updateDate = LocalDateTime.now();
        this.oldName = oldVal.getName();
        this.oldAddress = oldVal.getAddress();
    }
}
