package com.bsac.repository;

import com.bsac.entity.Complaint;
import com.bsac.entity.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ComplaintRepository extends JpaRepository<Complaint, Long> {
    List<Complaint> findByStudent(Student student);
}
