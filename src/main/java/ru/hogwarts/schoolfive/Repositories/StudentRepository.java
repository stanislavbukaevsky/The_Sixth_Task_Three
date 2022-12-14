package ru.hogwarts.schoolfive.Repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.hogwarts.schoolfive.Model.Student;

import java.util.Collection;

public interface StudentRepository extends JpaRepository<Student, Long> {

    Collection<Student> findByAgeBetween(int min, int max);

    Collection<Student> findByFacultiesId(long id);
}
