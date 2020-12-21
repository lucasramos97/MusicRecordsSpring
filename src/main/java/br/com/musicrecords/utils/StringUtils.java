package br.com.musicrecords.utils;

public class StringUtils {

  public static String leaveOnlyNumbers(String value) {
    return value.replaceAll("[^0-9]", "");
  }

}
