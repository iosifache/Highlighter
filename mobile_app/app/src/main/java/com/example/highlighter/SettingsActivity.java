package com.example.highlighter;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.SeekBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import com.example.highlighter.tasks.NotionDownloader;
import com.example.highlighter.tasks.NotionUploader;
import com.example.highlighter.utils.Configuration;

public class SettingsActivity extends AppCompatActivity {

  private boolean changedSettings = false;
  private int saveClicksCount = 0;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_settings);

    SharedPreferences sharedPreferences = getSharedPreferences(
        Configuration.SHARED_PREFERENCES_NAME, MODE_PRIVATE);

    String apiKey = sharedPreferences
        .getString(Configuration.SHARED_PREFERENCES_MEMBER_API_KEY, "");
    EditText apiKeyEditText = this.findViewById(R.id.et_api_key);
    apiKeyEditText.setText(apiKey);

    String collectionURL = sharedPreferences
        .getString(Configuration.SHARED_PREFERENCES_MEMBER_COLLECTION_URL, "");
    EditText collectionURLEditText = this.findViewById(R.id.et_collection_url);
    collectionURLEditText.setText(collectionURL);

    String proxyServerURL = sharedPreferences
        .getString(Configuration.SHARED_PREFERENCES_MEMBER_API_URL, "");
    EditText proxyServerURLEditText = this.findViewById(R.id.et_proxy_server_url);
    proxyServerURLEditText.setText(proxyServerURL);

    int syncInterval = sharedPreferences
        .getInt(Configuration.SHARED_PREFERENCES_MEMBER_SYNC_INTERVAL, 0);
    SeekBar syncIntervalSeekBar = this.findViewById(R.id.sb_sync_interval);
    syncIntervalSeekBar.setProgress(syncInterval);
  }

  public void saveSettings(View view) {
    SharedPreferences sharedPreferences = getSharedPreferences(
        Configuration.SHARED_PREFERENCES_NAME, MODE_PRIVATE);
    SharedPreferences.Editor editor = sharedPreferences.edit();

    EditText apiKeyEditText = this.findViewById(R.id.et_api_key);
    String apiKey = apiKeyEditText.getText().toString();
    String oldApiKey = sharedPreferences
        .getString(Configuration.SHARED_PREFERENCES_MEMBER_API_KEY, "");
    editor.putString(Configuration.SHARED_PREFERENCES_MEMBER_API_KEY, apiKey);

    EditText collectionURLEditText = this.findViewById(R.id.et_collection_url);
    String oldCollectionURL = sharedPreferences
        .getString(Configuration.SHARED_PREFERENCES_MEMBER_COLLECTION_URL, "");
    String collectionURL = collectionURLEditText.getText().toString();
    editor.putString(Configuration.SHARED_PREFERENCES_MEMBER_COLLECTION_URL, collectionURL);

    EditText proxyServerURLEditText = this.findViewById(R.id.et_proxy_server_url);
    String proxyServerURL = proxyServerURLEditText.getText().toString();
    editor.putString(Configuration.SHARED_PREFERENCES_MEMBER_API_URL, proxyServerURL);

    SeekBar syncIntervalSeekBar = this.findViewById(R.id.sb_sync_interval);
    int syncInterval = syncIntervalSeekBar.getProgress();
    editor.putInt(Configuration.SHARED_PREFERENCES_MEMBER_SYNC_INTERVAL, syncInterval);

    // Check if the pair of API key - collection URL is a new one
    if (!oldApiKey.equals(apiKey) && !oldCollectionURL.equals(collectionURL)) {
      NotionDownloader task = new NotionDownloader(MainActivity.instance);
      task.execute();
    }

    // Change the color scheme
    if (this.saveClicksCount == 10) {
      boolean oldDarkThemeEnabled = sharedPreferences
          .getBoolean(Configuration.SHARED_PREFERENCES_MEMBER_DARK_THEME, false);
      editor.putBoolean(Configuration.SHARED_PREFERENCES_MEMBER_DARK_THEME, !oldDarkThemeEnabled);

      if (!oldDarkThemeEnabled) {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
      } else {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
      }

      this.saveClicksCount = 0;
    } else {
      this.saveClicksCount++;
    }

    editor.apply();

    this.changedSettings = true;
  }

  public void goBack(View view) {
    Intent intent = new Intent();
    intent.putExtra(Configuration.INTENT_EXTRA_SETTINGS_CHANGED, this.changedSettings);

    setResult(RESULT_OK, intent);
    finish();
  }

  public void downloadFromNotion(View view) {
    NotionDownloader task = new NotionDownloader(MainActivity.instance);
    task.execute();
  }

  public void uploadToNotion(View view) {
    NotionUploader task = new NotionUploader(MainActivity.instance);
    task.execute();
  }

}