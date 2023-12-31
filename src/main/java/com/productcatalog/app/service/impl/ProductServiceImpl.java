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
import com.productcatalog.app.response.ResponseHandler;
import com.productcatalog.app.service.ProductService;

import lombok.extern.log4j.Log4j2;

@Log4j2
@Service
public class ProductServiceImpl implements ProductService {
	@Autowired
	private ProductRepository productRepository;

	@Autowired
	private ApprovalQueueRepository approvalQueueRepository;

	

	public ResponseEntity<Object> createProductwithApprovalCheck(Product product) {
		String response = null;
		if(product.getName()==null) {
			throw new IllegalArgumentException("Product name cannot be null");
		}
		if (product.getPrice()!=null) {
			if(product.getPrice() <= ProductCatalogConstants.MAX_PRICE) {
			if (product.getPrice() > 5000) {
				ApprovalQueue approvalQueue = new ApprovalQueue();
				product.setPostedDate(LocalDateTime.now());
				approvalQueue.setName(product.getName());
				approvalQueue.setPrice(product.getPrice());
				approvalQueue.setPostedDate(LocalDateTime.now());
				approvalQueue.setStatus(product.getStatus());
				approvalQueueRepository.save(approvalQueue);
				log.info("Creating product with price>5000, Hence added to approval queue");
				response = "Product Added To Approval Queue as price is more than 5000";

			} else {
				productRepository.save(product);
				log.info("Product Created successfully");
				response = "Product Created Successfully";
			}
			return ResponseHandler.generateResponse(response, HttpStatus.OK);
			}
			 else {
					
					throw new IllegalArgumentException("Product price exceeds 10,000. Hence not saved.");
				
			}
			
		}
		else {
			productRepository.save(product);
			log.info("Product Created successfully");
			response = "Product Created Successfully";
			return ResponseHandler.generateResponse(response, HttpStatus.OK);
		}
			
		}

	public ResponseEntity<Object> updateProductWithApprovalCheck(Long productId, Product updatedProduct) {
		Optional<Product> productOptional = productRepository.findById(productId);
		if (productOptional.isPresent()) {
			Product product = productOptional.get();
			Double previousPrice = product.getPrice();

			if (updatedProduct.getPrice() > 10000) {
				throw new IllegalArgumentException("Product price exceeds 10,000. Hence not updated.");
				
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
				return ResponseHandler.generateResponse("Product Sent for Approval as price is higher than 50% of previous value", HttpStatus.OK);
				
			} else {
				product.setName(updatedProduct.getName());
				product.setPrice(updatedProduct.getPrice());
				product.setStatus(updatedProduct.getStatus());
				product.setPostedDate(LocalDateTime.now());
				log.info("Product Saved Successfully");
				productRepository.save(product);
				return ResponseHandler.generateResponse("Product Updated Successfully", HttpStatus.OK);

				
			}
		} else {
			throw new ResourceNotFoundException("No product not found with ID: " + productId + "found");
		}
	}

	public ResponseEntity<Object> deleteProductWithApproval(Long productId) {
		Product prod = productRepository.findById(productId)
				.orElseThrow(() -> new ResourceNotFoundException("No Product with ID " + productId + " found!"));
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
		return ResponseHandler.generateResponse("Product Deleted successfully", HttpStatus.OK);
		

	}

	public ResponseEntity<Object> approveProduct(Long approvalId) {
	    if (approvalId == null) {
	        throw new IllegalArgumentException("Approval ID cannot be null");
	    }

	    ApprovalQueue approveQueueData = approvalQueueRepository.findById(approvalId).orElseThrow(
	            () -> new ResourceNotFoundException("No ID in Approval Queue with ID " + approvalId + " found!"));

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
	            approvalQueueRepository.delete(approveQueueData);
	            return ResponseHandler.generateResponse("Product approved successfully and product updated", HttpStatus.OK);
	        }
	    } else {
	        Product product = new Product();
	        product.setName(approveQueueData.getName());
	        product.setPrice(approveQueueData.getPrice());
	        product.setStatus(approveQueueData.getStatus());
	        product.setPostedDate(LocalDateTime.now());
	        log.info("Product approved successfully and product added");
	        productRepository.save(product);
	        approvalQueueRepository.delete(approveQueueData);
	        return ResponseHandler.generateResponse("Product approved successfully and product added", HttpStatus.OK);
	    }

	    // If neither of the above conditions is satisfied, delete the approvalQueueData and return
	    approvalQueueRepository.delete(approveQueueData);
	    return ResponseHandler.generateResponse("Product approval process completed", HttpStatus.OK);
	}


	public ResponseEntity<Object>rejectProduct(Long approvalId) {
		if (approvalId == null) {
			throw new IllegalArgumentException("Approval ID cannot be null");
		}
		ApprovalQueue approveQueueData = approvalQueueRepository.findById(approvalId).orElseThrow(
				() -> new ResourceNotFoundException("No ID in Approval Queue with ID " + approvalId + " found!"));
		log.info("Product rejected. Product state unchanged");
		approvalQueueRepository.delete(approveQueueData);
		return ResponseHandler.generateResponse("Product Rejected Successfully", HttpStatus.OK);


	}

	@Override
	public ResponseEntity<List<Product>> searchProductsBasedOnSearchCriteria(String productName, Double minPrice, Double maxPrice,
			LocalDateTime minPostedDate, LocalDateTime maxPostedDate) throws Exception{
		if(minPrice==null && maxPrice ==null &&  productName==null && minPostedDate==null && maxPostedDate==null) {
			log.info("Validation successful : Fetching all active products as search criteria is empty");
			List<Product> products =  productRepository.findProductByStatusOrderByPostedDateDesc(Status.ACTIVE);
			return ResponseEntity.ok(products);
			
			}
		// Validations: Ensure that the maxPrice is greater than or equal to minPrice
		else if (minPrice != null && maxPrice != null && minPrice.compareTo(maxPrice) >= 0) {
			log.info("Validation failed :: Max Price should be greater than min price");
			throw new IllegalArgumentException("maxPrice should be greater than or equal to minPrice");
		}

		// Validations: Ensure that the maxPostedDate is after or equal to minPostedDate
		else if(minPostedDate != null && maxPostedDate != null && minPostedDate.isAfter(maxPostedDate)) {
			log.info("Validation failed :: Max posted date should be after min posted date");
			throw new IllegalArgumentException("maxPostedDate should be after or equal to minPostedDate");
		}
		 
		else {
			log.info("Validation successful : Fetching active products based on search criteria");
			List<Product> products =  productRepository.findByNameEqualsIgnoreCaseOrPriceBetweenOrPostedDateBetweenAndStatus(productName, minPrice,
					maxPrice, minPostedDate, maxPostedDate,Status.ACTIVE);
			if(products.isEmpty()) {
				return ResponseHandler.generateResponseForEmptyProduct("No records found", HttpStatus.NO_CONTENT);
			}
			return ResponseEntity.ok(products);
			
		
		}
		
	}

	
	
}
