package br.com.musicrecords.config;

import javax.validation.UnexpectedTypeException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import br.com.musicrecords.model.MessageResponse;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.SignatureException;

@RestControllerAdvice
public class GlobalExceptionHandler {

  @ExceptionHandler(UsernameNotFoundException.class)
  public ResponseEntity<MessageResponse> handlerUsernameNotFoundException(
      UsernameNotFoundException e) {
    return new ResponseEntity<>(new MessageResponse(e.getMessage()), HttpStatus.BAD_REQUEST);
  }

  @ExceptionHandler(IllegalArgumentException.class)
  public ResponseEntity<MessageResponse> handlerIllegalArgumentException() {
    return new ResponseEntity<>(new MessageResponse("Unable to get JWT Token!"),
        HttpStatus.UNAUTHORIZED);
  }

  @ExceptionHandler(ExpiredJwtException.class)
  public ResponseEntity<MessageResponse> handlerExpiredJwtException() {
    return new ResponseEntity<>(new MessageResponse("Session Expired!"), HttpStatus.UNAUTHORIZED);
  }

  @ExceptionHandler(SignatureException.class)
  public ResponseEntity<MessageResponse> handlerSignatureException() {
    return new ResponseEntity<>(new MessageResponse("Invalid Token!"), HttpStatus.BAD_REQUEST);
  }

  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<MessageResponse> handlerMethodArgumentNotValidException(
      MethodArgumentNotValidException e) {
    String firstMessage = e.getBindingResult().getFieldErrors().get(0).getDefaultMessage();
    return new ResponseEntity<>(new MessageResponse(firstMessage), HttpStatus.BAD_REQUEST);
  }

  @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
  public ResponseEntity<MessageResponse> handlerHttpRequestMethodNotSupportedException() {
    return new ResponseEntity<>(new MessageResponse("URI not found!"), HttpStatus.BAD_REQUEST);
  }

  @ExceptionHandler(UnexpectedTypeException.class)
  public ResponseEntity<MessageResponse> handlerUnexpectedTypeException(
      UnexpectedTypeException exception) {
    try {
      String[] exceptionSplit = exception.getMessage().split("'");
      String type = exceptionSplit[3];
      String value = exceptionSplit[5];
      String errorMessage =
          String.format("It was not possible convert the value of %s to type %s", value, type);
      return new ResponseEntity<>(new MessageResponse(errorMessage), HttpStatus.BAD_REQUEST);
    } catch (Exception e) {
      return new ResponseEntity<>(new MessageResponse(e.getMessage()), HttpStatus.BAD_REQUEST);
    }
  }

}
