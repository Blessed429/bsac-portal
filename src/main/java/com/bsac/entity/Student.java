package com.bsac.entity;

import javax.persistence.*;

@Entity
public class Student {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String studentNumber;

    @Column(nullable = false)
    private String nationalId; // Used as password

    private String name;
    private String gender; // "Female" or "Male"
    private String role = "ROLE_USER";

    // Academic details
    private String program;        // e.g. "BSc Computer Science"
    private String yearOfStudy;    // e.g. "Year 1", "Year 2"
    private String semesterEnrolled; // e.g. "Semester 1", "Semester 2"
    private String part;           // e.g. "Part I", "Part II", "Part III", "Part IV"
    private String profilePictureUrl = "https://images.unsplash.com/photo-1534528741775-53994a69daeb?auto=format&fit=crop&w=150&q=80";

    public String getGender() { return gender; }
    public void setGender(String gender) { this.gender = gender; }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getStudentNumber() { return studentNumber; }
    public void setStudentNumber(String studentNumber) { this.studentNumber = studentNumber; }

    public String getNationalId() { return nationalId; }
    public void setNationalId(String nationalId) { this.nationalId = nationalId; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    public String getProgram() { return program; }
    public void setProgram(String program) { this.program = program; }

    public String getYearOfStudy() { return yearOfStudy; }
    public void setYearOfStudy(String yearOfStudy) { this.yearOfStudy = yearOfStudy; }

    public String getSemesterEnrolled() { return semesterEnrolled; }
    public void setSemesterEnrolled(String semesterEnrolled) { this.semesterEnrolled = semesterEnrolled; }

    public String getPart() { return part; }
    public void setPart(String part) { this.part = part; }

    public String getProfilePictureUrl() { return profilePictureUrl; }
    public void setProfilePictureUrl(String profilePictureUrl) { this.profilePictureUrl = profilePictureUrl; }
}
