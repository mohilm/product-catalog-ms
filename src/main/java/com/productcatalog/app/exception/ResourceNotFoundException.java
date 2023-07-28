package com.productcatalog.app.exception;

public class ResourceNotFoundException extends RuntimeException {

	private static final long serialVersionUID = 1L;
	
	private String systemErrorCode;
	private String type;
	private String message;
	private String timeStamp;

	public String getSystemErrorCode() {
		return systemErrorCode;
	}

	public void setSystemErrorCode(String systemErrorCode) {
		this.systemErrorCode = systemErrorCode;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String getTimeStamp() {
		return timeStamp;
	}

	public void setTimeStamp(String timeStamp) {
		this.timeStamp = timeStamp;
	}

	public ResourceNotFoundException(String message) {
		this.message = message;
	}
	
	/**
	 * ResourceNotFoundException to set with all the attributes
	 * 
	 * @param code     - 3 digit error code (100-999) to set that uniquely identify the error
	 * @param message  - error message describing the detail about the error
	 */
	public ResourceNotFoundException(String message, String code) {
		super(message);
		this.message = message;
		this.systemErrorCode = code;
	}
}
