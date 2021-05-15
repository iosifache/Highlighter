package com.example.highlighter.utils;

import android.os.Build.VERSION_CODES;
import androidx.annotation.RequiresApi;
import androidx.room.TypeConverter;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * Class implementing type converters used to transform objects into POJO
 */
public class Converters {

  /**
   * Converts an URL to a string.
   *
   * @param url URL to convert
   * @return String represented by the URL
   */
  @TypeConverter
  public String convertUrlToString(URL url) {
    if (url != null) {
      return url.toString();
    } else {
      return null;
    }
  }

  /**
   * Converts a string to an URL.
   *
   * @param url URL to convert
   * @return URL represented by the string
   */
  @TypeConverter
  public URL convertStringToUrl(String url) {
    try {
      URL castedUrl = new URL(url);
      castedUrl.toURI();

      return castedUrl;
    } catch (Exception e) {
      return null;
    }
  }

  /**
   * Converts a date to a long number (number of milliseconds since 1 January 1970).
   *
   * @param date Date to convert
   * @return Long number represented by the date
   */
  @TypeConverter
  public Long convertDateToLong(Date date) {
    if (date != null) {
      return date.getTime();
    } else {
      return null;
    }
  }

  /**
   * Converts a long number (number of milliseconds since 1 January 1970) to a date.
   *
   * @param date Long number to convert
   * @return Date represented by the long number
   */
  @TypeConverter
  public Date convertLongToDate(Long date) {
    if (date != null) {
      return new Date(date);
    } else {
      return null;
    }
  }

  /**
   * Converts a list of strings to a string by concatenating them with a separator.
   *
   * @param list List of strings to convert
   * @return String represented by the list of strings
   */
  @RequiresApi(api = VERSION_CODES.O)
  @TypeConverter
  public String convertListToString(List<String> list) {
    return String.join(Configuration.UI_TOPIC_SEPARATOR, list);
  }

  /**
   * Converts a string to a list of strings by splitting it by a separator.
   *
   * @param string String to convert
   * @return List of strings represented by the string
   */
  @TypeConverter
  public List<String> convertStringToList(String string) {
    String[] elements = string.split(Configuration.UI_TOPIC_SEPARATOR);

    return new ArrayList<>(Arrays.asList(elements));
  }

}
