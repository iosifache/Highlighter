package com.example.highlighter;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import com.example.highlighter.controllers.CustomQuotesListAdaptor;
import com.example.highlighter.models.Quote;
import com.example.highlighter.tasks.NotionUploader;
import com.example.highlighter.utils.Configuration;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import info.androidhive.fontawesome.FontTextView;
import java.util.ArrayList;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {

  public static MainActivity instance = null;
  private final ArrayList<Quote> quotesList = new ArrayList<>();
  private CustomQuotesListAdaptor adapter;
  private int syncInterval;
  private long lastUpdate;
  private int oldTextColor;

  @Override
  protected void onCreate(Bundle savedInstanceState) {

    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    MainActivity.instance = this;

    // Clear the preferences
    if (Configuration.SHARED_PREFERENCES_CLEAR_ON_START) {
      SharedPreferences settings = this.getApplicationContext()
          .getSharedPreferences(Configuration.SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE);
      settings.edit().clear().apply();
    }

    ListView quotesList = (ListView) findViewById(R.id.main_lw_quotes);

    // Attach the custom adapter
    Context context = getApplicationContext();
    context.setTheme(R.style.Theme_Highlighter);
    this.adapter = new CustomQuotesListAdaptor(this.quotesList, this);
    quotesList.setAdapter(this.adapter);

    // Check if an update needs to be executed
    this.updateConfiguration();
    if (this.lastUpdate == -1) {
      this.updateLastUpdate();
    } else {
      // Get the dates difference
      long now = new Date().getTime();
      long diffInMilliseconds = Math.abs(now - this.lastUpdate);
      long diffInHours = TimeUnit.HOURS.convert(diffInMilliseconds, TimeUnit.MILLISECONDS);
      long diffInDays = TimeUnit.DAYS.convert(diffInMilliseconds, TimeUnit.MILLISECONDS);

      // Check if the date difference reaches the set sync interval
      if ((diffInHours >= 1 && this.syncInterval == 0) || (diffInDays >= 1
          && this.syncInterval == 1) || (diffInDays >= 30)) {
        NotionUploader task = new NotionUploader(MainActivity.instance);
        task.execute();

        this.updateLastUpdate();
      }
    }

  }

  private void updateLastUpdate() {
    SharedPreferences sharedPreferences = getSharedPreferences(
        Configuration.SHARED_PREFERENCES_NAME, MODE_PRIVATE);
    SharedPreferences.Editor editor = sharedPreferences.edit();

    editor.putLong("last_update", new Date().getTime());

    editor.apply();
  }

  private void updateConfiguration() {
    SharedPreferences sharedPreferences = getSharedPreferences(
        Configuration.SHARED_PREFERENCES_NAME, MODE_PRIVATE);

    String apiKey = sharedPreferences
        .getString(Configuration.SHARED_PREFERENCES_MEMBER_API_KEY, "");
    String collectionURL = sharedPreferences
        .getString(Configuration.SHARED_PREFERENCES_MEMBER_COLLECTION_URL, "");
    this.syncInterval = sharedPreferences
        .getInt(Configuration.SHARED_PREFERENCES_MEMBER_SYNC_INTERVAL, -1);
    this.lastUpdate = sharedPreferences
        .getLong(Configuration.SHARED_PREFERENCES_MEMBER_LAST_UPDATE, -1);

    boolean isValidConfiguration = !apiKey.equals("") && !collectionURL.equals("");
    this.colorSettingsButton(isValidConfiguration);

  }

  private void colorSettingsButton(boolean isValidConfiguration) {
    FontTextView settingsButton = this.findViewById(R.id.btn_launch_settings);

    int setColor;
    if (!isValidConfiguration) {
      this.oldTextColor = settingsButton.getCurrentTextColor();
      setColor = this.getResources().getColor(R.color.nasturcian_flower_red);
    } else {
      setColor = this.oldTextColor;
    }

    settingsButton.setTextColor(setColor);
  }

  public void setQuotesList(ArrayList<Quote> quotesList) {
    this.quotesList.clear();
    this.quotesList.addAll(quotesList);

    this.adapter.notifyDataSetChanged();
  }

  public String jsonifyQuotesList() {
    ObjectMapper mapper = new ObjectMapper();
    try {
      return mapper.writerWithDefaultPrettyPrinter().writeValueAsString(this.quotesList);
    } catch (JsonProcessingException e) {
      e.printStackTrace();
    }

    return null;
  }

  @Override
  protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
    super.onActivityResult(requestCode, resultCode, data);

    if (requestCode == Configuration.INTENT_REQ_CODE_QUOTE_VIEW && resultCode == RESULT_OK
        && data != null) {
      Quote quote = (Quote) data
          .getSerializableExtra(Configuration.INTENT_EXTRA_MODIFIED_QUOTE_NAME);

      this.quotesList.set(quote.getId(), quote);
      this.adapter.notifyDataSetChanged();
    } else if (requestCode == Configuration.INTENT_REQ_CODE_SETTINGS && resultCode == RESULT_OK
        && data != null) {
      boolean updatedConfiguration = data
          .getBooleanExtra(Configuration.INTENT_EXTRA_SETTINGS_CHANGED, false);
      if (updatedConfiguration) {
        this.updateConfiguration();
      }
    }

  }

  public void editSettings(View view) {
    Intent intent = new Intent(this.getApplicationContext(), SettingsActivity.class);
    this.startActivityForResult(intent, Configuration.INTENT_REQ_CODE_SETTINGS);
  }

}