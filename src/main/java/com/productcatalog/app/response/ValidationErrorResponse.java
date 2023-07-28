package com.productcatalog.app.response;

import java.util.ArrayList;
import java.util.List;

import com.productcatalog.app.exception.ViolationError;

import lombok.Data;


@Data
public class ValidationErrorResponse {

	  private List<ViolationError> violations = new ArrayList<>();

	  
	}



