package com.productcatalog.app.repository;


import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Repository;

import com.productcatalog.app.model.Product;
import com.productcatalog.app.model.Status;
@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
	List<Product> findProductByStatusOrderByPostedDateDesc(Status status);

  
  
   List<Product> findByNameEqualsIgnoreCaseOrPriceBetweenOrPostedDateBetweenAndStatus(
           String productName, Double minPrice, Double maxPrice,
           LocalDateTime minPostedDate, LocalDateTime maxPostedDate,Status status);

   }



