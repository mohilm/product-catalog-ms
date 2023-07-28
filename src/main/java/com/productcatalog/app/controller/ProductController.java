package com.productcatalog.app.controller;

import java.time.LocalDateTime;
import java.util.List;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.productcatalog.app.exception.ProductCatalogException;
import com.productcatalog.app.model.ApprovalQueue;
import com.productcatalog.app.model.Product;
import com.productcatalog.app.model.Status;
import com.productcatalog.app.repository.ApprovalQueueRepository;
import com.productcatalog.app.repository.ProductRepository;
import com.productcatalog.app.service.ProductService;

import lombok.extern.log4j.Log4j2;

@Log4j2
@RestController
@RequestMapping("/api/v1/products")
public class ProductController {

	@Autowired
	private ProductRepository productRepository;

	@Autowired
	private ApprovalQueueRepository approvalQueueRepository;

	@Autowired
	private ProductService productService;

	@GetMapping
	public List<Product> listActiveProducts() {
		return productRepository.findProductByStatusOrderByPostedDateDesc(Status.ACTIVE);
	}

	@GetMapping("/search")
	public List<Product> searchProducts(
			@RequestParam(required = false) String productName, 
			@RequestParam(required = false) Double minPrice,
			@RequestParam(required = false) Double maxPrice,
			@RequestParam(required = false) @Pattern(regexp = "^\\d{4}-\\d{2}-\\d{2}T\\d{2}:\\d{2}:\\d{2}$", message = "minPostedDate must be in the format 'yyyy-MM-dd'T'HH:mm:ss'") LocalDateTime minPostedDate,
			@RequestParam(required = false) @Pattern(regexp = "^\\d{4}-\\d{2}-\\d{2}T\\d{2}:\\d{2}:\\d{2}$", message = "maxPostedDate must be in the format 'yyyy-MM-dd'T'HH:mm:ss'") LocalDateTime maxPostedDate) {

		// Validations: Ensure that the maxPrice is greater than or equal to minPrice
		if (minPrice != null && maxPrice != null && minPrice.compareTo(maxPrice) >= 0) {
			throw new IllegalArgumentException("maxPrice should be greater than or equal to minPrice");
		}

		// Validations: Ensure that the maxPostedDate is after or equal to minPostedDate
		if (minPostedDate != null && maxPostedDate != null && minPostedDate.isAfter(maxPostedDate)) {
			throw new IllegalArgumentException("maxPostedDate should be after or equal to minPostedDate");
		}
		log.info("Retrieving product on the basis of the search criteria");
		return productRepository.findByNameEqualsIgnoreCaseOrPriceBetweenOrPostedDateBetween(productName, minPrice,
				maxPrice, minPostedDate, maxPostedDate);

	}

	@PostMapping
	public ResponseEntity<String> createProduct(@RequestBody @Valid Product product) {
		try {
			return productService.createProductwithApprovalCheck(product);
		} catch (Exception e) {
			throw new ProductCatalogException("Create Product Failed - " + e.getMessage());
		}

	}

	@PutMapping(value = "/{productId}")
	ResponseEntity<String> updateProduct(@PathVariable("productId") @Min(1) Long id,
			@Valid @RequestBody Product product) {
		try {
			return productService.updateProductWithApprovalCheck(id, product);
		} catch (Exception e) {
			throw new ProductCatalogException("Create Product Failed - " + e.getMessage());
		}
	}

	@DeleteMapping("/{productId}")
	public ResponseEntity<String> deleteProduct(@PathVariable("productId") @NotNull Long productId) {

		try {
			if (productId == null) {
				return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Product ID cannot be null");
			}
			return productService.deleteProductWithApproval(productId);
		} catch (Exception e) {
			throw new ProductCatalogException("Create Product Failed - " + e.getMessage());
		}

	}

	@GetMapping("/approval-queue")
	public List<ApprovalQueue> getAllProductsInApprovalQueue() {

		try {
			return approvalQueueRepository.findAllByOrderByApprovalRequestDateAsc();
		} catch (Exception e) {
			throw new ProductCatalogException("Create Product Failed - " + e.getMessage());
		}

	}

	@PutMapping("/approval-queue/{approvalId}/approve")
	public ResponseEntity<String> approveProduct(@PathVariable Long approvalId) { //
		try {
			if (approvalId == null) {
				return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Approval ID cannot be null");
			}
			return productService.approveProduct(approvalId);
		} catch (Exception e) {
			throw new ProductCatalogException("Create Product Failed - " + e.getMessage());
		}
	}

	@PutMapping("/approval-queue/{approvalId}/reject")
	public ResponseEntity<String> rejectProduct(@PathVariable Long approvalId) { //
		try {
			if (approvalId == null) {
				return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Approval ID cannot be null");
			}
			return productService.rejectProduct(approvalId);
		} catch (Exception e) {
			throw new ProductCatalogException("Create Product Failed - " + e.getMessage());
		}
	}

}
