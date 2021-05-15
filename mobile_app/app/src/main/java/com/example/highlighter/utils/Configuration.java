package com.example.highlighter.utils;

/**
 * Class defining the configuration used in the code
 */
public class Configuration {

  // User interface parameters
  public static String UI_TOPIC_PREFIX = "#";
  public static String UI_TOPIC_SEPARATOR = " ";
  public static String UI_STRINGIFIED_DATE_FORMAT = "dd.MM.yyyy";
  public static int UI_MAX_QUOTE_CONTENT_LENGTH = 160;
  public static String UI_QUOTE_OVERFLOW_REPLACEMENT = "...";
  public static double UI_MAP_LATITUDE = 46.0891554;
  public static double UI_MAP_LONGITUDE = 24.5831354;
  public static float UI_MAP_ZOOM = 4;
  public static int UI_POPULAR_LABELS_COUNT = 3;
  public static int UI_DAILY_QUOTES_TO_DISPLAY = 7;

  // API details
  public static String API_KEY_REGEX = "[0-9a-fA-F]+";
  public static int API_MAX_DELAY_IN_SECONDS = 10;
  public static String API_SUCCESS_RESPONSE = "OK";
  public static String API_ROUTE_FORMAT = "quotes/%s/%s";

  // Intents request codes
  public static int INTENT_REQ_CODE_QUOTE_VIEW = 0;
  public static int INTENT_REQ_CODE_SETTINGS = 1;
  public static int INTENT_REQ_CODE_STATISTICS = 2;
  public static int INTENT_REQ_CODE_ABOUT = 3;

  // Intents extra parameters
  public static String INTENT_EXTRA_MODIFIED_QUOTE_NAME = "quote";
  public static String INTENT_EXTRA_IS_QUOTE_REMOVED = "is_quote_removed";
  public static String INTENT_EXTRA_SETTINGS_CHANGED = "settings_changed";

  // SharedPreferences configuration and names
  public static boolean SHARED_PREFERENCES_CLEAR_ON_START = false;
  public static String SHARED_PREFERENCES_NAME = "settings";
  public static String SHARED_PREFERENCES_MEMBER_API_KEY = "api_key";
  public static String SHARED_PREFERENCES_MEMBER_COLLECTION_URL = "collection_url";
  public static String SHARED_PREFERENCES_MEMBER_API_URL = "api_url";
  public static String SHARED_PREFERENCES_MEMBER_SYNC_INTERVAL = "sync_interval";
  public static String SHARED_PREFERENCES_MEMBER_DARK_THEME = "dark_theme";
  public static String SHARED_PREFERENCES_MEMBER_LAST_UPDATE = "last_update";

}
