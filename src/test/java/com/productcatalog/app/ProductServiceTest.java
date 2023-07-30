package com.productcatalog.app;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.productcatalog.app.exception.ResourceNotFoundException;
import com.productcatalog.app.model.ApprovalQueue;
import com.productcatalog.app.model.Product;
import com.productcatalog.app.model.Status;
import com.productcatalog.app.repository.ApprovalQueueRepository;
import com.productcatalog.app.repository.ProductRepository;
import com.productcatalog.app.service.impl.ProductServiceImpl;

public class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private ApprovalQueueRepository approvalQueueRepository;

    @InjectMocks
    private ProductServiceImpl productService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testUpdateProductWithApprovalCheck_ProductFoundAndApproved() {
        Long productId = 1L;
        Double previousPrice = 5000.0;
        Double newPrice = 8000.0;
        Product existingProduct = new Product("Existing Product", previousPrice, Status.ACTIVE,LocalDateTime.parse("2023-02-01T12:00:00"));
        Product updatedProduct = new Product("Updated Product", newPrice, Status.ACTIVE,LocalDateTime.parse("2023-02-01T12:00:00"));
        
        // Mock the behavior of productRepository.findById
        when(productRepository.findById(productId)).thenReturn(Optional.of(existingProduct));
        
        // Call the method to be tested
        ResponseEntity<Object> responseEntity = productService.updateProductWithApprovalCheck(productId, updatedProduct);

        // Verify that the approval queue and product repositories are called appropriately
        verify(approvalQueueRepository, times(1)).save(any(ApprovalQueue.class));
        verify(productRepository, times(1)).save(existingProduct);

        // Verify the response message and status code
        Map<String, Object> responseMap = (Map<String, Object>) responseEntity.getBody();
        String actualMessage = (String) responseMap.get("message");
        
        
        assertEquals("Product Sent for Approval as price is higher than 50% of previous value", actualMessage);
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
    }

    @Test
    public void testUpdateProductWithApprovalCheck_ProductFoundAndUpdated() {
        Long productId = 1L;
        Double previousPrice = 3000.0;
        Double newPrice = 3500.0;
        Product existingProduct = new Product("Existing Product", previousPrice, Status.ACTIVE,LocalDateTime.parse("2023-02-01T12:00:00"));
        Product updatedProduct = new Product("Updated Product", newPrice, Status.ACTIVE, LocalDateTime.parse("2023-02-03T12:00:00"));
        
        // Mock the behavior of productRepository.findById
        when(productRepository.findById(productId)).thenReturn(Optional.of(existingProduct));
        
        // Call the method to be tested
        ResponseEntity<Object> responseEntity = productService.updateProductWithApprovalCheck(productId, updatedProduct);

        // Verify that the product repository is called appropriately
        verify(productRepository, times(1)).save(existingProduct);

        // Verify the response message and status code
        Map<String, Object> responseMap = (Map<String, Object>) responseEntity.getBody();
        String actualMessage = (String) responseMap.get("message");
        
        assertEquals("Product Updated Successfully", actualMessage);
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
    }

    @Test
    public void testUpdateProductWithApprovalCheck_ProductNotFound() {
        Long productId = 1L;
        Product updatedProduct = new Product("Updated Product", 8000.0, Status.ACTIVE, LocalDateTime.parse("2023-02-01T12:00:00"));
        
        // Mock the behavior of productRepository.findById
        when(productRepository.findById(productId)).thenReturn(Optional.empty());
        
        // Call the method to be tested and expect an exception
        assertThrows(ResourceNotFoundException.class, () -> productService.updateProductWithApprovalCheck(productId, updatedProduct));

        // Verify that the product repository is not called
        verify(productRepository, never()).save(any(Product.class));
    }

    @Test
    public void testUpdateProductWithApprovalCheck_PriceExceeds10000() {
        Long productId = 1L;
        Double previousPrice = 5000.0;
        Double newPrice = 12000.0;
        Product existingProduct = new Product("Existing Product", previousPrice, Status.ACTIVE, LocalDateTime.parse("2023-02-01T12:00:00"));
        Product updatedProduct = new Product("Updated Product", newPrice, Status.ACTIVE, LocalDateTime.parse("2023-03-01T12:00:00"));
        
        // Mock the behavior of productRepository.findById
        when(productRepository.findById(productId)).thenReturn(Optional.of(existingProduct));
        
        // Call the method to be tested and expect an exception
        assertThrows(IllegalArgumentException.class, () -> productService.updateProductWithApprovalCheck(productId, updatedProduct));

        // Verify that the product repository is not called
        verify(productRepository, never()).save(any(Product.class));
    }
    
    
    // test cases for product search based on criteria 
    
    @Test
    public void testSearchProductsBasedOnSearchCriteria_ValidCriteria() {
        // Test data
        String productName = "testProduct";
        Double minPrice = 100.0;
        Double maxPrice = 500.0;
        LocalDateTime minPostedDate = LocalDateTime.parse("2023-01-01T00:00:00");
        LocalDateTime maxPostedDate = LocalDateTime.parse("2023-07-01T00:00:00");

        // Mock the repository method call
        List<Product> mockProducts = new ArrayList<>();
        // Add some products to the list (you can add more for different scenarios)
        mockProducts.add(new Product("Product A", 200.0,Status.ACTIVE, LocalDateTime.parse("2023-02-01T12:00:00")));
        mockProducts.add(new Product("Product B", 300.0, Status.ACTIVE, LocalDateTime.parse("2023-03-15T10:30:00")));
        Mockito.when(productRepository.findByNameEqualsIgnoreCaseOrPriceBetweenOrPostedDateBetweenAndStatus(
               productName, minPrice, maxPrice, minPostedDate, maxPostedDate ,Status.ACTIVE)).thenReturn(mockProducts);

        // Perform the service method call
        ResponseEntity<List<Product>> result;
        try {
            result = productService.searchProductsBasedOnSearchCriteria(productName, minPrice, maxPrice,
                    minPostedDate, maxPostedDate);
        } catch (Exception e) {
            fail("Unexpected exception occurred: " + e.getMessage());
            return; // Exit early as the test has failed
        }

        // Assertions
        assertNotNull(result);
        assertEquals(2, ((Map<String, Object>) result).size()); // Ensure the correct number of products is returned
        // Add more specific assertions based on your use case and test data
    }

    @Test
    public void testSearchProductsBasedOnSearchCriteria_InvalidMinMaxPrice() {
        // Test data with invalid minPrice and maxPrice
        String productName = "testProduct";
        Double minPrice = 500.0;
        Double maxPrice = 100.0;
        LocalDateTime minPostedDate = null;
        LocalDateTime maxPostedDate = null;

        // Perform the service method call and expect an IllegalArgumentException
        assertThrows(IllegalArgumentException.class, () -> {
            productService.searchProductsBasedOnSearchCriteria(productName, minPrice, maxPrice,
                    minPostedDate, maxPostedDate);
        });
    }

    @Test
    public void testSearchProductsBasedOnSearchCriteria_InvalidMinMaxPostedDate() {
        // Test data with invalid minPostedDate and maxPostedDate
        String productName = "testProduct";
        Double minPrice = null;
        Double maxPrice = null;
        LocalDateTime minPostedDate = LocalDateTime.parse("2023-12-01T00:00:00");
        LocalDateTime maxPostedDate = LocalDateTime.parse("2023-07-01T00:00:00");

        // Perform the service method call and expect an IllegalArgumentException
        assertThrows(IllegalArgumentException.class, () -> {
            productService.searchProductsBasedOnSearchCriteria(productName, minPrice, maxPrice,
                    minPostedDate, maxPostedDate);
        });
    }
    

