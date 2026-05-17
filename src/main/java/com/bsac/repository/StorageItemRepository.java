package com.bsac.repository;

import com.bsac.entity.StorageItem;
import com.bsac.entity.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface StorageItemRepository extends JpaRepository<StorageItem, Long> {
    List<StorageItem> findByStudent(Student student);
}
