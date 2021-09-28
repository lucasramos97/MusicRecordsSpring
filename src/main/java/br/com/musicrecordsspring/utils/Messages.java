package br.com.musicrecordsspring.utils;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class Messages {

  // User Messages
  public static final String USERNAME_IS_REQUIRED = "Username is required!";
  public static final String EMAIL_INVALID = "E-mail invalid!";
  public static final String EMAIL_IS_REQUIRED = "E-mail is required!";
  public static final String PASSWORD_IS_REQUIRED = "Password is required!";

  // Music Messages
  public static final String ID_IS_REQUIRED = "Id is required!";
  public static final String TITLE_IS_REQUIRED = "Title is required!";
  public static final String ARTIST_IS_REQUIRED = "Artist is required!";
  public static final String RELEASE_DATE_IS_REQUIRED = "Release Date is required!";
  public static final String DURATION_IS_REQUIRED = "Duration is required!";
  public static final String MUSIC_NOT_FOUND = "Music not found!";
  public static final String RELEASE_DATE_CANNOT_BE_FUTURE = "Release Date cannot be future!";
  public static final String WRONG_RELEASE_DATE_FORMAT =
      "Wrong Release Date format, try yyyy-MM-dd!";
  public static final String WRONG_DURATION_FORMAT = "Wrong Duration format, try HH:mm:ss!";

  // Authorization Messages
  public static final String HEADER_AUTHORIZATION_NOT_PRESENT = "Header Authorization not present!";
  public static final String NO_BEARER_AUTHORIZATION_SCHEME =
      "No Bearer HTTP authentication scheme!";
  public static final String NO_TOKEN_PROVIDED = "No token provided!";
  public static final String INVALID_TOKEN = "Invalid token!";

  public static String getEmailAlreadyRegistered(String email) {
    return String.format("The %s e-mail has already been registered!", email);
  }

  public static String getUserNotFoundByEmail(String email) {
    return String.format("User not found by e-mail: %s!", email);
  }

  public static String getPasswordDoesNotMatchWithEmail(String email) {
    return String.format("Password does not match with email: %s!", email);
  }
}
