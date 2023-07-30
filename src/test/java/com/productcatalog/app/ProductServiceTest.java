package com.productcatalog.app;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
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
}
