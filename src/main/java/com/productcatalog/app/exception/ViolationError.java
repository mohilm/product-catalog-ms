package com.productcatalog.app.exception;
import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@JsonInclude(Include.NON_NULL)
@AllArgsConstructor
@NoArgsConstructor
public class ViolationError implements Serializable {

	
	private static final long serialVersionUID = 5955657879212558619L;
	private String feild;
	private String message;
	private String type;
}
