package com.example.highlighter.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import com.example.highlighter.R;
import com.example.highlighter.custom_adapters.QuotesListAdaptor;
import com.example.highlighter.data.ApplicationDatabase;
import com.example.highlighter.data.Quote;
import com.example.highlighter.data.QuoteDAO;
import com.example.highlighter.tasks.NotionUploader;
import com.example.highlighter.utils.Configuration;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.android.material.snackbar.Snackbar;
import info.androidhive.fontawesome.FontTextView;
import java.util.ArrayList;
import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * Main activity of the application
 */
public class MainActivity extends AppCompatActivity {

  public static MainActivity instance = null;
  private final ArrayList<Quote> filteredQuotesList = new ArrayList<>();
  private QuoteDAO quotesDAO = null;
  private ArrayList<Quote> quotesList = new ArrayList<>();
  private QuotesListAdaptor adapter;
  private boolean isContentFilter = true;
  private int syncInterval;
  private long lastUpdate;
  private int oldTextColor = -1;

  /**
   * Starts the activity.
   *
   * @param savedInstanceState Saved data
   */
  @Override
  protected void onCreate(Bundle savedInstanceState) {

    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    MainActivity.instance = this;

    // Get the quotes from the database
    ApplicationDatabase database = ApplicationDatabase.getInstance(this);
    this.quotesDAO = database.quoteDAO();
    this.quotesList = (ArrayList<Quote>) this.quotesDAO.getAll();
    this.filteredQuotesList.addAll(this.quotesList);

    // Check if the settings needs to be cleared
    SharedPreferences settings = this
        .getSharedPreferences(Configuration.SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE);
    if (Configuration.SHARED_PREFERENCES_CLEAR_ON_START) {
      settings.edit().clear().apply();
    }

    // Check if the dark theme is activated
    boolean darkTheme = settings
        .getBoolean(Configuration.SHARED_PREFERENCES_MEMBER_DARK_THEME, false);
    if (darkTheme) {
      AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
    } else {
      AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
    }

    // Attach the custom adapter
    ListView quotesListView = findViewById(R.id.lw_quotes);
    this.adapter = new QuotesListAdaptor(this.filteredQuotesList, this);
    quotesListView.setAdapter(this.adapter);

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

    // Set a change handler for the search keywords
    EditText searchKeywordsEditText = this.findViewById(R.id.et_search_keywords);
    searchKeywordsEditText.addTextChangedListener(new TextWatcher() {
      @Override
      public void beforeTextChanged(CharSequence s, int start, int count, int after) {
      }

      @Override
      public void onTextChanged(CharSequence s, int start, int before, int count) {
      }

      @Override
      public void afterTextChanged(Editable s) {
        String keywords = s.toString();

        ArrayList<Quote> result;
        if (isContentFilter) {
          result = (ArrayList<Quote>) quotesDAO.searchByContent(keywords);

        } else {
          result = (ArrayList<Quote>) quotesDAO.searchByLabel(keywords);
        }
        setQuotesList(result, true);
      }
    });

  }

  /**
   * Destroys the activity.
   */
  @Override
  public void onDestroy() {
    super.onDestroy();

    ApplicationDatabase.destroyInstance();
  }

  private void updateLastUpdate() {
    SharedPreferences sharedPreferences = this.getSharedPreferences(
        Configuration.SHARED_PREFERENCES_NAME, MODE_PRIVATE);
    SharedPreferences.Editor editor = sharedPreferences.edit();

    editor.putLong(Configuration.SHARED_PREFERENCES_MEMBER_LAST_UPDATE, new Date().getTime());

    editor.apply();
  }

  private void updateConfiguration() {
    SharedPreferences sharedPreferences = this.getSharedPreferences(
        Configuration.SHARED_PREFERENCES_NAME, MODE_PRIVATE);

    String apiKey = sharedPreferences
        .getString(Configuration.SHARED_PREFERENCES_MEMBER_API_KEY, "");
    String collectionUrl = sharedPreferences
        .getString(Configuration.SHARED_PREFERENCES_MEMBER_COLLECTION_URL, "");
    this.syncInterval = sharedPreferences
        .getInt(Configuration.SHARED_PREFERENCES_MEMBER_SYNC_INTERVAL, -1);
    this.lastUpdate = sharedPreferences
        .getLong(Configuration.SHARED_PREFERENCES_MEMBER_LAST_UPDATE, -1);

    boolean isValidConfiguration = !apiKey.equals("") && !collectionUrl.equals("");
    this.colorSettingsButton(isValidConfiguration);
  }