// test cases for create product 
	
    @Test
    public void testCreateProductwithApprovalCheck_ValidProduct() {
        // Test data
        Product product = new Product("Test Product", 3000.0, Status.ACTIVE, LocalDateTime.now());

        // Call the method to be tested
        ResponseEntity<Object> responseEntity = productService.createProductwithApprovalCheck(product);

        // Verify that the product repository is called
        verify(productRepository, times(1)).save(product);

        // Verify the response message and status code
        Map<String, Object> responseMap = (Map<String, Object>) responseEntity.getBody();
        String actualMessage = (String) responseMap.get("message");
        
        assertEquals("Product Created Successfully", actualMessage);
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
    }

    @Test
    public void testCreateProductwithApprovalCheck_PriceExceeds5000() {
        // Test data
        Product product = new Product("Test Product", 6000.0, Status.ACTIVE, LocalDateTime.now());

        // Call the method to be tested
        ResponseEntity<Object> responseEntity = productService.createProductwithApprovalCheck(product);

        // Verify that the approval queue repository is called
        verify(approvalQueueRepository, times(1)).save(any(ApprovalQueue.class));

        // Verify the response message and status code
        Map<String, Object> responseMap = (Map<String, Object>) responseEntity.getBody();
        String actualMessage = (String) responseMap.get("message");
        
        assertEquals("Product Added To Approval Queue as price is more than 5000", actualMessage);
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
    }

    @Test
    public void testCreateProductwithApprovalCheck_PriceExceeds10000() {
        // Test data with a price exceeding 10,000
        Product product = new Product("Test Product", 12000.0, Status.ACTIVE, LocalDateTime.now());

        // Call the method to be tested and expect an IllegalArgumentException
        assertThrows(IllegalArgumentException.class, () -> {
            productService.createProductwithApprovalCheck(product);
        });

        // Verify that neither the product repository nor the approval queue repository is called
        verify(productRepository, never()).save(any(Product.class));
        verify(approvalQueueRepository, never()).save(any(ApprovalQueue.class));
    }

    
    // delete product test cases 
    
    @Test
    public void testDeleteProductWithApproval_ProductFound() {
        // Test data
        Long productId = 1L;
        Product product = new Product("Test Product", 1000.0, Status.ACTIVE, LocalDateTime.now());

        // Mock the repository method calls
        Mockito.when(productRepository.findById(productId)).thenReturn(Optional.of(product));

        // Call the method to be tested
        ResponseEntity<Object> responseEntity = productService.deleteProductWithApproval(productId);

        // Verify that the product repository and approval queue repository are called
        verify(productRepository, times(1)).findById(productId);
        verify(approvalQueueRepository, times(1)).save(any(ApprovalQueue.class));
        verify(productRepository, times(1)).save(product);

        // Verify the response message and status code
        Map<String, Object> responseMap = (Map<String, Object>) responseEntity.getBody();
        String actualMessage = (String) responseMap.get("message");
        
        assertEquals("Product Deleted successfully", actualMessage);
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals(Status.INACTIVE, product.getStatus());
    }

    @Test
    public void testDeleteProductWithApproval_ProductNotFound() {
        // Test data with a non-existent productId
        Long productId = 10L;

        // Mock the repository method calls
        Mockito.when(productRepository.findById(productId)).thenReturn(Optional.empty());

        // Call the method to be tested and expect a ResourceNotFoundException
        assertThrows(ResourceNotFoundException.class, () -> {
            productService.deleteProductWithApproval(productId);
        });

        // Verify that the product repository and approval queue repository are not called
        verify(productRepository, times(1)).findById(productId);
        verify(approvalQueueRepository, never()).save(any(ApprovalQueue.class));
        verify(productRepository, never()).save(any(Product.class));
    }

    
    // test cases to approve product 
    
    
    @Test
    public void testApproveProduct_ValidApprovalId_ProductIdNotNull() {
        // Test data
        Long approvalId = 1L;
        Long productId = 2L;
        String productName = "Test Product";
        Double productPrice = 1000.0;
        Status productStatus = Status.ACTIVE;

        // Mock the repository method calls
        ApprovalQueue approveQueueData = new ApprovalQueue(productName, productPrice, productStatus, LocalDateTime.now(), productId );
        Mockito.when(approvalQueueRepository.findById(approvalId)).thenReturn(Optional.of(approveQueueData));
        Mockito.when(productRepository.findById(productId)).thenReturn(Optional.of(new Product()));

        // Call the method to be tested
        ResponseEntity<Object> responseEntity = productService.approveProduct(approvalId);

        // Verify that the product repository and approval queue repository are called
        verify(approvalQueueRepository, times(1)).findById(approvalId);
        verify(productRepository, times(1)).findById(productId);
        verify(productRepository, times(1)).save(any(Product.class));
        verify(approvalQueueRepository, times(1)).delete(approveQueueData);

        // Verify the response message and status code
        Map<String, Object> responseMap = (Map<String, Object>) responseEntity.getBody();
        String actualMessage = (String) responseMap.get("message");
        
        assertEquals("Product approved successfully and product updated", actualMessage);
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
    }

    @Test
    public void testApproveProduct_ValidApprovalId_ProductIdNull() {
        // Test data with a null productId
        Long approvalId = 1L;
        Long productId = null;
        String productName = "Test Product";
        Double productPrice = 1000.0;
        Status productStatus = Status.ACTIVE;

        // Mock the repository method calls
        ApprovalQueue approveQueueData = new ApprovalQueue(productName, productPrice, productStatus, LocalDateTime.now(), productId );
        Mockito.when(approvalQueueRepository.findById(approvalId)).thenReturn(Optional.of(approveQueueData));

        // Call the method to be tested
        ResponseEntity<Object> responseEntity = productService.approveProduct(approvalId);

        // Verify that the product repository and approval queue repository are called
        verify(approvalQueueRepository, times(1)).findById(approvalId);
        verify(productRepository, times(1)).save(any(Product.class));
        verify(approvalQueueRepository, times(1)).delete(approveQueueData);

        // Verify the response message and status code
        Map<String, Object> responseMap = (Map<String, Object>) responseEntity.getBody();
        String actualMessage = (String) responseMap.get("message");
        
        assertEquals("Product approved successfully and product added", actualMessage);
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
    }

    @Test
    public void testApproveProduct_NullApprovalId() {
        // Call the method to be tested and expect an IllegalArgumentException
        assertThrows(IllegalArgumentException.class, () -> {
            productService.approveProduct(null);
        });

        // Verify that neither the product repository nor the approval queue repository is called
        verify(approvalQueueRepository, never()).findById(anyLong());
        verify(productRepository, never()).findById(anyLong());
        verify(productRepository, never()).save(any(Product.class));
        verify(approvalQueueRepository, never()).delete(any(ApprovalQueue.class));
    }

    @Test
    public void testApproveProduct_InvalidApprovalId() {
        // Test data with an invalid (non-existent) approvalId
        Long approvalId = 10L;

        // Mock the repository method calls
        Mockito.when(approvalQueueRepository.findById(approvalId)).thenReturn(Optional.empty());

        // Call the method to be tested and expect a ResourceNotFoundException
        assertThrows(ResourceNotFoundException.class, () -> {
            productService.approveProduct(approvalId);
        });

        // Verify that neither the product repository nor the approval queue repository is called
        verify(approvalQueueRepository, times(1)).findById(approvalId);
        verify(productRepository, never()).findById(anyLong());
        verify(productRepository, never()).save(any(Product.class));
        verify(approvalQueueRepository, never()).delete(any(ApprovalQueue.class));
    }
    
    
    // test cases to reject product 
    
    
    @Test
    public void testRejectProduct_ValidApprovalId() {
        // Test data
        Long approvalId = 1L;
        Long productId = 2L;
        String productName = "Test Product";
        Double productPrice = 1000.0;
        Status productStatus = Status.ACTIVE;
        LocalDateTime postedDate = LocalDateTime.now();
        
     // Mock the repository method calls
        Mockito.when(approvalQueueRepository.findById(approvalId)).thenReturn(Optional.empty());


        // Mock the repository method calls
        ApprovalQueue approveQueueData = new ApprovalQueue(productName, productPrice, productStatus, LocalDateTime.now(), productId );
        Mockito.when(approvalQueueRepository.findById(approvalId)).thenReturn(Optional.of(approveQueueData));

        // Call the method to be tested
        ResponseEntity<Object> responseEntity = productService.rejectProduct(approvalId);

        // Verify that the approval queue repository is called and the product state is unchanged
        verify(approvalQueueRepository, times(1)).findById(approvalId);
        verify(approvalQueueRepository, times(1)).delete(approveQueueData);

        // Verify the response message and status code
        assertEquals("Product Rejected Successfully", responseEntity.getBody());
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
    }

    @Test
    public void testRejectProduct_NullApprovalId() {
        // Call the method to be tested and expect an IllegalArgumentException
        assertThrows(IllegalArgumentException.class, () -> {
            productService.rejectProduct(null);
        });

        // Verify that the approval queue repository is not called
        verify(approvalQueueRepository, never()).findById(anyLong());
        verify(approvalQueueRepository, never()).delete(any(ApprovalQueue.class));
    }

    @Test
    public void testRejectProduct_InvalidApprovalId() {
        // Test data with an invalid (non-existent) approvalId
        Long approvalId = 10L;

        // Mock the repository method calls
        Mockito.when(approvalQueueRepository.findById(approvalId)).thenReturn(Optional.empty());

        // Call the method to be tested and expect a ResourceNotFoundException
        assertThrows(ResourceNotFoundException.class, () -> {
            productService.rejectProduct(approvalId);
        });

        // Verify that neither the approval queue repository nor the product repository is called
        verify(approvalQueueRepository, times(1)).findById(approvalId);
        verify(approvalQueueRepository, never()).delete(any(ApprovalQueue.class));
    }
    
}
