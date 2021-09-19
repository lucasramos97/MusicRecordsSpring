package br.com.musicrecordsspring.exceptions;

public class FutureDateException extends RuntimeException {

  private static final long serialVersionUID = 2559320238100384191L;

  public FutureDateException(String message) {
    super(message);
  }

}
