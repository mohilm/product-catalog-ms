package com.productcatalog.app.response;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.productcatalog.app.model.Product;

public class ResponseHandler {
    public static ResponseEntity<Object> generateResponse(String message, HttpStatus status) {
        Map<String, Object> map = new HashMap<String, Object>();
            map.put("message", message);
            map.put("status", status.value());
            return new ResponseEntity<Object>(map,status);
    }

    
    public static ResponseEntity<List<Product>> generateResponseForEmptyProduct(String message, HttpStatus status) {
        List<Product> list = new ArrayList();
            
      return new ResponseEntity<List<Product>>(list,status);
    }

	
}