package com.productcatalog.app.model;

import java.time.LocalDateTime;

import javax.validation.constraints.DecimalMax;
import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@Table
public class Product {
	
	public Product(String name, Double price, Status status, LocalDateTime postedDate) {
		super();
		this.name = name;
		this.price = price;
		this.status = status;
		this.postedDate = postedDate;
	}
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column
	@NotBlank(message = "Name is mandatory")
	@Size(max = 255)
	private String name;

	@Column
	@DecimalMin(value = "0.0", inclusive = false)
	@DecimalMax(value = "10000.0") // Assuming a maximum price of 10,000
	private Double price;

	@Enumerated(EnumType.STRING)
	@Column(name = "status")
	private Status status;

	@Column
	private LocalDateTime postedDate = LocalDateTime.now();


}
