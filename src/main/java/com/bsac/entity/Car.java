package com.bsac.entity;

import javax.persistence.*;

@Entity
public class Car {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String regNumber;

    @Column(nullable = false)
    private String carName;

    @Column(nullable = false)
    private String carColor;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id")
    private Student student;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getRegNumber() { return regNumber; }
    public void setRegNumber(String regNumber) { this.regNumber = regNumber; }

    public String getCarName() { return carName; }
    public void setCarName(String carName) { this.carName = carName; }

    public String getCarColor() { return carColor; }
    public void setCarColor(String carColor) { this.carColor = carColor; }

    public Student getStudent() { return student; }
    public void setStudent(Student student) { this.student = student; }
}
