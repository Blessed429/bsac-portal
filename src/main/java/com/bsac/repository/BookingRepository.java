package com.bsac.repository;

import com.bsac.entity.Booking;
import com.bsac.entity.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {
    List<Booking> findByStudent(Student student);
}
