package school.controller;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import school.dto.SchoolCreateDTO;
import school.dto.SchoolEntityDTO;
import school.service.SchoolService;

@Controller
@RequestMapping(SchoolController.BASE_URL)
@Slf4j
public class SchoolController {
    private final SchoolService schoolService;

    // URL константы
    public static final String BASE_URL = "/schools";
    public static final String CREATE_URL = "/create";
    public static final String EDIT_URL = "/edit/{id}";
    public static final String UPDATE_URL = "/update/{id}";
    public static final String DELETE_URL = "/delete/{id}";

    // Константы представлений
    private static final String CREATE_VIEW = "create";
    private static final String EDIT_VIEW = "edit";
    private static final String LIST_VIEW = "all-schools";

    // Константы для пагинации и сообщений
    private static final int PAGE_SIZE = 10;
    private static final String SUCCESS_CREATE_MESSAGE = "School created successfully!";
    private static final String ERROR_CREATE_MESSAGE = "Error creating school";
    private static final String SUCCESS_UPDATE_MESSAGE = "School updated successfully!";
    private static final String ERROR_UPDATE_MESSAGE = "Error updating school";
    private static final String SUCCESS_DELETE_MESSAGE = "School deleted successfully!";
    private static final String ERROR_DELETE_MESSAGE = "Error deleting school";

    public SchoolController(SchoolService schoolService) {
        this.schoolService = schoolService;
    }

    @GetMapping
    public String listSchools(Model model, @RequestParam(defaultValue = "0") int page) {
        Page<SchoolEntityDTO> schoolPage = schoolService.getAllSchoolsPaged(PageRequest.of(page, PAGE_SIZE));
        if (schoolPage.isEmpty() && page > 0) {
            return "redirect:" + BASE_URL + "?page=" + (page - 1);
        }

        // Передача констант URL в модель
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
            return "redirect:" + BASE_URL;
        } catch (Exception e) {
            log.error("Error while creating school", e);
            redirectAttributes.addFlashAttribute("errorMessage", ERROR_CREATE_MESSAGE);
            return "redirect:" + BASE_URL + CREATE_URL;
        }
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
            return EDIT_VIEW;
        }
        try {
            schoolService.update(id, schoolDTO);
            redirectAttributes.addFlashAttribute("successMessage", SUCCESS_UPDATE_MESSAGE);
            return "redirect:" + BASE_URL;
        } catch (Exception e) {
            log.error("Error while updating school", e);
            redirectAttributes.addFlashAttribute("errorMessage", ERROR_UPDATE_MESSAGE);
            return "redirect:" + BASE_URL + EDIT_URL.replace("{id}", id.toString());
        }
    }

    @GetMapping(DELETE_URL)
    public String deleteSchool(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            schoolService.deleteById(id);
            redirectAttributes.addFlashAttribute("successMessage", SUCCESS_DELETE_MESSAGE);
        } catch (Exception e) {
            log.error("Error while deleting school", e);
            redirectAttributes.addFlashAttribute("errorMessage", ERROR_DELETE_MESSAGE);
        }
        return "redirect:" + BASE_URL;
    }
}