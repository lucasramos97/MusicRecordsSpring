package br.com.musicrecords.utils;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import br.com.musicrecords.exeption.LaunchDateException;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ValidatorUtils {

  public static void validLaunchDate(String launchDate) {
    try {
      if (launchDate.length() != 8) {
        String errorMessage =
            String.format("Invalid date value '%s' follow 'ddMMyyyy' pattern!", launchDate);
        throw new LaunchDateException(errorMessage);
      }
      LocalDate.parse(launchDate, DateTimeFormatter.ofPattern("ddMMyyyy"));
    } catch (DateTimeParseException e) {
      String invalidDate = e.getMessage().split("'")[1];
      String typeDate = e.getMessage().split("Invalid value for ")[1].split(" ")[0];
      String validValues = e.getMessage().split("valid values ")[1].split("[)]")[0];
      String invalidValue = e.getMessage().split(": ")[2];
      String errorMessage = String.format(
          "Date '%s' is invalid, %s does not exist, valid values are (%s) informed: %s!",
          invalidDate, ValidatorUtils.formattedTypeDate(typeDate), validValues, invalidValue);
      throw new LaunchDateException(errorMessage, e);
    }
  }

  private static String formattedTypeDate(String typeDate) {
    String[] dayMonthOrYear = typeDate.split("Of");
    String dayOrMonth = dayMonthOrYear[0];
    String monthOrYear = dayMonthOrYear[1];
    return String.format("%s of %s", dayOrMonth.toLowerCase(), monthOrYear.toLowerCase());
  }

}
