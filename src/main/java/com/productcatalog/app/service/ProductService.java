package com.productcatalog.app.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.http.ResponseEntity;

import com.productcatalog.app.model.Product;

public interface ProductService {
	public ResponseEntity<Object> createProductwithApprovalCheck(Product product);

	public ResponseEntity<Object> updateProductWithApprovalCheck(Long productId, Product updatedProduct);

	public ResponseEntity<Object> deleteProductWithApproval(Long productId);

	public ResponseEntity<Object> approveProduct(Long approvalId);

	public ResponseEntity<Object> rejectProduct(Long approvalId);

	public List<Product> searchProductsBasedOnSearchCriteria(String productName, Double minPrice, Double maxPrice,
			LocalDateTime minPostedDate, LocalDateTime maxPostedDate) throws Exception;
}
