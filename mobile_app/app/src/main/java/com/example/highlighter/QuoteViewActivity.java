package com.example.highlighter;

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
import com.example.highlighter.models.Quote;
import com.example.highlighter.utils.Configuration;
import info.androidhive.fontawesome.FontTextView;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class QuoteViewActivity extends AppCompatActivity {

  private boolean launchedFromOutside;
  private Quote quote = null;
  private int lastDialogOpenedID;

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

    // Save the method of activity launch
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
    this.quote.setCreationDateAsDatetype(new Date());
  }

  private void populateFieldsFromQuote() {
    // Set the source title
    TextView quoteSourceName = this.findViewById(R.id.tv_quote_view_source_name);
    quoteSourceName.setText(quote.getSourceTitle());

    // Set the source URL
    TextView quoteSourceURL = this.findViewById(R.id.tv_quote_view_source_url);
    quoteSourceURL.setText(quote.getSourceURLAsString());

    // Set the content
    TextView quoteContent = this.findViewById(R.id.btn_quote_view_edit_content);
    quoteContent.setText(quote.getContent());

    // Set the creation date
    TextView quoteCreationDate = this.findViewById(R.id.tv_quote_view_date);
    @SuppressLint("SimpleDateFormat") DateFormat dateFormat = new SimpleDateFormat(
        Configuration.STRINGIFIED_DATE_FORMAT);
    quoteCreationDate.setText(dateFormat.format(quote.getCreationDate()));

    // Set the labels
    TextView labels = this.findViewById(R.id.tv_quote_view_labels_view);
    labels.setText(quote.getStringifiedLabels());
  }

  public void goBack(View view) {
    Intent intent = new Intent();
    if (!this.launchedFromOutside) {
      intent.putExtra(Configuration.INTENT_EXTRA_MODIFIED_QUOTE_NAME, quote);
    }
    else{
      // TODO: Save the quote in the database
    }

    setResult(RESULT_OK, intent);
    finish();
  }

  @SuppressLint("NonConstantResourceId")
  public void editStringProperty(View view) {

    FontTextView icon = (FontTextView) view;
    int id = icon.getId();

    final AlertDialog dialogBuilder = new AlertDialog.Builder(this).create();

    LayoutInflater inflater = this.getLayoutInflater();
    View dialogView = inflater.inflate(R.layout.dialog_string_property_edit, null);

    // Get the required details
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
        value = quote.getSourceURLAsString();
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

    // Get references to the dialog elements
    final TextView propertyDescription = dialogView
        .findViewById(R.id.tv_string_property_edit_description);
    final TextView propertyValue = dialogView
        .findViewById(R.id.et_string_property_edit_value);
    Button cancelButton = dialogView.findViewById(R.id.btn_string_property_edit_cancel);
    Button saveButton = dialogView.findViewById(R.id.btn_string_property_edit_ok);

    // Populate them based on the selected edited field
    propertyDescription.setText(description);
    propertyValue.setText(value);
    if (isMultiline) {
      propertyValue.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_MULTI_LINE);
    }

    // Add a listener for the cancel button
    cancelButton.setOnClickListener(view1 -> dialogBuilder.dismiss());

    // Add a listener for the save button
    saveButton.setOnClickListener(view12 -> {
      String newPropertyValue = propertyValue.getText().toString();

      // Check what property needs to be changed and change it
      switch (lastDialogOpenedID) {
        case R.id.btn_quote_view_edit_source_name:
          quote.setSourceTitle(newPropertyValue);
          break;
        case R.id.btn_quote_view_edit_source_url:
          try {
            quote.setSourceURL(new URL(newPropertyValue));
          } catch (MalformedURLException e) {
            e.printStackTrace();
          }
          break;
        case R.id.btn_quote_view_edit_quote_content:
          quote.setContent(newPropertyValue);
          break;
        case R.id.btn_quote_view_edit_labels:
          quote.setStringifiedLabels(newPropertyValue);
          break;
      }

      // Refresh the shown data
      QuoteViewActivity.this.populateFieldsFromQuote();

      dialogBuilder.dismiss();
    });

    this.lastDialogOpenedID = id;

    dialogBuilder.setView(dialogView);
    dialogBuilder.show();
  }

  public void editDateProperty(View view) {
    final Calendar newCalendar = Calendar.getInstance();
    final DatePickerDialog StartTime = new DatePickerDialog(this,
        (view1, year, monthOfYear, dayOfMonth) -> {
          Calendar newDate = Calendar.getInstance();
          newDate.set(year, monthOfYear, dayOfMonth);

          // Change the creation date
          quote.setCreationDateAsDatetype(newDate.getTime());

          // Refresh the shown data
          QuoteViewActivity.this.populateFieldsFromQuote();
        }, newCalendar.get(Calendar.YEAR), newCalendar.get(Calendar.MONTH),
        newCalendar.get(Calendar.DAY_OF_MONTH));

    StartTime.show();
  }
}