package com.example.highlighter.activities;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Build.VERSION_CODES;
import android.os.Bundle;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import com.example.highlighter.R;
import com.example.highlighter.data.ApplicationDatabase;
import com.example.highlighter.data.Quote;
import com.example.highlighter.utils.Configuration;
import com.google.android.material.snackbar.Snackbar;
import info.androidhive.fontawesome.FontTextView;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Calendar;
import java.util.Date;

/**
 * Activity for viewing and editing the details about a specific quote
 */
public class QuoteViewActivity extends AppCompatActivity {

  private boolean launchedFromOutside;
  private Quote quote = null;
  private int lastDialogOpenedID;

  /**
   * Starts the activity.
   *
   * @param savedInstanceState Saved data
   */
  @RequiresApi(api = VERSION_CODES.M)
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_quote_view);

    // Get the quote data from the creation intent to populate the fields
    Intent intent = getIntent();
    quote = (Quote) intent
        .getSerializableExtra(Configuration.INTENT_EXTRA_MODIFIED_QUOTE_NAME);
    String quoteContent = (String) intent.getCharSequenceExtra(Intent.EXTRA_PROCESS_TEXT);

    // Check if the activity was launched as a context menu action or directly from the app
    this.launchedFromOutside = (quote == null);

    // Process the quote or its details
    if (quote == null) {
      this.populateFieldsFromNewQuote(quoteContent);
    }
    this.populateFieldsFromQuote();

  }

  private void populateFieldsFromNewQuote(String content) {
    // Create a new quote
    this.quote = new Quote();

    // Populate the quote
    this.quote.setContent(content);
    this.quote.setCreationDateAsDate(new Date());
  }

  private void populateFieldsFromQuote() {
    // Set the source title
    TextView sourceNameTextView = this.findViewById(R.id.tv_quote_view_source_name);
    sourceNameTextView.setText(quote.getSourceTitle());

    // Set the source URL
    TextView sourceUrlTextView = this.findViewById(R.id.tv_quote_view_source_url);
    sourceUrlTextView.setText(quote.getSourceUrlAsString());

    // Set the content
    TextView contentTextView = this.findViewById(R.id.btn_quote_view_edit_content);
    contentTextView.setText(quote.getContent());

    // Set the creation date
    TextView creationDateTextView = this.findViewById(R.id.tv_quote_view_date);
    creationDateTextView.setText(quote.getCreationDate());

    // Set the labels
    TextView labelsTextView = this.findViewById(R.id.tv_quote_view_labels_view);
    labelsTextView.setText(quote.getStringifiedLabels());
  }

  /**
   * Deletes the quote.
   *
   * @param view View representing the clicked delete button
   */
  public void delete(View view) {
    Intent intent = new Intent();
    if (!this.launchedFromOutside) {
      intent.putExtra(Configuration.INTENT_EXTRA_IS_QUOTE_REMOVED, true);
      intent.putExtra(Configuration.INTENT_EXTRA_MODIFIED_QUOTE_NAME, quote);
    }
    setResult(RESULT_OK, intent);
    finish();
    overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
  }

  private void showSuccessfulEditSnackbar() {
    String message = this.getResources().getString(R.string.ntf_success_property_edit);
    String actionName = this.getResources().getString(R.string.action_hide);

    final Snackbar snackBar = Snackbar
        .make(findViewById(android.R.id.content), message, Snackbar.LENGTH_LONG);
    snackBar.setAction(actionName, v -> snackBar.dismiss());
    snackBar.show();
  }

  /**
   * Edits a string property of the quote.
   *
   * @param view View representing the clicked edit button of a string property
   */
  @SuppressLint("NonConstantResourceId")
  public void editStringProperty(View view) {
    FontTextView propertyEditFontTextView = (FontTextView) view;
    int id = propertyEditFontTextView.getId();

    final AlertDialog alertDialog = new AlertDialog.Builder(this).create();

    LayoutInflater inflater = this.getLayoutInflater();
    View dialogView = inflater.inflate(R.layout.dialog_string_property_edit, null);

    // Get the required details of the edited field
    CharSequence description = "";
    String value = "";
    boolean isMultiline = false;
    switch (id) {
      case R.id.btn_quote_view_edit_source_name:
        description = getText(R.string.msg_new_source_name);
        value = quote.getSourceTitle();
        break;
      case R.id.btn_quote_view_edit_source_url:
        description = getText(R.string.msg_new_source_url);
        value = quote.getSourceUrlAsString();
        break;
      case R.id.btn_quote_view_edit_quote_content:
        description = getText(R.string.msg_new_quote_content);
        value = quote.getContent();
        isMultiline = true;
        break;
      case R.id.btn_quote_view_edit_labels:
        description = getText(R.string.msg_new_topics);
        value = quote.getStringifiedLabels();
        break;
    }

    // Bind the description of the property
    final TextView descriptionTextView = dialogView
        .findViewById(R.id.tv_string_property_edit_description);
    descriptionTextView.setText(description);

    // Bind the value of the property
    final TextView valueTextView = dialogView
        .findViewById(R.id.et_string_property_edit_value);
    valueTextView.setText(value);
    if (isMultiline) {
      valueTextView.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_MULTI_LINE);
    }

    // Add an action for the cancel button
    Button cancelButton = dialogView.findViewById(R.id.btn_property_edit_cancel);
    cancelButton.setOnClickListener(view1 -> alertDialog.dismiss());

    // Add an action for the save button
    Button saveButton = dialogView.findViewById(R.id.btn_property_edit_ok);
    saveButton.setOnClickListener(view12 -> {
      String newPropertyValue = valueTextView.getText().toString();

      // Get the property and change it
      switch (lastDialogOpenedID) {
        case R.id.btn_quote_view_edit_source_name:
          quote.setSourceTitle(newPropertyValue);
          break;
        case R.id.btn_quote_view_edit_source_url:
          try {
            URL castedUrl = new URL(newPropertyValue);
            castedUrl.toURI();
            quote.setSourceUrl(castedUrl);
          } catch (MalformedURLException | URISyntaxException e) {
            String message = this.getResources().getString(R.string.ntf_invalid_source_url);
            String actionName = this.getResources().getString(R.string.action_hide);

            final Snackbar snackBar = Snackbar
                .make(findViewById(android.R.id.content), message, Snackbar.LENGTH_LONG);
            snackBar.setAction(actionName, v -> snackBar.dismiss());
            snackBar.show();
          }
          break;
        case R.id.btn_quote_view_edit_quote_content:
          quote.setContent(newPropertyValue);
          break;
        case R.id.btn_quote_view_edit_labels:
          quote.setStringifiedLabels(newPropertyValue);
          break;
        default:
          break;
      }

      // Refresh the data
      QuoteViewActivity.this.populateFieldsFromQuote();

      // Close the modal
      alertDialog.dismiss();

      this.showSuccessfulEditSnackbar();
    });

    this.lastDialogOpenedID = id;

    alertDialog.setView(dialogView);
    alertDialog.show();
  }

  /**
   * Edits a date property of the quote.
   *
   * @param view View representing the clicked edit button of a date property
   */
  public void editDateProperty(View view) {
    final Calendar creationDateCalendar = Calendar.getInstance();
    final DatePickerDialog datePickerDialog = new DatePickerDialog(this,
        (view1, year, monthOfYear, dayOfMonth) -> {
          Calendar innerCreationDateCalendar = Calendar.getInstance();
          innerCreationDateCalendar.set(year, monthOfYear, dayOfMonth);

          // Change the creation date
          quote.setCreationDateAsDate(innerCreationDateCalendar.getTime());

          // Refresh the shown data
          QuoteViewActivity.this.populateFieldsFromQuote();

          this.showSuccessfulEditSnackbar();
        }, creationDateCalendar.get(Calendar.YEAR), creationDateCalendar.get(Calendar.MONTH),
        creationDateCalendar.get(Calendar.DAY_OF_MONTH));

    datePickerDialog.show();
  }

  public void goBack(View view) {
    Intent intent = new Intent();
    if (!this.launchedFromOutside) {
      intent.putExtra(Configuration.INTENT_EXTRA_MODIFIED_QUOTE_NAME, quote);
    } else {
      ApplicationDatabase.getInstance(this).quoteDAO().insert(quote);
    }
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