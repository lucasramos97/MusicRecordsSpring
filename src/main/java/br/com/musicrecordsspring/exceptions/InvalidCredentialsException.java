package br.com.musicrecordsspring.exceptions;

public class InvalidCredentialsException extends RuntimeException {

  private static final long serialVersionUID = 2047268282096799269L;

  public InvalidCredentialsException(String message) {
    super(message);
  }

}
