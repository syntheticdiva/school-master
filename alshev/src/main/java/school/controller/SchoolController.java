package school.controller;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import school.dto.SchoolCreateDTO;
import school.dto.SchoolEntityDTO;
import school.exception.NotificationSendingException;
import school.exception.ResourceNotFoundException;
import school.exception.SchoolServiceException;
import school.exception.SchoolUpdateException;
import school.mapper.SchoolMapper;
import school.repository.SchoolRepository;
import school.service.SchoolService;
import school.service.ThreadService;

@Controller
@RequestMapping(SchoolController.BASE_URL)
@Slf4j
public class SchoolController {
    private final SchoolService schoolService;

    private final ThreadService threadService;

    private final SchoolRepository schoolRepository;

    private final SchoolMapper schoolMapper;

    public static final String BASE_URL = "/schools";
    public static final String CREATE_URL = "/create";
    public static final String EDIT_URL = "/edit/{id}";
    public static final String UPDATE_URL = "/update/{id}";
    public static final String DELETE_URL = "/delete/{id}";

    private static final String CREATE_VIEW = "create";
    private static final String EDIT_VIEW = "edit";
    private static final String LIST_VIEW = "all-schools";

    private static final int PAGE_SIZE = 10;
    private static final String SUCCESS_CREATE_MESSAGE = "School created successfully!";
    private static final String ERROR_CREATE_MESSAGE = "Error creating school";
    private static final String SUCCESS_UPDATE_MESSAGE = "School updated successfully!";
    private static final String ERROR_UPDATE_MESSAGE = "Error updating school";
    private static final String SUCCESS_DELETE_MESSAGE = "School deleted successfully!";
    private static final String ERROR_DELETE_MESSAGE = "Error deleting school";

    @Autowired
    public SchoolController(SchoolService schoolService, ThreadService threadService, SchoolRepository schoolRepository, SchoolMapper schoolMapper) {
        this.schoolService = schoolService;
        this.threadService = threadService;
        this.schoolRepository = schoolRepository;
        this.schoolMapper = schoolMapper;
    }

    @GetMapping
    public String listSchools(Model model, @RequestParam(defaultValue = "0") int page) {
        Page<SchoolEntityDTO> schoolPage = schoolService.getAllSchoolsPaged(PageRequest.of(page, PAGE_SIZE));
        if (schoolPage.isEmpty() && page > 0) {
            return "redirect:" + BASE_URL + "?page=" + (page - 1);
        }

        model.addAttribute("baseUrl", BASE_URL);
        model.addAttribute("createUrl", CREATE_URL);
        model.addAttribute("editUrl", EDIT_URL);
        model.addAttribute("deleteUrl", DELETE_URL);

        model.addAttribute("schools", schoolPage.getContent());
        model.addAttribute("currentPage", schoolPage.getNumber());
        model.addAttribute("totalPages", schoolPage.getTotalPages());
        model.addAttribute("totalItems", schoolPage.getTotalElements());
        return LIST_VIEW;
    }

    @GetMapping(CREATE_URL)
    public String showCreateForm(Model model) {
        model.addAttribute("school", new SchoolCreateDTO());
        model.addAttribute("baseUrl", BASE_URL);
        model.addAttribute("createUrl", CREATE_URL);
        return CREATE_VIEW;
    }
    @PostMapping(CREATE_URL)
    public String createSchool(
            @Valid @ModelAttribute("school") SchoolCreateDTO schoolCreateDTO,
            BindingResult bindingResult,
            RedirectAttributes redirectAttributes,
            Model model
    ) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("baseUrl", BASE_URL);
            model.addAttribute("createUrl", CREATE_URL);
            return CREATE_VIEW;
        }

        try {
            SchoolEntityDTO createdSchool = schoolService.create(schoolCreateDTO);
            redirectAttributes.addFlashAttribute("successMessage", SUCCESS_CREATE_MESSAGE);
        } catch (SchoolServiceException e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Ошибка при создании школы: " + e.getMessage());
        }

        return "redirect:" + BASE_URL;
    }

    @GetMapping(EDIT_URL)
    public String showEditForm(@PathVariable Long id, Model model) {
        SchoolEntityDTO school = schoolService.findById(id);
        model.addAttribute("school", school);
        model.addAttribute("baseUrl", BASE_URL);
        model.addAttribute("updateUrl", UPDATE_URL);
        return EDIT_VIEW;
    }


    @PostMapping(UPDATE_URL)
    public String updateSchool(@PathVariable Long id,
                               @Valid @ModelAttribute("school") SchoolEntityDTO schoolDTO,
                               BindingResult result,
                               RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            log.info("result has error");
            return EDIT_VIEW;
        }

        try {
            schoolService.update(id, schoolDTO);

            redirectAttributes.addFlashAttribute("successMessage", SUCCESS_UPDATE_MESSAGE);
            log.info("School updated successfully");

            return "redirect:" + BASE_URL;
        } catch (ResourceNotFoundException e) {
            log.error("School not found with id: " + id, e);
            redirectAttributes.addFlashAttribute("errorMessage", ERROR_UPDATE_MESSAGE);
            return "redirect:" + BASE_URL + EDIT_URL.replace("{id}", id.toString());
        } catch (SchoolUpdateException e) {
            log.error("Error while updating school", e);
            redirectAttributes.addFlashAttribute("errorMessage", ERROR_UPDATE_MESSAGE);
            return "redirect:" + BASE_URL + EDIT_URL.replace("{id}", id.toString());
        }
    }
@GetMapping(DELETE_URL)
public String deleteSchool(@PathVariable Long id, RedirectAttributes redirectAttributes) {
    try {
        schoolService.delete(id);
        redirectAttributes.addFlashAttribute("successMessage", SUCCESS_DELETE_MESSAGE);
    } catch (ResourceNotFoundException e) {
        redirectAttributes.addFlashAttribute("errorMessage", ERROR_DELETE_MESSAGE + " - School not found");
    } catch (SchoolServiceException e) {
        redirectAttributes.addFlashAttribute("errorMessage", ERROR_DELETE_MESSAGE);
    }

    return "redirect:" + BASE_URL;
}
}