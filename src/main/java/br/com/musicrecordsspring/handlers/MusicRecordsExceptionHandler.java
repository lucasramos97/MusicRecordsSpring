package br.com.musicrecordsspring.handlers;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;
import javax.persistence.EntityNotFoundException;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import com.fasterxml.jackson.databind.JsonMappingException.Reference;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import br.com.musicrecordsspring.exceptions.FutureDateException;
import br.com.musicrecordsspring.exceptions.InvalidCredentialsException;
import br.com.musicrecordsspring.utils.Messages;

@ControllerAdvice
public class MusicRecordsExceptionHandler {

  private static final String MESSAGE_FIELD = "message";

  @ExceptionHandler(EntityNotFoundException.class)
  public ResponseEntity<Map<String, String>> entityNotFoundExceptionHandler(
      EntityNotFoundException e) {

    Map<String, String> response = new HashMap<>();
    response.put(MESSAGE_FIELD, e.getMessage());

    return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
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
    response.put(MESSAGE_FIELD, message);

    return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
  }

  @ExceptionHandler(InvalidFormatException.class)
  public ResponseEntity<Map<String, String>> invalidFormatExceptionHandler(
      InvalidFormatException e) {

    String message = e.getMessage();

    if (CollectionUtils.isNotEmpty(e.getPath())) {

      Reference reference = e.getPath().get(0);

      if (StringUtils.equals(reference.getFieldName(), "release_date")) {
        message = getReleaseDateMessage(e.getValue().toString());
      }

      if (StringUtils.equals(reference.getFieldName(), "duration")) {
        message = getDurationMessage(e.getValue().toString());
      }
    }

    Map<String, String> response = new HashMap<>();
    response.put(MESSAGE_FIELD, message);

    return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
  }

  @ExceptionHandler(FutureDateException.class)
  public ResponseEntity<Map<String, String>> futureDateExceptionHandler(FutureDateException e) {

    Map<String, String> response = new HashMap<>();
    response.put(MESSAGE_FIELD, e.getMessage());

    return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
  }

  @ExceptionHandler(DataIntegrityViolationException.class)
  public ResponseEntity<Map<String, String>> dataIntegrityViolationExceptionHandler(
      DataIntegrityViolationException e) {

    Map<String, String> response = new HashMap<>();
    response.put(MESSAGE_FIELD, e.getMessage());

    return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
  }

  @ExceptionHandler(InvalidCredentialsException.class)
  public ResponseEntity<Map<String, String>> invalidCredentialsExceptionHandler(
      InvalidCredentialsException e) {

    Map<String, String> response = new HashMap<>();
    response.put(MESSAGE_FIELD, e.getMessage());

    return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
  }

  private String getReleaseDateMessage(String value) {

    if (Pattern.matches("\\d{4}-\\d{2}-\\d{2}", value)) {
      return Messages.getInvalidDate(value);
    }

    return Messages.WRONG_RELEASE_DATE_FORMAT;
  }

  private String getDurationMessage(String value) {

    if (Pattern.matches("\\d{2}:\\d{2}:\\d{2}", value)) {
      return Messages.getInvalidTime(value);
    }

    return Messages.WRONG_DURATION_FORMAT;
  }
}
