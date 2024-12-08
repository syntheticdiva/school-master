package school.controller;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import school.dto.SchoolCreateDTO;
import school.dto.SchoolEntityDTO;
import school.service.SchoolService;

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

    @PostMapping
    @Operation(summary = "Create a school")
    public ResponseEntity<SchoolEntityDTO> createSchool(@Valid @RequestBody SchoolCreateDTO schoolCreateDTO) {
        SchoolEntityDTO createdSchool = schoolService.create(schoolCreateDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdSchool);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update the school")
    public ResponseEntity<SchoolEntityDTO> updateSchool(@PathVariable Long id, @Valid @RequestBody SchoolEntityDTO schoolEntityDTO) {
        SchoolEntityDTO updatedSchool = schoolService.update(id, schoolEntityDTO);
        return ResponseEntity.ok(updatedSchool);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete school")
    public ResponseEntity<Void> deleteSchool(@PathVariable Long id) {
        try {
            log.info("Attempting to delete school with ID: {}", id);
            schoolService.delete(id);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            log.error("Error deleting school with ID {}: {}", id, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}