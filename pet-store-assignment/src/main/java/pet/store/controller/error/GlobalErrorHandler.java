package pet.store.controller.error;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.NoSuchElementException;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;

@RestControllerAdvice
@Slf4j
public class GlobalErrorHandler {
	
	private enum LogStatus {
		STACK_TRACE, MESSAGE_ONLY
	}
	
	@Data
	private class ExceptionMessage {
		private String message;
		private String statusReason;
		private int statusCode;
		private String timestamp;
		private String uri;
	}
	
	private ExceptionMessage buildExceptionMessage(Exception ex, HttpStatus status,
			WebRequest wr, LogStatus logStatus) {
		String message = ex.toString();
		String statusReason = status.getReasonPhrase();  // returns the reason phrase of this status code
		int statusCode = status.value();  // returns the integer value of this status code
		String uri = null;
		String timestamp = 
			ZonedDateTime.now().format(DateTimeFormatter.RFC_1123_DATE_TIME);  // The RFC-1123 date-time formatter, such as 'Tue, 3 Jun 2008 11:05:30 GMT'. 
		
		if(wr instanceof ServletWebRequest swr) {
			uri = swr.getRequest().getRequestURI();
		}
		
		if(logStatus == LogStatus.MESSAGE_ONLY) {
			log.error("Exception: {}", ex.toString());
		}
		else {
			log.error("Exception: ", ex);
		}
		
		ExceptionMessage excMsg = new  ExceptionMessage();
		
		excMsg.setMessage(message);
		excMsg.setStatusCode(statusCode);
		excMsg.setStatusReason(statusReason);
		excMsg.setTimestamp(timestamp);
		excMsg.setUri(uri);
		
		return excMsg;
	}

	@ExceptionHandler(NoSuchElementException.class)
	@ResponseStatus(code = HttpStatus.NOT_FOUND)
	public ExceptionMessage handleNoSuchElementException(
		NoSuchElementException ex, WebRequest wr) {
		return buildExceptionMessage(ex, HttpStatus.NOT_FOUND, wr, LogStatus.MESSAGE_ONLY);
	}
	
}
