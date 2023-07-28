package com.productcatalog.app.exception;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import javax.validation.ConstraintViolationException;
import javax.ws.rs.ClientErrorException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import com.productcatalog.app.response.ErrorResource;

import lombok.extern.log4j.Log4j2;

@Log4j2
@Provider
public class GlobalExceptionHandler implements ExceptionMapper<Exception> {

	@Override
	public Response toResponse(final Exception exception) {

		log.error("Product Catalog caught exception - {}", exception);
		ErrorResource errorResource = new ErrorResource();
		errorResource.setTimeStamp((new Timestamp(System.currentTimeMillis())).toString());
		if (exception instanceof ResourceNotFoundException) {
			errorResource.setMessage(this.getMessage(exception));
			errorResource.setType(Status.NOT_FOUND.name());
			return Response.status(Status.NOT_FOUND).entity(errorResource).type(MediaType.APPLICATION_JSON).build();

		} else if (exception instanceof ClientErrorException) {
			ClientErrorException ce = (ClientErrorException) exception;
			errorResource.setMessage(this.getMessage(exception));
			return Response.status(ce.getResponse().getStatus()).entity(errorResource).type(MediaType.APPLICATION_JSON)
					.build();
		} else if (exception instanceof ConstraintViolationException) {
			errorResource.setMessage(this.getMessage(exception));
			return Response.status(Status.BAD_REQUEST).entity(errorResource).type(MediaType.APPLICATION_JSON).build();
		} else if (exception instanceof HttpMessageNotReadableException) {
			errorResource.setMessage(this.getMessage(exception));
			return Response.status(Status.BAD_REQUEST).entity(errorResource).type(MediaType.APPLICATION_JSON).build();

		} else if (exception instanceof IllegalArgumentException) {
			errorResource.setMessage(this.getMessage(exception));
			return Response.status(Status.BAD_REQUEST).entity(errorResource).type(MediaType.APPLICATION_JSON).build();
		} else if (exception instanceof MissingServletRequestParameterException) {
			errorResource.setMessage(this.getMessage(exception));
			return Response.status(Status.BAD_REQUEST).entity(errorResource).type(MediaType.APPLICATION_JSON).build();
		}
		else {
		errorResource.setMessage(this.getMessage(exception));
		return Response.status(Status.INTERNAL_SERVER_ERROR).entity(errorResource).type(MediaType.APPLICATION_JSON)
				.build();
	}
	}

	@ExceptionHandler(MethodArgumentTypeMismatchException.class)
	protected Response handleMethodArgumentTypeMismatch(MethodArgumentTypeMismatchException ex, WebRequest request) {
		List<String> details = new ArrayList<String>();
		ErrorResource errorResource = new ErrorResource();
		errorResource.setTimeStamp((new Timestamp(System.currentTimeMillis())).toString());
		errorResource.setMessage(this.getMessage(ex));
		return Response.status(Status.BAD_REQUEST).entity(errorResource).type(MediaType.APPLICATION_JSON).build();
	}

	private String getMessage(final Exception exception) {
		String message = (exception != null && exception.getMessage() != null) ? exception.getMessage()
				: "Some exception occurred while processing your request.";
		return message;
	}
}
