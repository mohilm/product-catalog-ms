package com.productcatalog.app.response;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Data;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Data
public class ErrorResource implements Serializable {

	private static final long serialVersionUID = 5955657879212558619L;

	private String systemErrorCode;
	private String type;
	private String message;
	private String timeStamp;
}
