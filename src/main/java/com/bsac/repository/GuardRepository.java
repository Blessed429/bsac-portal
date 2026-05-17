package com.bsac.repository;

import com.bsac.entity.Guard;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface GuardRepository extends JpaRepository<Guard, Long> {
    Optional<Guard> findByFirstNameAndSurnameAndLoginCode(String firstName, String surname, String loginCode);
}
