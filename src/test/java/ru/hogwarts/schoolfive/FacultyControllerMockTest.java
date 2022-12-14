package ru.hogwarts.schoolfive;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.json.JSONObject;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultMatcher;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import ru.hogwarts.schoolfive.Controller.FacultyController;
import ru.hogwarts.schoolfive.Model.Faculty;
import ru.hogwarts.schoolfive.Repositories.FacultyRepository;
import ru.hogwarts.schoolfive.Service.FacultyService;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = FacultyController.class)
public class FacultyControllerMockTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private FacultyRepository facultyRepository;

    @SpyBean
    private FacultyService facultyService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    public void findFacultyTest() throws Exception {
        Long id = 1L;
        String name = "Исторический";
        String color = "Желтый";

        Faculty faculty = new Faculty();
        faculty.setId(id);
        faculty.setName(name);
        faculty.setColor(color);

        when(facultyRepository.findById(id)).thenReturn(Optional.of(faculty));

        mockMvc.perform(MockMvcRequestBuilders
                        .get("/faculty/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect((ResultMatcher) status())
                .andExpect(jsonPath("$.id").value(id))
                .andExpect(jsonPath("$.name").value(name))
                .andExpect(jsonPath("$.color").value(color));
    }

    @Test
    public void matchingFacultiesByColorTest() throws Exception {
        Long idOne = 1L;
        String nameOne = "Исторический";
        Long idTwo = 2L;
        String nameTwo = "Биологический";
        String color = "Желтый";

        Faculty facultyOne = new Faculty();
        facultyOne.setId(idOne);
        facultyOne.setName(nameOne);
        facultyOne.setColor(color);

        Faculty facultyTwo = new Faculty();
        facultyTwo.setId(idTwo);
        facultyTwo.setName(nameTwo);
        facultyTwo.setColor(color);

        when(facultyRepository.findFacultiesByColorIgnoreCase(color)).thenReturn(Set.of(facultyOne, facultyTwo));

        mockMvc.perform(MockMvcRequestBuilders
                        .get("/faculty")
                        .queryParam("color", color)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(List.of(facultyOne, facultyTwo))));
    }

    @Test
    public void findByNameAndColorTest() throws Exception {
        Long idOne = 1L;
        String nameOne = "Исторический";
        String colorOne = "Желтый";
        String colorOneIgnoreCase = "желтый";
        Long idTwo = 2L;
        String nameTwo = "Биологический";
        String colorTwo = "Синий";
        String nameTwoIgnoreCase = "биолоГиЧеский";

        Faculty facultyOne = new Faculty();
        facultyOne.setId(idOne);
        facultyOne.setName(nameOne);
        facultyOne.setColor(colorOne);

        Faculty facultyTwo = new Faculty();
        facultyTwo.setId(idTwo);
        facultyTwo.setName(nameTwo);
        facultyTwo.setColor(colorTwo);

        when(facultyRepository.findFacultiesByColorIgnoreCase(colorOneIgnoreCase)).thenReturn(Set.of(facultyOne));
        when(facultyRepository.findFacultiesByNameIgnoreCase(nameTwoIgnoreCase)).thenReturn(Set.of(facultyTwo));

        mockMvc.perform(MockMvcRequestBuilders
                        .get("/faculty")
                        .queryParam("color", colorOneIgnoreCase)
                        .queryParam("name", nameTwoIgnoreCase)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(List.of(facultyOne, facultyTwo))));
    }

    @Test
    public void addFacultyTest() throws Exception {
        Long id = 1L;
        String name = "Исторический";
        String color = "Желтый";

        JSONObject objectFaculty = new JSONObject();
        objectFaculty.put("name", name);
        objectFaculty.put("color", color);

        Faculty faculty = new Faculty();
        faculty.setId(id);
        faculty.setName(name);
        faculty.setColor(color);

        when(facultyRepository.save(any(Faculty.class))).thenReturn(faculty);
        when(facultyRepository.findById(id)).thenReturn(Optional.of(faculty));

        mockMvc.perform(MockMvcRequestBuilders
                        .post("/faculty")
                        .content(objectFaculty.toString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(id))
                .andExpect(jsonPath("$.name").value(name))
                .andExpect(jsonPath("$.color").value(color));
    }

    @Test
    public void editFacultyTest() throws Exception {
        Long id = 1L;
        String name = "Исторический";
        String color = "Желтый";
        String editName = "Биологический";
        String editColor = "Синий";

        JSONObject objectFaculty = new JSONObject();
        objectFaculty.put("id", id);
        objectFaculty.put("name", name);
        objectFaculty.put("color", color);

        Faculty faculty = new Faculty();
        faculty.setId(id);
        faculty.setName(name);
        faculty.setColor(color);

        Faculty editFaculty = new Faculty();
        editFaculty.setId(id);
        editFaculty.setName(editName);
        editFaculty.setColor(editColor);

        when(facultyRepository.findById(id)).thenReturn(Optional.of(faculty));
        when(facultyRepository.save(any(Faculty.class))).thenReturn(editFaculty);

        mockMvc.perform(MockMvcRequestBuilders
                        .put("/faculty")
                        .content(objectFaculty.toString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id))
                .andExpect(jsonPath("$.name").value(editName))
                .andExpect(jsonPath("$.color").value(editColor));
    }

    @Test
    public void deleteFacultyTest() throws Exception {
        Long id = 1L;
        String name = "Исторический";
        String color = "Желтый";

        Faculty faculty = new Faculty();
        faculty.setId(id);
        faculty.setName(name);
        faculty.setColor(color);

        when(facultyRepository.findById(id)).thenReturn(Optional.of(faculty));

        mockMvc.perform(MockMvcRequestBuilders
                        .delete("/faculty/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id))
                .andExpect(jsonPath("$.name").value(name))
                .andExpect(jsonPath("$.color").value(color));

        verify(facultyRepository, atLeastOnce()).deleteById(id);
    }
}
