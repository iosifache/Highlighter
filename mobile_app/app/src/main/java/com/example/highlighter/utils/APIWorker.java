package com.example.highlighter.utils;

import static android.content.Context.MODE_PRIVATE;

import android.content.SharedPreferences;
import android.os.Build.VERSION_CODES;
import androidx.annotation.RequiresApi;
import com.example.highlighter.MainActivity;
import com.example.highlighter.models.Quote;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.stream.Collectors;


public class APIWorker {

  private final MainActivity parentActivity;
  private String apiKey = null;
  private String encodedCollectionURL = null;
  private String proxyServerURL = null;

  public APIWorker(MainActivity parentActivity) {
    this.parentActivity = parentActivity;
  }

  private void refreshConfiguration() {
    SharedPreferences sharedPreferences = this.parentActivity
        .getSharedPreferences(Configuration.SHARED_PREFERENCES_NAME, MODE_PRIVATE);

    this.apiKey = sharedPreferences.getString(Configuration.SHARED_PREFERENCES_MEMBER_API_KEY, "");
    this.encodedCollectionURL = this
        .encodeHexadecimal(
            sharedPreferences
                .getString(Configuration.SHARED_PREFERENCES_MEMBER_COLLECTION_URL, ""));
    this.proxyServerURL = sharedPreferences
        .getString(Configuration.SHARED_PREFERENCES_MEMBER_API_URL, "");
  }

  private String encodeHexadecimal(String string) {
    final char[] stringCharArray = string.toCharArray();

    StringBuilder encodedString = new StringBuilder();
    for (char c : stringCharArray) {
      String hexString = Integer.toHexString(c);
      encodedString.append(hexString);
    }

    return encodedString.toString();

  }

  @RequiresApi(api = VERSION_CODES.N)
  public void uploadData(String jsonEncodedData) {
    this.refreshConfiguration();

    try {
      // Create the HTTP request
      URL url = new URL(proxyServerURL + String
          .format(Configuration.API_ROUTE_FORMAT, this.apiKey, this.encodedCollectionURL));
      HttpURLConnection connection = (HttpURLConnection) url.openConnection();

      // Set the request configuration
      connection.setConnectTimeout(10 * 1000);
      connection.setRequestMethod("POST");

      // Set the request data
      try (OutputStream os = connection.getOutputStream()) {
        byte[] input = jsonEncodedData.getBytes(StandardCharsets.UTF_8);
        os.write(input, 0, input.length);
      }

      // Make the request and read the response
      connection.connect();
      BufferedReader reader = new BufferedReader(
          new InputStreamReader(connection.getInputStream()));
      String responseString = reader.lines().collect(Collectors.joining());
    } catch (Exception ignored) {
    }

  }

  @RequiresApi(api = VERSION_CODES.N)
  public ArrayList<Quote> downloadData() {
    this.refreshConfiguration();

    try {
      // Create the HTTP request
      URL url = new URL(proxyServerURL + String
          .format(Configuration.API_ROUTE_FORMAT, this.apiKey, this.encodedCollectionURL));
      HttpURLConnection connection = (HttpURLConnection) url.openConnection();

      // Set the request configuration
      connection.setConnectTimeout(10 * 1000);
      connection.setRequestMethod("GET");

      // Make the request and read the response
      connection.connect();
      BufferedReader reader = new BufferedReader(
          new InputStreamReader(connection.getInputStream()));
      String responseString = reader.lines().collect(Collectors.joining());

      // Iterate through the returned quotes and populate the list
      ObjectMapper mapper = new ObjectMapper();

      return mapper.readValue(responseString,
          mapper.getTypeFactory().constructCollectionType(ArrayList.class, Quote.class));
    } catch (Exception ignored) {
    }

    return null;
  }

}
