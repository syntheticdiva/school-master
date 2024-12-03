package school.controller;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import school.dto.SchoolCreateDTO;
import school.dto.SchoolEntityDTO;
import school.service.SchoolService;

import java.util.List;

@RestController
@RequestMapping(SchoolRestController.BASE_URL)
@Slf4j
public class SchoolRestController {
    public static final String BASE_URL = "/api/schools";

    private final SchoolService schoolService;

    @Autowired
    public SchoolRestController(SchoolService schoolService) {
        this.schoolService = schoolService;
    }

    @GetMapping
    public ResponseEntity<List<SchoolEntityDTO>> getAllSchools() {
        List<SchoolEntityDTO> schools = schoolService.getAllSchools();
        return ResponseEntity.ok(schools);
    }

    @GetMapping("/{id}")
    public ResponseEntity<SchoolEntityDTO> getSchoolById(@PathVariable Long id) {
        SchoolEntityDTO school = schoolService.findById(id);
        return ResponseEntity.ok(school);
    }

    @PostMapping
    public ResponseEntity<SchoolEntityDTO> createSchool(@Valid @RequestBody SchoolCreateDTO schoolCreateDTO) {
        SchoolEntityDTO createdSchool = schoolService.create(schoolCreateDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdSchool);
    }

    @PutMapping("/{id}")
    public ResponseEntity<SchoolEntityDTO> updateSchool(@PathVariable Long id,
                                                        @Valid @RequestBody SchoolEntityDTO schoolEntityDTO) {
        SchoolEntityDTO updatedSchool = schoolService.update(id, schoolEntityDTO);
        return ResponseEntity.ok(updatedSchool);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSchool(@PathVariable Long id) {
        schoolService.delete(id);
        return ResponseEntity.noContent().build();
    }
}