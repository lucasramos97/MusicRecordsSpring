package br.com.musicrecords.exeption;

public class LaunchDateException extends RuntimeException {

  private static final long serialVersionUID = -7106824810569945413L;

  public LaunchDateException(String message) {
    super(message);
  }

  public LaunchDateException(String message, Throwable throwable) {
    super(message, throwable);
  }

}
