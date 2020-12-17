package br.com.musicrecords.config;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import br.com.musicrecords.model.MessageResponse;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.SignatureException;

@RestControllerAdvice
public class GlobalExceptionHandler {

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

}
