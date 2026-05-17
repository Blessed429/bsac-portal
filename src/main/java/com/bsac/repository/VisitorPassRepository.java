package com.bsac.repository;

import com.bsac.entity.Student;
import com.bsac.entity.VisitorPass;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VisitorPassRepository extends JpaRepository<VisitorPass, Long> {
    List<VisitorPass> findByStudent(Student student);
}
