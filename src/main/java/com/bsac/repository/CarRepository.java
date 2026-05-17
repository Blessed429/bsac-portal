package com.bsac.repository;

import com.bsac.entity.Car;
import com.bsac.entity.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CarRepository extends JpaRepository<Car, Long> {
    Optional<Car> findByStudent(Student student);
}