  private void colorSettingsButton(boolean isValidConfiguration) {
    FontTextView settingsFontTextView = this.findViewById(R.id.btn_launch_settings);

    int setColor;
    if (!isValidConfiguration) {
      this.oldTextColor = settingsFontTextView.getCurrentTextColor();
      setColor = this.getResources().getColor(R.color.nasturcian_flower_red);
    } else {
      setColor = this.oldTextColor;
    }

    if (setColor != -1) {
      settingsFontTextView.setTextColor(setColor);
    }
  }

  /**
   * Sets the filtered (and, optionally, the full) quotes list.
   *
   * @param quotesList   List of quotes
   * @param onlyFiltered Boolean indicating if only the filtered list of quotes needs to be updated
   */
  public void setQuotesList(ArrayList<Quote> quotesList, boolean onlyFiltered) {
    // Update the local list of objects
    if (!onlyFiltered) {
      this.quotesList.clear();
      this.quotesList.addAll(quotesList);
    }
    this.filteredQuotesList.clear();
    this.filteredQuotesList.addAll(quotesList);

    // Update the database
    if (!onlyFiltered) {
      this.quotesDAO.deleteAll();
      this.quotesDAO.insertAll(this.quotesList);
    }
    this.adapter.notifyDataSetChanged();
  }

  /**
   * Stringify the list of quotes.
   *
   * @return JSON representation of the list of the quotes
   */
  public String jsonifyQuotesList() {
    ObjectMapper mapper = new ObjectMapper();
    try {
      return mapper.writerWithDefaultPrettyPrinter().writeValueAsString(this.quotesList);
    } catch (JsonProcessingException e) {
      e.printStackTrace();

      return null;
    }
  }

  /**
   * Shows an error snackbar.
   *
   * @param message Message to show in the snackbar
   */
  public void showSnackbar(String message) {
    String actionName = this.getResources().getString(R.string.action_hide);

    final Snackbar snackBar = Snackbar
        .make(findViewById(android.R.id.content), message, Snackbar.LENGTH_LONG);
    snackBar.setAction(actionName, v -> snackBar.dismiss());
    snackBar.show();
  }

  /**
   * Process the result of a launched activity.
   *
   * @param requestCode Request code
   * @param resultCode  Result code
   * @param data        Data from the returned intent
   */
  @Override
  protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
    super.onActivityResult(requestCode, resultCode, data);

    if (requestCode == Configuration.INTENT_REQ_CODE_QUOTE_VIEW && resultCode == RESULT_OK
        && data != null) {
      Quote quote = (Quote) data
          .getSerializableExtra(Configuration.INTENT_EXTRA_MODIFIED_QUOTE_NAME);
      boolean isQuoteRemoved = data
          .getBooleanExtra(Configuration.INTENT_EXTRA_IS_QUOTE_REMOVED, false);

      // Remove or update the received quote
      if (isQuoteRemoved) {
        this.quotesDAO.delete(quote);
      } else {
        this.quotesDAO.updateAll(quote);
      }
      this.setQuotesList((ArrayList<Quote>) this.quotesDAO.getAll(), false);

      this.adapter.notifyDataSetChanged();
    } else if (requestCode == Configuration.INTENT_REQ_CODE_SETTINGS && resultCode == RESULT_OK
        && data != null) {
      boolean updatedConfiguration = data
          .getBooleanExtra(Configuration.INTENT_EXTRA_SETTINGS_CHANGED, false);
      if (updatedConfiguration) {
        // Update the known settings
        this.updateConfiguration();
      }
    }

  }

  /**
   * Launches the settings activity.
   *
   * @param view View representing the clicked settings button
   */
  public void editSettings(View view) {
    Intent intent = new Intent(this.getApplicationContext(), SettingsActivity.class);
    this.startActivityForResult(intent, Configuration.INTENT_REQ_CODE_SETTINGS);
    this.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
  }

  /**
   * Switch the search filter (by content or by topics).
   *
   * @param view View representing the clicked search filter button
   */
  public void switchSearchFilter(View view) {
    this.isContentFilter = !this.isContentFilter;

    // Change the icon based on the filter
    FontTextView button = this.findViewById(R.id.btn_search_filter);
    if (this.isContentFilter) {
      button.setText(R.string.fa_font_solid);
    } else {
      button.setText(R.string.fa_tag_solid);
    }
  }

  /**
   * Launched the statistics activity.
   *
   * @param view View representing the clicked statistics button
   */
  public void launchStatistics(View view) {
    Intent intent = new Intent(this.getApplicationContext(), StatisticsActivity.class);
    this.startActivityForResult(intent, Configuration.INTENT_REQ_CODE_STATISTICS);
    this.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
  }

  /**
   * Launched the about activity.
   *
   * @param view View representing the clicked about button
   */
  public void launchAbout(View view) {
    Intent intent = new Intent(this.getApplicationContext(), AboutActivity.class);
    this.startActivityForResult(intent, Configuration.INTENT_REQ_CODE_ABOUT);
    this.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
  }

}