package com.example.highlighter.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.SeekBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import com.example.highlighter.R;
import com.example.highlighter.tasks.NotionDownloader;
import com.example.highlighter.tasks.NotionUploader;
import com.example.highlighter.utils.Configuration;
import com.google.android.material.snackbar.Snackbar;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;

/**
 * Activity for displaying and manipulating the settings of the application
 */
public class SettingsActivity extends AppCompatActivity {

  private boolean changedSettings = false;
  private int saveClicksCount = 0;

  /**
   * Starts the activity.
   *
   * @param savedInstanceState Saved data
   */
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_settings);

    // Get the shared preferences to bind them
    SharedPreferences sharedPreferences = this.getSharedPreferences(
        Configuration.SHARED_PREFERENCES_NAME, MODE_PRIVATE);

    // Get and bind the API key
    String apiKey = sharedPreferences
        .getString(Configuration.SHARED_PREFERENCES_MEMBER_API_KEY, "");
    EditText apiKeyEditText = this.findViewById(R.id.et_api_key);
    apiKeyEditText.setText(apiKey);

    // Get and bind the collection URL
    String collectionUrl = sharedPreferences
        .getString(Configuration.SHARED_PREFERENCES_MEMBER_COLLECTION_URL, "");
    EditText collectionUrlEditText = this.findViewById(R.id.et_collection_url);
    collectionUrlEditText.setText(collectionUrl);

    // Get and bind the proxy server URL
    String proxyServerUrl = sharedPreferences
        .getString(Configuration.SHARED_PREFERENCES_MEMBER_API_URL, "");
    EditText proxyServerUrlEditText = this.findViewById(R.id.et_proxy_server_url);
    proxyServerUrlEditText.setText(proxyServerUrl);

    // Get and bind the sync interval
    int syncInterval = sharedPreferences
        .getInt(Configuration.SHARED_PREFERENCES_MEMBER_SYNC_INTERVAL, 0);
    SeekBar syncIntervalSeekBar = this.findViewById(R.id.sb_sync_interval);
    syncIntervalSeekBar.setProgress(syncInterval);
  }

  /**
   * Saves the settings.
   *
   * @param view View representing the clicked save button
   */
  public void saveSettings(View view) {
    // Get the shared preferences to edit its fields
    SharedPreferences sharedPreferences = this.getSharedPreferences(
        Configuration.SHARED_PREFERENCES_NAME, MODE_PRIVATE);
    SharedPreferences.Editor editor = sharedPreferences.edit();

    if (this.saveClicksCount == 10) {
      boolean oldDarkThemeEnabled = sharedPreferences
          .getBoolean(Configuration.SHARED_PREFERENCES_MEMBER_DARK_THEME, false);
      editor.putBoolean(Configuration.SHARED_PREFERENCES_MEMBER_DARK_THEME, !oldDarkThemeEnabled);
      editor.apply();

      if (!oldDarkThemeEnabled) {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
      } else {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
      }

      this.saveClicksCount = 0;
    } else {
      this.saveClicksCount++;
    }

    // Get and save the API key
    EditText apiKeyEditText = this.findViewById(R.id.et_api_key);
    String apiKey = apiKeyEditText.getText().toString();
    if (!apiKey.matches(Configuration.API_KEY_REGEX)) {
      String message = this.getResources().getString(R.string.ntf_invalid_api_key);
      String actionName = this.getResources().getString(R.string.action_hide);

      final Snackbar snackBar = Snackbar
          .make(findViewById(android.R.id.content), message, Snackbar.LENGTH_LONG);
      snackBar.setAction(actionName, v -> snackBar.dismiss());
      snackBar.show();

      return;
    }
    editor.putString(Configuration.SHARED_PREFERENCES_MEMBER_API_KEY, apiKey);

    // Get, check and save the collection URL
    EditText collectionURLEditText = this.findViewById(R.id.et_collection_url);
    String oldCollectionUrl = sharedPreferences
        .getString(Configuration.SHARED_PREFERENCES_MEMBER_COLLECTION_URL, "");
    String collectionUrl = collectionURLEditText.getText().toString();
    try {
      URL castedCollectionURL = new URL(collectionUrl);
      castedCollectionURL.toURI();
    } catch (MalformedURLException | URISyntaxException e) {
      String message = this.getResources().getString(R.string.ntf_invalid_collection_url);
      String actionName = this.getResources().getString(R.string.action_hide);

      final Snackbar snackBar = Snackbar
          .make(findViewById(android.R.id.content), message, Snackbar.LENGTH_LONG);
      snackBar.setAction(actionName, v -> snackBar.dismiss());
      snackBar.show();

      return;
    }
    editor.putString(Configuration.SHARED_PREFERENCES_MEMBER_COLLECTION_URL, collectionUrl);

    // Get and save the proxy server URL
    EditText proxyServerURLEditText = this.findViewById(R.id.et_proxy_server_url);
    String proxyServerUrl = proxyServerURLEditText.getText().toString();
    try {
      URL castedProxyServerUrl = new URL(proxyServerUrl);
      castedProxyServerUrl.toURI();
    } catch (MalformedURLException | URISyntaxException e) {
      String message = this.getResources().getString(R.string.ntf_invalid_proxy_server_url);
      String actionName = this.getResources().getString(R.string.action_hide);

      final Snackbar snackBar = Snackbar
          .make(findViewById(android.R.id.content), message, Snackbar.LENGTH_LONG);
      snackBar.setAction(actionName, v -> snackBar.dismiss());
      snackBar.show();

      return;
    }
    editor.putString(Configuration.SHARED_PREFERENCES_MEMBER_API_URL, proxyServerUrl);

    // Get and save the sync interval
    SeekBar syncIntervalSeekBar = this.findViewById(R.id.sb_sync_interval);
    int syncInterval = syncIntervalSeekBar.getProgress();
    editor.putInt(Configuration.SHARED_PREFERENCES_MEMBER_SYNC_INTERVAL, syncInterval);
    editor.apply();

    // Show a success snackbar
    String message = this.getResources().getString(R.string.ntf_success_settings);
    String actionName = this.getResources().getString(R.string.action_hide);
    final Snackbar snackBar = Snackbar
        .make(findViewById(android.R.id.content), message, Snackbar.LENGTH_LONG);
    snackBar.setAction(actionName, v -> snackBar.dismiss());
    snackBar.show();

    this.changedSettings = true;
  }

  /**
   * Downloads the quotes from Notion.so.
   *
   * @param view View representing the clicked download button
   */
  public void downloadFromNotion(View view) {
    NotionDownloader task = new NotionDownloader(MainActivity.instance);
    task.execute();
  }

  /**
   * Uploads the quotes to Notion.so.
   *
   * @param view View representing the clicked upload button
   */
  public void uploadToNotion(View view) {
    NotionUploader task = new NotionUploader(MainActivity.instance);
    task.execute();
  }

  /**
   * Goes back to the calling activity.
   *
   * @param view View representing the clicked back button
   */
  public void goBack(View view) {
    Intent intent = new Intent();
    intent.putExtra(Configuration.INTENT_EXTRA_SETTINGS_CHANGED, this.changedSettings);
    setResult(RESULT_OK, intent);
    finish();
    overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
  }

  /**
   * Goes back to the calling activity on back button press.
   */
  @Override
  public void onBackPressed() {
    super.onBackPressed();

    this.goBack(null);
  }

}