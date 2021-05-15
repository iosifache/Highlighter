package com.example.highlighter.activities;

import android.app.Activity;
import android.os.Bundle;
import com.example.highlighter.R;

/**
 * Activity showing summary details about a quote
 */
public class QuotesListItemActivity extends Activity {

  /**
   * Starts the activity.
   *
   * @param savedInstanceState Saved data
   */
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_quotes_list_item);
  }

}