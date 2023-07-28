package com.productcatalog.app.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.http.ResponseEntity;

import com.productcatalog.app.model.Product;

public interface ProductService {
	public ResponseEntity<String> createProductwithApprovalCheck(Product product);

	public ResponseEntity<String> updateProductWithApprovalCheck(Long productId, Product updatedProduct);

	public ResponseEntity<String> deleteProductWithApproval(Long productId);

	public ResponseEntity<String> approveProduct(Long approvalId);

	public ResponseEntity<String> rejectProduct(Long approvalId);

	public List<Product> searchProductsBasedOnSearchCriteria(String productName, Double minPrice, Double maxPrice,
			LocalDateTime minPostedDate, LocalDateTime maxPostedDate);
}
