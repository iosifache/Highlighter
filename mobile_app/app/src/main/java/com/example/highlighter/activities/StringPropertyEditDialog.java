package com.example.highlighter.activities;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import com.example.highlighter.R;

/**
 * Dialog for editing a string property of a quote
 */
public class StringPropertyEditDialog extends AppCompatActivity {

  /**
   * Starts the activity.
   *
   * @param savedInstanceState Saved data
   */
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.dialog_string_property_edit);
  }

}