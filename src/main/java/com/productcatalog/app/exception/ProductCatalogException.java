package com.productcatalog.app.exception;

import java.util.List;
import java.util.Map;

/**
 * This is a RuntimeException extension which allows to carry additional attributes of exceptions along with builder
 * classes to create exception.
 *
 */
public class ProductCatalogException extends RuntimeException {

	/** default serial version uid */
	private static final long serialVersionUID = 1L;

	/** numeric unique error code set by application */
	private String code;

	/** error mnemonic describing error classification */
	private String type;

	/** source system caused the error */
	private String sourceSystem;

	/** Additional K/V pairs for error info to pass metadata about the error caused */
	private Map<String, String> errorInfos;

	/** severity of the error */
	private Severity severity = Severity.SYSTEM;

	/**
	 * HTTP status code, used in context of Web where exception is mapped to a specific HTTP status code
	 */
	private int status = -1;
	

	/** additional causes that need to be captured **/
	private List<String> causes;

	/**
	 * Default constructor
	 */
	public ProductCatalogException() {
		super();
	}

	/**
	 * ProductCatalogException with message
	 * 
	 * @param message - message to set
	 */
	public ProductCatalogException(String message) {
		super(message);
	}

	/**
	 * ProductCatalogException with message and root cause
	 * 
	 * @param message - message to set
	 * @param cause   - root cause
	 */
	public ProductCatalogException(String message, Throwable cause) {
		super(message, cause);
	}
	


	/**
	 * ProductCatalogException to set with all the attributes
	 * 
	 * @param code     - 3 digit error code (100-999) to set that uniquely identify the error
	 * @param type     - error classification that uniquely identifies or can even groups them into a generic category
	 * @param severity - severity of the error look at the different types of Severity
	 * @param status   - HTTP status code need to be set
	 * @param causes   - root cause messages to be set
	 * @param message  - error message describing the detail about the error
	 * @param cause    - root cause Throwable in case the exception chaining is done by applications
	 */
	public ProductCatalogException(String code, String type, Severity severity, int status, List<String> causes,
			String message, Throwable cause) {
		super(message, cause);
		this.code = code;
		this.type = type;
		this.severity = severity;
		this.status = status;
		this.causes = causes;
	}

	/**
	 * ProductCatalogException to set with all the attributes
	 * 
	 * @param code         - 3 digit error code (100-999) to set that uniquely identify the error
	 * @param type         - error classification that uniquely identifies or can even groups them into a generic
	 *                     category
	 * @param severity     - severity of the error look at the different types of Severity
	 * @param status       - HTTP status code need to be set
	 * @param causes       - root cause messages to be set
	 * @param message      - error message describing the detail about the error
	 * @param sourceSystem - source system that caused this error ex : API | SOR_NAME etc
	 * @param cause        - root cause Throwable in case the exception chaining is done by applications
	 */
	public ProductCatalogException(String code, String type, Severity severity, int status, List<String> causes,
			String message, String sourceSystem, Throwable cause) {
		this(code, type, severity, status, causes, message, cause);
		this.sourceSystem = sourceSystem;
	}

	/**
	 * ProductCatalogException to set with all the attributes
	 * 
	 * @param code         - 3 digit error code (100-999) to set that uniquely identify the error
	 * @param type         - error classification that uniquely identifies or can even groups them into a generic
	 *                     category
	 * @param severity     - severity of the error look at the different types of Severity
	 * @param status       - HTTP status code need to be set
	 * @param causes       - root cause messages to be set
	 * @param message      - error message describing the detail about the error
	 * @param sourceSystem - source system that caused this error ex : API | SOR_NAME etc
	 * @param errorInfos   - error info Map of K/V pairs to provide additional meta-data about the error
	 * @param cause        - root cause Throwable in case the exception chaining is done by applications
	 */
	public ProductCatalogException(String code, String type, Severity severity, int status, List<String> causes,
			String message, String sourceSystem, Map<String, String> errorInfos, Throwable cause) {
		this(code, type, severity, status, causes, message, sourceSystem, cause);
		this.errorInfos = errorInfos;
	}

	/**
	 * Error severity for ProductCatalogException
	 *
	 * @author Ajit Das
	 *
	 */
	public static enum Severity {

		/** retriable errors (bad request, validation errors, service unavailable etc) */
		RETRIABLE(6, "Minor Error. Correct and Retry"),

		/** business errors which can't be retried with same input */
		NON_RETRIABLE(7, "Un-retry able Business Logic Failure"),

		/**
		 * any generic system errors, majority of application errors fall under this category
		 */
		SYSTEM(8, "System Malfunction, Retry After Time lag"),

		/** business errors which can't retried or processed */
		FATAL(9, "Fatal Error");

		/** severity value */
		private int value;

		/** severity description */
		private String description;

		private Severity(final int value, final String description) {
			this.value = value;
			this.description = description;
		}

		/**
		 * severity value
		 * 
		 * @return severity value
		 */
		public int value() {
			return value;
		}

		/**
		 * severity description
		 * 
		 * @return severity description
		 */
		public String description() {
			return description;
		}

		/**
		 * Getter with severity integer
		 * 
		 * @param severity - severity value
		 * @return Enum mapped with severity code
		 */
		public static Severity valueOf(final int severity) {
			for (Severity instance : Severity.values()) {
				if (instance.value == severity) {
					return instance;
				}
			}
			return null;
		}

		/**
		 * String representation of Enum
		 * 
		 * @return string value of enum
		 */
		@Override
		public String toString() {
			return String.valueOf(value);
		}

	}


	/**
	 * Getter for the error code that uniquely identifies the error
	 * 
	 * @return - error code
	 */
	public String getCode() {
		return code;
	}

	/**
	 * Getter for the error type
	 * 
	 * @return - error type
	 */
	public String getType() {
		return type;
	}

	/**
	 * Getter for source system name
	 * 
	 * @return - source system name
	 */
	public String getSourceSystem() {
		return sourceSystem;
	}

	/**
	 * Getter for error Severity
	 * 
	 * @return - error Severity
	 */
	public Severity getSeverity() {
		return severity;
	}

	/**
	 * Getter for HTTP status
	 * 
	 * @return - http status code
	 */
	public int getStatus() {
		return status;
	}

	/**
	 * Getter for root cause exception messages
	 * 
	 * @return - root cause messages
	 */
	public List<String> getCauses() {
		return causes;
	}

	/**
	 * Getter for error info K/V map
	 * 
	 * @return Map of KV pairs for error info
	 */
	public Map<String, String> getErrorInfos() {
		return errorInfos;
	}

}
