package ru.hogwarts.schoolfive;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.util.UriComponentsBuilder;
import ru.hogwarts.schoolfive.Controller.StudentController;
import ru.hogwarts.schoolfive.Model.Student;

import java.net.URI;
import java.util.Collection;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class StudentControllerTests {
    @LocalServerPort
    private int port;
    @Autowired
    private StudentController studentController;
    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    public void contextLoads() throws Exception {
        Assertions.assertThat(studentController).isNotNull();
    }

    @Test
    public void addStudentTest() {
        Student student = formingStudent("Игорь", 26);
        ResponseEntity<Student> response = formingUrl(constructionUriBuilder().build().toUri(), student);
        checkingTheStudentForCreation(response);
    }

    @Test
    public void findStudentTest() {
        Student student = formingStudent("Игорь", 26);
        ResponseEntity<Student> response = formingUrl(constructionUriBuilder().build().toUri(), student);
        checkingTheStudentForCreation(response);
        Student createdStudent = response.getBody();
        getStudentById(createdStudent.getId(), createdStudent);
    }

    @Test
    public void editStudentTest() {
        Student student = formingStudent("Игорь", 26);
        ResponseEntity<Student> response = formingUrl(constructionUriBuilder().build().toUri(), student);
        checkingTheStudentForCreation(response);
        Student editStudent = response.getBody();

        editStudentCreating(editStudent, "Иван", 36);
        checkingTheStudentsEditing(editStudent, "Иван", 36);
    }

    @Test
    public void deleteStudentTest() {
        Student student = formingStudent("Игорь", 26);
        ResponseEntity<Student> response = formingUrl(constructionUriBuilder().build().toUri(), student);
        checkingTheStudentForCreation(response);
        Student deleteStudent = response.getBody();

        deleteStudentCreating(deleteStudent);
        checkingTheDeletionOfStudents(deleteStudent);
    }

    @Test
    public void coincidencesStudentsByAgeTest() {
        Student studentOne = formingStudent("Игорь", 26);
        Student studentTwo = formingStudent("Инна", 21);
        Student studentThree = formingStudent("Василий", 16);
        Student studentFour = formingStudent("Евгений", 34);
        Student studentFive = formingStudent("Ангелина", 29);

        formingUrl(constructionUriBuilder().build().toUri(), studentOne);
        formingUrl(constructionUriBuilder().build().toUri(), studentTwo);
        formingUrl(constructionUriBuilder().build().toUri(), studentThree);
        formingUrl(constructionUriBuilder().build().toUri(), studentFour);
        formingUrl(constructionUriBuilder().build().toUri(), studentFive);

        MultiValueMap<String, String> queryParams = new LinkedMultiValueMap<>();
        queryParams.add("Игорь", "26");
        getStudentByCriteria(queryParams, studentOne);
    }

    @Test
    public void findStudentsByAgeTest() {
        Student studentOne = formingStudent("Игорь", 26);
        Student studentTwo = formingStudent("Инна", 21);
        Student studentThree = formingStudent("Василий", 16);
        Student studentFour = formingStudent("Евгений", 34);
        Student studentFive = formingStudent("Ангелина", 29);

        formingUrl(constructionUriBuilder().build().toUri(), studentOne);
        formingUrl(constructionUriBuilder().build().toUri(), studentTwo);
        formingUrl(constructionUriBuilder().build().toUri(), studentThree);
        formingUrl(constructionUriBuilder().build().toUri(), studentFour);
        formingUrl(constructionUriBuilder().build().toUri(), studentFive);

        MultiValueMap<String, String> queryParams = new LinkedMultiValueMap<>();
        queryParams.add("maxValue", "26");
        queryParams.add("minValue", "21");
        getStudentByCriteria(queryParams, studentOne, studentTwo);
    }

    private Student formingStudent(String name, int age) {
        return new Student(name, age);
    }

    private ResponseEntity<Student> formingUrl(URI uri, Student student) {
        return restTemplate.postForEntity(uri, student, Student.class);
    }

    private UriComponentsBuilder constructionUriBuilder() {
        return UriComponentsBuilder.newInstance()
                .scheme("http")
                .host("localhost")
                .port(port)
                .path("/student");
    }

    private void checkingTheStudentForCreation(ResponseEntity<Student> response) {
        Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        Assertions.assertThat(response.getBody()).isNotNull();
        Assertions.assertThat(response.getBody().getId()).isNotNull();
    }

    private void getStudentById(Long studentId, Student student) {
        URI uri = constructionUriBuilder().path("/{id}").buildAndExpand(studentId).toUri();
        ResponseEntity<Student> response = restTemplate.getForEntity(uri, Student.class);

        Assertions.assertThat(response.getBody()).isEqualTo(student);
        Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    private void getStudentByCriteria(MultiValueMap<String, String> queryParams, Student... students) {
        URI uri = constructionUriBuilder().queryParams(queryParams).build().toUri();
        ResponseEntity<Collection<Student>> response = restTemplate.exchange(uri, HttpMethod.GET, null, new ParameterizedTypeReference<Collection<Student>>() {
        });

        Assertions.assertThat(response.getBody()).isNotNull();
        Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

        Collection<Student> actualResult = response.getBody();
        resetIds(actualResult);
        Assertions.assertThat(actualResult).containsExactlyInAnyOrder(students);
    }

    private void resetIds(Collection<Student> students) {
        students.forEach(id -> id.setId(null));
    }

    private void editStudentCreating(Student editStudent, String newName, int newAge) {
        editStudent.setName(newName);
        editStudent.setAge(newAge);

        restTemplate.put(constructionUriBuilder().build().toUri(), editStudent);
    }

    private void checkingTheStudentsEditing(Student editStudent, String newName, int newAge) {
        URI uri = constructionUriBuilder().path("/{id}").buildAndExpand(editStudent.getId()).toUri();
        ResponseEntity<Student> response = restTemplate.getForEntity(uri, Student.class);

        Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        Assertions.assertThat(response.getBody()).isNotNull();
        Assertions.assertThat(response.getBody().getName()).isEqualTo(newName);
        Assertions.assertThat(response.getBody().getAge()).isEqualTo(newAge);
    }

    private void deleteStudentCreating(Student deleteStudent) {
        restTemplate.delete(constructionUriBuilder().path("/{id}").buildAndExpand(deleteStudent.getId()).toUri());
    }

    private void checkingTheDeletionOfStudents(Student deleteStudent) {
        URI uri = constructionUriBuilder().path("/{id}").buildAndExpand(deleteStudent.getId()).toUri();
        ResponseEntity<Student> response = restTemplate.getForEntity(uri, Student.class);

        Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }
}
