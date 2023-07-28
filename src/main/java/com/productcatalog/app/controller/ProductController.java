package com.productcatalog.app.controller;

import java.time.LocalDateTime;
import java.util.List;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
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

	/**
	 * 
	 * @returns a list of all active products
	 * 
	 *          The controller provides an endpoint "/api/v1/products" to retrieve a
	 *          list of active products.
	 * 
	 *          The method "listActiveProducts()" queries the database for products
	 *          with the "ACTIVE" status and orders them with the latest added
	 *          record first
	 * 
	 *          and returns them as a list.
	 * 
	 * 
	 */
	@GetMapping
	public List<Product> listActiveProducts() {
		try {
			return productRepository.findProductByStatusOrderByPostedDateDesc(Status.ACTIVE);
		} catch (Exception e) {
			throw new ProductCatalogException("Search Active Product Failed - " + e.getMessage());
		}
	}

	/**
	 * 
	 * @returns a list of all active products based on criteria passed in request params
	 * 
	 *          The controller provides an endpoint "/api/v1/products/search" to
	 *          search products based on various criteria. The method
	 *          "searchProducts()" accepts optional query parameters like
	 *          productName, minPrice, maxPrice, minPostedDate, and maxPostedDate.
	 *          It performs validation checks on the query parameters, ensuring that
	 *          minPrice <= maxPrice and minPostedDate <= maxPostedDate. The search
	 *          is performed by calling the "productRepository" with the search
	 *          criteria and returning the matched products.
	 * 
	 *          The method "listActiveProducts()" queries the database for products
	 *          with the "ACTIVE" status and orders them with the latest added
	 *          record first
	 * 
	 *          and returns them as a list.
	 * 
	 * 
	 * 
	 */
	@GetMapping("/search")
	public List<Product> searchProducts(
			@RequestParam(required = false) String productName, 
			@RequestParam(required = false) Double minPrice,
			@RequestParam(required = false) Double maxPrice,
			@Valid @RequestParam  (required = false) LocalDateTime minPostedDate,
			@Valid @RequestParam (required = false)  LocalDateTime maxPostedDate) {
		try {
		return productService.searchProductsBasedOnSearchCriteria(productName,minPrice,maxPrice, minPostedDate, maxPostedDate);
		} 
		catch (IllegalArgumentException e) {
			throw new IllegalArgumentException(e.getMessage());
		}
		catch (Exception e) {
			throw new ProductCatalogException("Search Product based on criteria failed - " + e.getMessage());
		}

	}

	/**
	 * 
	 * @creates a new product in the Database.
	 * 
	 *          The controller provides a POST endpoint "/api/v1/products" to create
	 *          a new product. The method "createProduct()" accepts a JSON payload
	 *          representing the new product. It performs validation using
	 *          the @Valid annotation based on the Product model class. The product
	 *          creation is done through the
	 *          "productService.createProductwithApprovalCheck()" method, which
	 *          includes approval checks.
	 * 
	 * 
	 */
	@PostMapping
	public ResponseEntity<String> createProduct(@RequestBody @Valid Product product) {
		try {
			return productService.createProductwithApprovalCheck(product);
		} catch (Exception e) {
			throw new ProductCatalogException("Create Product Failed - " + e.getMessage());
		}

	}

	/**
	 * 
	 * @update an existing product in the Database.
	 * 
	 *         The controller provides a PUT endpoint "/api/v1/products/{productId}"
	 *         to update an existing product by its ID. The method "updateProduct()"
	 *         accepts the product ID and a JSON payload representing the updated
	 *         product. It performs validation using the @Valid annotation based on
	 *         the Product model class. The product update is done through the
	 *         "productService.updateProductWithApprovalCheck()" method, which
	 *         includes approval checks.
	 * 
	 * 
	 */
	@PutMapping(value = "/{productId}")
	ResponseEntity<String> updateProduct(@PathVariable("productId") @Min(1) Long id,
			@Valid @RequestBody Product product) {
		try {
			return productService.updateProductWithApprovalCheck(id, product);
		} catch (Exception e) {
			throw new ProductCatalogException("Update Product Failed - " + e.getMessage());
		}
	}

	/**
	 * 
	 * @delete an existing product .
	 * 
	 *         The controller provides a DELETE endpoint
	 *         "/api/v1/products/{productId}" to delete a product by its ID. The
	 *         method "deleteProduct()" accepts the product ID as a path variable.
	 *         It ensures that the productId is not null and then calls the
	 *         "productService.deleteProductWithApproval()" method to handle
	 *         deletion with approval checks.
	 * 
	 * 
	 */
	@DeleteMapping("/{productId}")
	public ResponseEntity<String> deleteProduct(@PathVariable("productId") @NotNull Long productId) {

		try {
			if (productId == null) {
				return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Product ID cannot be null");
			}
			return productService.deleteProductWithApproval(productId);
		} catch (Exception e) {
			throw new ProductCatalogException("Delete Product Failed - " + e.getMessage());
		}

	}

	/**
	 * 
	 * @get all the records which are in pending approval status.
	 * 
	 *      The controller provides a PUT endpoint
	 *      "/api/v1/products/approval-queue/{approvalId}/approve" to approve a
	 *      product in the approval queue by its approval ID. The method
	 *      "approveProduct()" accepts the approval ID as a path variable. It
	 *      ensures that the approvalId is not null and then calls the
	 *      "productService.approveProduct()" method to handle the approval process.
	 * 
	 * 
	 */
	@GetMapping("/approval-queue")
	public List<ApprovalQueue> getAllProductsInApprovalQueue() {

		try {
			return approvalQueueRepository.findAllByOrderByApprovalRequestDateAsc();
		} catch (Exception e) {
			throw new ProductCatalogException(
					"Not able to fetch the pending records at this moment - " + e.getMessage());
		}

	}

	/**
	 * 
	 * @put this endpoint is used to approve the records.
	 * 
	 *      The controller provides a PUT endpoint
	 *      "/api/v1/products/approval-queue/{approvalId}/approve" to approve a
	 *      product in the approval queue by its approval ID. The method
	 *      "approveProduct()" accepts the approval ID as a path variable. It
	 *      ensures that the approvalId is not null and then calls the
	 *      "productService.approveProduct()" method to handle the approval process.
	 * 
	 * 
	 */
	@PutMapping("/approval-queue/{approvalId}/approve")
	public ResponseEntity<String> approveProduct(@PathVariable Long approvalId) { //
		try {
			if (approvalId == null) {
				return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Approval ID cannot be null");
			}
			return productService.approveProduct(approvalId);
		} catch (Exception e) {
			throw new ProductCatalogException("Unable to Approve Product - " + e.getMessage());
		}
	}

	/**
	 * 
	 * @put this endpoint is used to reject the records.
	 * 
	 *      The controller provides a PUT endpoint
	 *      "/api/v1/products/approval-queue/{approvalId}/reject" to reject a
	 *      product in the approval queue by its approval ID. The method
	 *      "rejectProduct()" accepts the approval ID as a path variable. It ensures
	 *      that the approvalId is not null and then calls the
	 *      "productService.rejectProduct()" method to handle the rejection process.
	 * 
	 * 
	 */
	@PutMapping("/approval-queue/{approvalId}/reject")
	public ResponseEntity<String> rejectProduct(@PathVariable Long approvalId) { //
		try {
			if (approvalId == null) {
				return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Approval ID cannot be null");
			}
			return productService.rejectProduct(approvalId);
		} catch (Exception e) {
			throw new ProductCatalogException("Unable to Reject Product - " + e.getMessage());
		}
	}

}
