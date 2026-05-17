package com.bsac.security;

import com.bsac.entity.Student;
import com.bsac.entity.Admin;
import com.bsac.repository.StudentRepository;
import com.bsac.repository.AdminRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private AdminRepository adminRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        if (username == null) {
            throw new UsernameNotFoundException("Username is required.");
        }
        
        String formattedUsername = username.trim();
        
        if (formattedUsername.contains("@")) {
            // Admin login
            Optional<Admin> adminOpt = adminRepository.findByEmail(formattedUsername.toLowerCase());
            if (adminOpt.isPresent()) {
                Admin admin = adminOpt.get();
                if (!admin.isVerified()) {
                    throw new UsernameNotFoundException("Admin account is not verified.");
                }
                return User.builder()
                        .username(admin.getEmail())
                        .password(admin.getPassword())
                        .roles("ADMIN")
                        .build();
            }
        }
        
        // Student login
        String studentNumber = formattedUsername.toUpperCase();
        Optional<Student> studentOpt = studentRepository.findByStudentNumber(studentNumber);
        if (studentOpt.isEmpty()) {
            throw new UsernameNotFoundException("User not found with username: " + username);
        }
        
        Student student = studentOpt.get();
        return User.builder()
                .username(student.getStudentNumber())
                .password(student.getNationalId())
                .roles(student.getRole().replace("ROLE_", ""))
                .build();
    }
}
