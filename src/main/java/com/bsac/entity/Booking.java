package com.bsac.entity;

import javax.persistence.*;

@Entity
public class Booking {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private Student student;

    @ManyToOne
    private Room room;

    private String semester;
    private Double amountPaid = 0.0;

    // PENDING, SECURED, CANCELLED
    private String status = "PENDING";

    // "ECOCASH" or "ONLINE_BANKING"
    private String paymentMethod;

    // EcoCash phone number or bank reference
    private String paymentReference;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Student getStudent() { return student; }
    public void setStudent(Student student) { this.student = student; }

    public Room getRoom() { return room; }
    public void setRoom(Room room) { this.room = room; }

    public String getSemester() { return semester; }
    public void setSemester(String semester) { this.semester = semester; }

    public Double getAmountPaid() { return amountPaid; }
    public void setAmountPaid(Double amountPaid) { this.amountPaid = amountPaid; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getPaymentMethod() { return paymentMethod; }
    public void setPaymentMethod(String paymentMethod) { this.paymentMethod = paymentMethod; }

    public String getPaymentReference() { return paymentReference; }
    public void setPaymentReference(String paymentReference) { this.paymentReference = paymentReference; }
}
