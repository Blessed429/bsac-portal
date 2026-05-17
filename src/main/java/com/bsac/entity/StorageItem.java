package com.bsac.entity;

import javax.persistence.*;

@Entity
public class StorageItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private Student student;

    private String itemName;
    private String description;
    
    // Flat $20 fee per semester/storage period
    private Double storageFee = 20.0;
    
    private String paymentMethod;
    private String paymentReference;
    private String status = "PENDING ADMIN APPROVAL";

    public String getPaymentMethod() { return paymentMethod; }
    public void setPaymentMethod(String paymentMethod) { this.paymentMethod = paymentMethod; }

    public String getPaymentReference() { return paymentReference; }
    public void setPaymentReference(String paymentReference) { this.paymentReference = paymentReference; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Student getStudent() { return student; }
    public void setStudent(Student student) { this.student = student; }

    public String getItemName() { return itemName; }
    public void setItemName(String itemName) { this.itemName = itemName; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public Double getStorageFee() { return storageFee; }
    public void setStorageFee(Double storageFee) { this.storageFee = storageFee; }
}
