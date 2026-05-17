package com.bsac.entity;

import javax.persistence.*;

@Entity
public class Room {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String blockName;
    private String roomNumber;
    private String designatedGender; // "Female" or "Male"
    
    private boolean isOccupied = false;

    public String getDesignatedGender() { return designatedGender; }
    public void setDesignatedGender(String designatedGender) { this.designatedGender = designatedGender; }
    
    private Double rentFee = 400.0;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getBlockName() { return blockName; }
    public void setBlockName(String blockName) { this.blockName = blockName; }

    public String getRoomNumber() { return roomNumber; }
    public void setRoomNumber(String roomNumber) { this.roomNumber = roomNumber; }

    public boolean isOccupied() { return isOccupied; }
    public void setOccupied(boolean occupied) { isOccupied = occupied; }

    public Double getRentFee() { return rentFee; }
    public void setRentFee(Double rentFee) { this.rentFee = rentFee; }
}
