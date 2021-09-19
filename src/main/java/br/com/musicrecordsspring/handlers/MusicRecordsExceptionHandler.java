package br.com.musicrecordsspring.handlers;

import java.util.HashMap;
import java.util.Map;
import javax.persistence.EntityNotFoundException;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import com.fasterxml.jackson.databind.JsonMappingException.Reference;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import br.com.musicrecordsspring.exceptions.FutureDateException;

@ControllerAdvice
public class MusicRecordsExceptionHandler {

  @ExceptionHandler(EntityNotFoundException.class)
  public ResponseEntity<Map<String, String>> entityNotFoundExceptionHandler(
      EntityNotFoundException e) {

    Map<String, String> response = new HashMap<>();
    response.put("message", e.getMessage());

    return new ResponseEntity<Map<String, String>>(response, HttpStatus.NOT_FOUND);
  }

  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<Map<String, String>> methodArgumentNotValidExceptionHandler(
      MethodArgumentNotValidException e) {

    String message = e.getMessage();

    if (CollectionUtils.isNotEmpty(e.getAllErrors())) {

      ObjectError objectError = e.getAllErrors().get(0);
      message = objectError.getDefaultMessage();
    }

    Map<String, String> response = new HashMap<>();
    response.put("message", message);

    return new ResponseEntity<Map<String, String>>(response, HttpStatus.BAD_REQUEST);
  }

  @ExceptionHandler(InvalidFormatException.class)
  public ResponseEntity<Map<String, String>> invalidFormatExceptionHandler(
      InvalidFormatException e) {

    String message = e.getMessage();

    if (CollectionUtils.isNotEmpty(e.getPath())) {

      Reference reference = e.getPath().get(0);

      if (StringUtils.equals(reference.getFieldName(), "release_date")) {
        message = "Wrong Release Date format, try yyyy-MM-dd!";
      }

      if (StringUtils.equals(reference.getFieldName(), "duration")) {
        message = "Wrong Duration format, try HH:mm:ss!";
      }
    }

    Map<String, String> response = new HashMap<>();
    response.put("message", message);

    return new ResponseEntity<Map<String, String>>(response, HttpStatus.BAD_REQUEST);
  }

  @ExceptionHandler(FutureDateException.class)
  public ResponseEntity<Map<String, String>> futureDateExceptionHandler(FutureDateException e) {

    Map<String, String> response = new HashMap<>();
    response.put("message", e.getMessage());

    return new ResponseEntity<Map<String, String>>(response, HttpStatus.BAD_REQUEST);
  }

}
