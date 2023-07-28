package com.productcatalog.app.service.impl;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.productcatalog.app.exception.ResourceNotFoundException;
import com.productcatalog.app.model.ApprovalQueue;
import com.productcatalog.app.model.Product;
import com.productcatalog.app.model.Status;
import com.productcatalog.app.repository.ApprovalQueueRepository;
import com.productcatalog.app.repository.ProductRepository;
import com.productcatalog.app.response.ProductCatalogConstants;
import com.productcatalog.app.service.ProductService;

import lombok.extern.log4j.Log4j2;

@Log4j2
@Service
public class ProductServiceImpl implements ProductService {
	@Autowired
	private ProductRepository productRepository;

	@Autowired
	private ApprovalQueueRepository approvalQueueRepository;

	@Override
	public List<Product> searchProductsBasedOnSearchCriteria(String productName, Double minPrice, Double maxPrice,
			LocalDateTime minPostedDate, LocalDateTime maxPostedDate) {
		// Validations: Ensure that the maxPrice is greater than or equal to minPrice
		if (minPrice != null && maxPrice != null && minPrice.compareTo(maxPrice) >= 0) {
			throw new IllegalArgumentException("maxPrice should be greater than or equal to minPrice");
		}

		// Validations: Ensure that the maxPostedDate is after or equal to minPostedDate
		if (minPostedDate != null && maxPostedDate != null && minPostedDate.isAfter(maxPostedDate)) {
			throw new IllegalArgumentException("maxPostedDate should be after or equal to minPostedDate");
		}
		log.info("After ValidationRetrieving product on the basis of the search criteria");
		return productRepository.findByNameEqualsIgnoreCaseOrPriceBetweenOrPostedDateBetween(productName, minPrice,
				maxPrice, minPostedDate, maxPostedDate);
	}

	public ResponseEntity<String> createProductwithApprovalCheck(Product product) {
		String response = null;
		if (product.getPrice() <= ProductCatalogConstants.MAX_PRICE) {

			if (product.getPrice() > 5000) {

				ApprovalQueue approvalQueue = new ApprovalQueue();
				product.setPostedDate(LocalDateTime.now());
				approvalQueue.setName(product.getName());
				approvalQueue.setPrice(product.getPrice());
				approvalQueue.setPostedDate(LocalDateTime.now());
				approvalQueue.setStatus(product.getStatus());
				approvalQueueRepository.save(approvalQueue);
				log.info("Creating product with price>5000, Hence added to approval queue");
				response = "Product Added To Approval Queue";

			} else {
				productRepository.save(product);
				log.info("Product Created successfully");
				response = "Product Created Successfully";
			}
			return new ResponseEntity<>(response, HttpStatus.OK);

		} else {

			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Product price exceeds 10,000. Hence not saved.");
		}
	}

	public ResponseEntity<String> updateProductWithApprovalCheck(Long productId, Product updatedProduct) {
		Optional<Product> productOptional = productRepository.findById(productId);
		if (productOptional.isPresent()) {
			Product product = productOptional.get();
			Double previousPrice = product.getPrice();

			if (updatedProduct.getPrice() > 10000) {
				return ResponseEntity.status(HttpStatus.BAD_REQUEST)
						.body("Product price exceeds 10,000. Hence not updated.");
			}
			// Check if the new price is more than 50% of the previous price
			Double fiftyPercentOfPreviousPrice = previousPrice * 0.5;
			if (updatedProduct.getPrice().compareTo(fiftyPercentOfPreviousPrice) > 0) {

				ApprovalQueue approvalQueue = new ApprovalQueue();
				approvalQueue.setName(updatedProduct.getName());
				approvalQueue.setPrice(updatedProduct.getPrice());
				approvalQueue.setPostedDate(LocalDateTime.now());
				approvalQueue.setStatus(updatedProduct.getStatus());
				approvalQueue.setProductId(productId);
				approvalQueueRepository.save(approvalQueue);
				log.info("Product price updated to 50% more than previous price, hence added to approval queue");
				productRepository.save(product);

				return ResponseEntity.ok()
						.body("Product Sent for Approval as price is higher than 50% of previous value");
			} else {
				product.setName(updatedProduct.getName());
				product.setPrice(updatedProduct.getPrice());
				product.setStatus(updatedProduct.getStatus());
				product.setPostedDate(LocalDateTime.now());
				log.info("Product Saved Successfully");
				productRepository.save(product);
				return new ResponseEntity<>("Product Updated Successfully", HttpStatus.OK);
			}
		} else {
			throw new ResourceNotFoundException("No oduct not found with ID: " + productId + "found");
		}
	}

	public ResponseEntity<String> deleteProductWithApproval(Long productId) {
		Product prod = productRepository.findById(productId)
				.orElseThrow(() -> new ResourceNotFoundException("No Product with ID :" + productId + " found!"));
		ApprovalQueue approvalQueue = new ApprovalQueue();
		prod.setStatus(Status.INACTIVE);
		approvalQueue.setProductId(productId);
		approvalQueue.setName(prod.getName());
		approvalQueue.setPrice(prod.getPrice());
		approvalQueue.setPostedDate(LocalDateTime.now());
		approvalQueue.setStatus(prod.getStatus());
		log.info("Product requested to be deleted, Hence added to approval queue");
		approvalQueueRepository.save(approvalQueue);
		productRepository.save(prod);
		return new ResponseEntity<>("Product Deleted successfully", HttpStatus.OK);

	}

	public ResponseEntity<String> approveProduct(Long approvalId) {
		ApprovalQueue approveQueueData = approvalQueueRepository.findById(approvalId).orElseThrow(
				() -> new ResourceNotFoundException("No ID in Approval Queue with ID :" + approvalId + " found!"));
		Long productId = approveQueueData.getProductId();
		if (productId != null) {
			Optional<Product> productOptional = productRepository.findById(productId);
			if (productOptional.isPresent()) {
				Product product = productOptional.get();
				product.setName(approveQueueData.getName());
				product.setPrice(approveQueueData.getPrice());
				product.setStatus(approveQueueData.getStatus());
				product.setPostedDate(LocalDateTime.now());
				log.info("Product approved successfully and product updated");
				productRepository.save(product);

			}
		} else {
			Product product = new Product();
			product.setName(approveQueueData.getName());
			product.setPrice(approveQueueData.getPrice());
			product.setStatus(approveQueueData.getStatus());
			product.setPostedDate(LocalDateTime.now());
			log.info("Product approved successfully and product added");
			productRepository.save(product);
		}
		approvalQueueRepository.delete(approveQueueData);
		return new ResponseEntity<>("Product approved successfully and product updated", HttpStatus.OK);
	}

	public ResponseEntity<String> rejectProduct(Long approvalId) {
		ApprovalQueue approveQueueData = approvalQueueRepository.findById(approvalId).orElseThrow(
				() -> new ResourceNotFoundException("No ID in Approval Queue with ID :" + approvalId + " found!"));
		log.info("Product rejected. Product state unchanged");
		approvalQueueRepository.delete(approveQueueData);
		return new ResponseEntity<>("Product Rejected", HttpStatus.OK);

	}
}
