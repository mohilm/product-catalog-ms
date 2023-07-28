package com.productcatalog.app.controller;

import javax.validation.ConstraintViolationException;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import com.productcatalog.app.exception.ProductCatalogException;
import com.productcatalog.app.exception.ViolationError;
import com.productcatalog.app.response.ValidationErrorResponse;

@RestController
@Validated
@ControllerAdvice
class ValidateParametersController {

  // request mapping method omitted
  
  @ExceptionHandler(ConstraintViolationException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  ResponseEntity<String> handleConstraintViolationException(ConstraintViolationException e) {
    return new ResponseEntity<>("not valid due to validation error: " + e.getMessage(), HttpStatus.BAD_REQUEST);
  }
  
  @ExceptionHandler(ProductCatalogException.class)
  @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
  ResponseEntity<String> handleProductCatalogException(ProductCatalogException e) {
    return new ResponseEntity<>("Internal Server error: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
  }

  
  @ExceptionHandler(MethodArgumentNotValidException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  @ResponseBody
  ValidationErrorResponse onMethodArgumentNotValidException(
      MethodArgumentNotValidException e) {
    ValidationErrorResponse error = new ValidationErrorResponse();
    for (FieldError fieldError : e.getBindingResult().getFieldErrors()) {
    	error.getViolations().add(
        new ViolationError(fieldError.getField(), fieldError.getDefaultMessage(), fieldError.getCode()));
    }
    return error;
  }
  
  
  @ExceptionHandler(MethodArgumentTypeMismatchException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  @ResponseBody
  ValidationErrorResponse onMethodArgumentTypeMismatchException(
		  MethodArgumentTypeMismatchException e) {
    ValidationErrorResponse error = new ValidationErrorResponse();
    if(e.getPropertyName().equalsIgnoreCase("minPostedDate") || e.getPropertyName().equalsIgnoreCase("maxPostedDate")) {
    	 error.getViolations().add(new ViolationError(e.getPropertyName(), "Date format excepted in YYYY-MM-DDThh:mm:ss format", "date format mismatch"));
    	 return error;
    }
    error.getViolations().add(new ViolationError(e.getPropertyName(), e.getLocalizedMessage(), e.getErrorCode()));
    return error;
  }
  
  
  
  
  @ExceptionHandler(IllegalArgumentException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  @ResponseBody
  ValidationErrorResponse onIllegalArgumentException(
		  IllegalArgumentException e) {
    ValidationErrorResponse error = new ValidationErrorResponse();
    error.getViolations().add(new ViolationError(null,e.getMessage(), e.getLocalizedMessage()));
    return error;
  }
  
  
}