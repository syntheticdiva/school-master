package school.controller;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import school.dto.SchoolCreateDTO;
import school.dto.SchoolEntityDTO;
import school.service.SchoolService;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class SchoolControllerTest {

    @Mock
    private SchoolService schoolService;

    @Mock
    private Model model;

    @Mock
    private BindingResult bindingResult;

    @Mock
    private RedirectAttributes redirectAttributes;

    @InjectMocks
    private SchoolController schoolController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void listSchools_shouldReturnListView_whenSchoolsExist() {
        // Arrange
        List<SchoolEntityDTO> schools = Collections.singletonList(new SchoolEntityDTO());
        Page<SchoolEntityDTO> schoolPage = new PageImpl<>(schools);
        when(schoolService.getAllSchoolsPaged(any(PageRequest.class))).thenReturn(schoolPage);

        // Act
        String viewName = schoolController.listSchools(model, 0);

        // Assert
        assertEquals("all-schools", viewName);
        verify(model).addAttribute(eq("schools"), anyList());
        verify(model).addAttribute(eq("currentPage"), anyInt());
        verify(model).addAttribute(eq("totalPages"), anyInt());
        verify(model).addAttribute(eq("totalItems"), anyLong());
    }

    @Test
    void showCreateForm_shouldReturnCreateView() {
        // Act
        String viewName = schoolController.showCreateForm(model);

        // Assert
        assertEquals("create", viewName);
        verify(model).addAttribute(eq("school"), any(SchoolCreateDTO.class));
    }

    @Test
    void createSchool_withValidData_shouldRedirectToBaseUrl() {
        // Arrange
        SchoolCreateDTO schoolCreateDTO = new SchoolCreateDTO();
        SchoolEntityDTO createdSchool = new SchoolEntityDTO();
        when(bindingResult.hasErrors()).thenReturn(false);
        when(schoolService.create(any(SchoolCreateDTO.class))).thenReturn(createdSchool);

        // Act
        String result = schoolController.createSchool(schoolCreateDTO, bindingResult, redirectAttributes, model);

        // Assert
        assertEquals("redirect:/schools", result);
        verify(schoolService).create(schoolCreateDTO);
        verify(redirectAttributes).addFlashAttribute(eq("successMessage"), anyString());
    }

    @Test
    void createSchool_withInvalidData_shouldReturnCreateView() {
        // Arrange
        SchoolCreateDTO schoolCreateDTO = new SchoolCreateDTO();
        when(bindingResult.hasErrors()).thenReturn(true);

        // Act
        String result = schoolController.createSchool(schoolCreateDTO, bindingResult, redirectAttributes, model);

        // Assert
        assertEquals("create", result);
        verify(model).addAttribute(eq("baseUrl"), anyString());
        verify(model).addAttribute(eq("createUrl"), anyString());
    }

    @Test
    void showEditForm_shouldReturnEditView() {
        // Arrange
        Long schoolId = 1L;
        SchoolEntityDTO school = new SchoolEntityDTO();
        when(schoolService.findById(schoolId)).thenReturn(school);

        // Act
        String viewName = schoolController.showEditForm(schoolId, model);

        // Assert
        assertEquals("edit", viewName);
        verify(model).addAttribute(eq("school"), eq(school));
    }

    @Test
    void updateSchool_withInvalidData_shouldReturnEditView() {
        // Arrange
        Long schoolId = 1L;
        SchoolEntityDTO schoolDTO = new SchoolEntityDTO();
        when(bindingResult.hasErrors()).thenReturn(true);

        // Act
        String result = schoolController.updateSchool(schoolId, schoolDTO, bindingResult, redirectAttributes);

        // Assert
        assertEquals("edit", result);
    }

    @Test
    void deleteSchool_shouldRedirectToBaseUrl() {
        // Arrange
        Long schoolId = 1L;
        doNothing().when(schoolService).deleteById(schoolId);

        // Act
        String result = schoolController.deleteSchool(schoolId, redirectAttributes);

        // Assert
        assertEquals("redirect:/schools", result);
        verify(schoolService).deleteById(schoolId);
        verify(redirectAttributes).addFlashAttribute(eq("successMessage"), anyString());
    }

    @Test
    void deleteSchool_withException_shouldAddErrorMessage() {
        // Arrange
        Long schoolId = 1L;
        doThrow(new RuntimeException("Delete error")).when(schoolService).deleteById(schoolId);

        // Act
        String result = schoolController.deleteSchool(schoolId, redirectAttributes);

        // Assert
        assertEquals("redirect:/schools", result);
        verify(schoolService).deleteById(schoolId);
        verify(redirectAttributes).addFlashAttribute(eq("errorMessage"), anyString());
    }
}