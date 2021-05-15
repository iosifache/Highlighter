package com.example.highlighter.data;

import static android.content.Context.MODE_PRIVATE;

import android.content.SharedPreferences;
import android.os.Build.VERSION_CODES;
import androidx.annotation.RequiresApi;
import com.example.highlighter.R;
import com.example.highlighter.activities.MainActivity;
import com.example.highlighter.utils.Configuration;
import com.example.highlighter.utils.TextUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.stream.Collectors;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;

/**
 * Class working with the API exposed by the proxy server
 */
public class APIWorker {

  private final MainActivity parentActivity;
  private String apiKey = null;
  private String encodedCollectionUrl = null;
  private String proxyServerUrl = null;

  /**
   * Initializes the APIWorker object.
   *
   * @param parentActivity Activity using the worker
   */
  public APIWorker(MainActivity parentActivity) {
    this.parentActivity = parentActivity;
  }

  private void refreshConfiguration() {
    SharedPreferences sharedPreferences = this.parentActivity
        .getSharedPreferences(Configuration.SHARED_PREFERENCES_NAME, MODE_PRIVATE);

    this.apiKey = sharedPreferences.getString(Configuration.SHARED_PREFERENCES_MEMBER_API_KEY, "");
    this.encodedCollectionUrl = TextUtils.encodeHexadecimal(
        sharedPreferences
            .getString(Configuration.SHARED_PREFERENCES_MEMBER_COLLECTION_URL, ""));
    this.proxyServerUrl = sharedPreferences
        .getString(Configuration.SHARED_PREFERENCES_MEMBER_API_URL, "");

    // Check if the proxy server URL has a backslash on its end
    if (!this.proxyServerUrl.endsWith("/")) {
      this.proxyServerUrl += "/";
    }
  }

  /**
   * Upload quotes to Notion.so.
   *
   * @param jsonEncodedData JSON representation of the quotes
   */
  @RequiresApi(api = VERSION_CODES.N)
  public void uploadData(String jsonEncodedData) {
    this.refreshConfiguration();

    try {
      // Create the HTTPS connection
      URL url = new URL(proxyServerUrl + String
          .format(Configuration.API_ROUTE_FORMAT, this.apiKey, this.encodedCollectionUrl));
      url.toURI();
      HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();

      // Setup the SSL
      SSLContext sslContext;
      sslContext = SSLContext.getInstance("TLS");
      sslContext.init(null, null, new java.security.SecureRandom());
      connection.setSSLSocketFactory(sslContext.getSocketFactory());

      // Set the request configuration
      connection.setConnectTimeout(1000 * Configuration.API_MAX_DELAY_IN_SECONDS);
      connection.setRequestMethod("POST");
      connection.setRequestProperty("Content-Type", "application/json; utf-8");
      connection.setDoOutput(true);

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
      if (!responseString.equals(Configuration.API_SUCCESS_RESPONSE)) {
        throw new Exception();
      }

      // Show a success snackbar
      String message = this.parentActivity.getResources()
          .getString(R.string.ntf_success_notion_upload);
      this.parentActivity.showSnackbar(message);
    } catch (Exception ignored) {
      String message = this.parentActivity.getResources()
          .getString(R.string.ntf_error_notion_upload);

      this.parentActivity.showSnackbar(message);
    }

  }

  /**
   * Download quotes from Notion.so.
   *
   * @return List of downloaded quotes
   */
  @RequiresApi(api = VERSION_CODES.N)
  public ArrayList<Quote> downloadData() {
    this.refreshConfiguration();

    try {
      // Create the HTTP request
      URL url = new URL(proxyServerUrl + String
          .format(Configuration.API_ROUTE_FORMAT, this.apiKey, this.encodedCollectionUrl));
      url.toURI();
      HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();

      // Setup the SSL
      SSLContext sslContext;
      sslContext = SSLContext.getInstance("TLS");
      sslContext.init(null, null, new java.security.SecureRandom());
      connection.setSSLSocketFactory(sslContext.getSocketFactory());

      // Set the request configuration
      connection.setConnectTimeout(1000 * Configuration.API_MAX_DELAY_IN_SECONDS);
      connection.setRequestMethod("GET");

      // Make the request and read the response
      connection.connect();
      BufferedReader reader = new BufferedReader(
          new InputStreamReader(connection.getInputStream()));
      String responseString = reader.lines().collect(Collectors.joining());

      // Iterate through the returned quotes and populate the list
      ObjectMapper mapper = new ObjectMapper();
      ArrayList<Quote> returnedList = mapper.readValue(responseString,
          mapper.getTypeFactory().constructCollectionType(ArrayList.class, Quote.class));

      // Show a success snackbar
      String message = this.parentActivity.getResources()
          .getString(R.string.ntf_success_notion_download);
      this.parentActivity.showSnackbar(message);

      return returnedList;
    } catch (Exception ignored) {
      String message = this.parentActivity.getResources()
          .getString(R.string.ntf_error_notion_download);

      this.parentActivity.showSnackbar(message);
    }

    return null;
  }

}
