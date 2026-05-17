package com.bsac.entity;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
public class VisitorPass {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String visitorName;

    @Column(nullable = false)
    private String visitorPhone;

    @Column(length = 6, nullable = false)
    private String gateCode;

    private LocalDateTime createdAt = LocalDateTime.now();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id")
    private Student student;

    @Column(nullable = false, columnDefinition = "varchar(255) default 'ACTIVE'")
    private String status = "ACTIVE";

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getVisitorName() { return visitorName; }
    public void setVisitorName(String visitorName) { this.visitorName = visitorName; }

    public String getVisitorPhone() { return visitorPhone; }
    public void setVisitorPhone(String visitorPhone) { this.visitorPhone = visitorPhone; }

    public String getGateCode() { return gateCode; }
    public void setGateCode(String gateCode) { this.gateCode = gateCode; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public Student getStudent() { return student; }
    public void setStudent(Student student) { this.student = student; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}
