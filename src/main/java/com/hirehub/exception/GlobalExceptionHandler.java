package com.hirehub.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import com.hirehub.exception.DuplicateEmailException;
import java.util.*;

@RestControllerAdvice
public class GlobalExceptionHandler {

	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<?> handleValidationException(MethodArgumentNotValidException exception) {
		HashMap<String, String> errors = new HashMap<>();
		for (FieldError error : exception.getBindingResult().getFieldErrors()) {

			errors.put(error.getField(), error.getDefaultMessage());

		}
		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errors);
	}

	@ExceptionHandler(DuplicateEmailException.class)
	public ResponseEntity<?> handleEmailException(DuplicateEmailException exception) {

		return ResponseEntity.status(HttpStatus.CONFLICT).body(exception.getMessage());
	}
	
	@ExceptionHandler(UserNotFoundException.class)
	public ResponseEntity<?> handleUserNotFoundException(UserNotFoundException exception) {

		return ResponseEntity.status(HttpStatus.NOT_FOUND).body(exception.getMessage());
	}
	
	@ExceptionHandler(InvalidUserIdException.class)
	public ResponseEntity<?> handleInvalidUserException(InvalidUserIdException exception) {

		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(exception.getMessage());
	}
}
