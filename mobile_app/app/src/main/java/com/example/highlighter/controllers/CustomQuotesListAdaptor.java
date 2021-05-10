package com.example.highlighter.controllers;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import com.example.highlighter.MainActivity;
import com.example.highlighter.QuoteViewActivity;
import com.example.highlighter.R;
import com.example.highlighter.data.Quote;
import com.example.highlighter.utils.Configuration;
import info.androidhive.fontawesome.FontTextView;
import java.util.ArrayList;

public class CustomQuotesListAdaptor extends ArrayAdapter<Quote> {

  private final Context context;

  public CustomQuotesListAdaptor(ArrayList<Quote> data, Context context) {
    super(context, R.layout.activity_quotes_list_item, data);

    this.context = context;
  }

  private String trimString(String string) {
    if (string.length() > Configuration.MAX_QUOTE_CONTENT) {
      string = string.substring(0, Configuration.MAX_QUOTE_CONTENT)
          + Configuration.QUOTE_OVERFLOW_REPLACEMENT;
    }

    return string;
  }

  @Override
  public View getView(int position, View convertView, ViewGroup parent) {
    // Inflate the template
    View listItem = convertView;
    if (listItem == null) {
      listItem = LayoutInflater.from(this.context)
          .inflate(R.layout.activity_quotes_list_item, parent, false);
    }

    // Get the current quote
    Quote currentQuote = getItem(position);

    // Add the trimmed quote content
    TextView quoteContent = listItem.findViewById(R.id.tv_quotes_list_item_content);
    quoteContent.setText(this.trimString(currentQuote.getContent()));

    // Adds the topics
    TextView labelsGroup = listItem.findViewById(R.id.tv_quotes_list_item_labels);
    labelsGroup.setText(currentQuote.getStringifiedLabels());

    // Set a click event for the edit button
    FontTextView editButton = listItem
        .findViewById(R.id.btn_quotes_list_item_view_quote);
    editButton.setOnClickListener(view -> {
      Intent intent = new Intent((context), QuoteViewActivity.class);
      Quote quote = getItem(position);
      intent.putExtra(Configuration.INTENT_EXTRA_MODIFIED_QUOTE_NAME, quote);

      ((MainActivity) context)
          .startActivityForResult(intent, Configuration.INTENT_REQ_CODE_QUOTE_VIEW);
      ((MainActivity) context).overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    });

    return listItem;
  }

}