package com.productcatalog.app.model;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table
public class ApprovalQueue {
    
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    
    @Column
    private String name;

    @Column
    private Double price;

    @Enumerated(EnumType.STRING)
    @Column(name="status")
    private Status status;

    @Column
    private LocalDateTime postedDate  = LocalDateTime.now();
    
    @Column
    private String approvalAction = "PENDING";

    @Column
    private LocalDateTime approvalRequestDate = LocalDateTime.now();
    
    
   @Column
    private Long productId;
  
   
 
}
