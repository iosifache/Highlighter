package com.example.highlighter.utils;

public class Configuration {

  // User interface
  public static int MAX_QUOTE_CONTENT = 160;
  public static String QUOTE_OVERFLOW_REPLACEMENT = "...";
  public static String TOPIC_PREFIX = "#";
  public static String TOPIC_SEPARATOR = " ";
  public static String STRINGIFIED_DATE_FORMAT = "dd.MM.yyyy";
  public static double MAP_LATITUDE = 44.4180325;
  public static double MAP_LONGITUDE = 26.085095;
  public static float MAP_ZOOM = 15;
  public static String MAP_MARKER_TITLE = "Military Technical Academy 'Ferdinand I' Bucharest";
  public static String QUOTES_COUNT_MSG_FMT = "There are <b>%s</b> quotes stored in the app.";
  public static String POPULAR_LABELS = "Your most popular labels are:";
  public static String POPULAR_LABEL_FMT = "<br/>%d. <b>%s</b>, with %d occurrences and last use on %s";
  public static int POPULAR_LABELS_COUNT = 3;
  public static int DAILY_QUOTES_TO_DISPLAY = 7;
  public static String QUOTES_PER_DAY_CHART_LABEL = "Quotes per Day";
  public static String TODAY_CHART_LABEL = "today";
  public static String YESTERDAY_CHART_LABEL = "yesterday";
  public static String DAYS_AGO_LABEL_FMT = "%d days ago";

  // API
  public static String API_ROUTE_FORMAT = "quotes/%s/%s";

  // Intent extra parameters
  public static int INTENT_REQ_CODE_QUOTE_VIEW = 0;
  public static int INTENT_REQ_CODE_SETTINGS = 1;
  public static int INTENT_REQ_CODE_STATISTICS = 2;
  public static int INTENT_REQ_CODE_ABOUT = 3;
  public static String INTENT_EXTRA_MODIFIED_QUOTE_NAME = "quote";
  public static String INTENT_EXTRA_IS_QUOTE_REMOVED = "is_quote_removed";
  public static String INTENT_EXTRA_SETTINGS_CHANGED = "settings_changed";

  // SharedPreferences names and configuration
  public static boolean SHARED_PREFERENCES_CLEAR_ON_START = false;
  public static String SHARED_PREFERENCES_NAME = "settings";
  public static String SHARED_PREFERENCES_MEMBER_API_KEY = "api_key";
  public static String SHARED_PREFERENCES_MEMBER_COLLECTION_URL = "collection_url";
  public static String SHARED_PREFERENCES_MEMBER_API_URL = "api_url";
  public static String SHARED_PREFERENCES_MEMBER_SYNC_INTERVAL = "sync_interval";
  public static String SHARED_PREFERENCES_MEMBER_DARK_THEME = "dark_theme";
  public static String SHARED_PREFERENCES_MEMBER_LAST_UPDATE = "last_update";

}
