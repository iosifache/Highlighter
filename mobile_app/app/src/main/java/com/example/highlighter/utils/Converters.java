package com.example.highlighter.utils;

import android.os.Build.VERSION_CODES;
import androidx.annotation.RequiresApi;
import androidx.room.TypeConverter;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

public class Converters {

  @TypeConverter
  public String convertURLToString(URL url) {
    if (url != null) {
      return url.toString();
    } else {
      return null;
    }
  }

  @TypeConverter
  public URL convertStringToURL(String url) {
    try {
      return new URL(url);
    } catch (Exception e) {
      return null;
    }
  }

  @TypeConverter
  public Long convertDateToLong(Date date) {
    if (date != null) {
      return date.getTime();
    } else {
      return null;
    }
  }

  @TypeConverter
  public Date convertLongToDate(Long date) {
    if (date != null) {
      return new Date(date);
    } else {
      return null;
    }
  }

  @RequiresApi(api = VERSION_CODES.O)
  @TypeConverter
  public String convertListToString(List<String> list) {
    return String.join(Configuration.TOPIC_SEPARATOR, list);
  }

  @TypeConverter
  public List<String> convertStringToList(String string) {
    String[] elements = string.split(Configuration.TOPIC_SEPARATOR);

    return new ArrayList<>(Arrays.asList(elements));
  }
}
