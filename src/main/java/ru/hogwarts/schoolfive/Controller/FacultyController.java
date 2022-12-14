package ru.hogwarts.schoolfive.Controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.hogwarts.schoolfive.Model.Faculty;
import ru.hogwarts.schoolfive.Model.Student;
import ru.hogwarts.schoolfive.Service.FacultyService;

import java.util.Collection;
import java.util.Collections;

@RestController
@RequestMapping("/faculty")
public class FacultyController {
    private final FacultyService facultyService;

    public FacultyController(FacultyService facultyService) {
        this.facultyService = facultyService;
    }

    @PostMapping
    public Faculty addFaculty(@RequestBody Faculty faculty) {
        return facultyService.addFaculty(faculty);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Faculty> findFaculty(@PathVariable Long id) {
        Faculty faculty = facultyService.findFaculty(id);
        if (faculty == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(faculty);
    }

    @PutMapping
    public ResponseEntity<Faculty> editFaculty(@RequestBody Faculty faculty) {
        Faculty editFaculty = facultyService.editFaculty(faculty);
        if (editFaculty == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(editFaculty);
    }

    @DeleteMapping("{id}")
    public ResponseEntity<Faculty> deleteFaculty(@PathVariable Long id) {
        facultyService.deleteFaculty(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/color")
    public ResponseEntity<Collection<Faculty>> matchingFacultiesByColor(@RequestParam(required = false) String color) {
        if (color != null && !color.isBlank()) {
            return ResponseEntity.ok(facultyService.matchingFacultiesByColor(color));
        }
        return ResponseEntity.ok(Collections.emptyList());
    }

    @GetMapping
    public ResponseEntity<Collection<Faculty>> findByNameAndColor(@RequestParam(required = false) String name,
                                                                  @RequestParam(required = false) String color) {
        if (name != null && !name.isBlank()) {
            return ResponseEntity.ok(facultyService.findByName(name));
        } else if (color != null && !color.isBlank()) {
            return ResponseEntity.ok(facultyService.findByColor(color));
        }
        return ResponseEntity.ok(Collections.emptyList());
    }

    @GetMapping("/list-faculties")
    public ResponseEntity<Collection<Student>> listOfStudentsOfTheFaculty(@RequestParam Long id) {
        if (facultyService.findFaculty(id) == null) {
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.ok(facultyService.listOfStudentsOfTheFaculty(id));
    }
}
