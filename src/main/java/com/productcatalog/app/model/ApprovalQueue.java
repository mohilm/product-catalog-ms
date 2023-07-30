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
@NoArgsConstructor
@Table
public class ApprovalQueue {
    
	public ApprovalQueue(String name, Double price, Status status, LocalDateTime postedDate, 
			 Long productId) {
		super();
		this.name = name;
		this.price = price;
		this.status = status;
		this.postedDate = postedDate;
		this.productId = productId;
	}


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
