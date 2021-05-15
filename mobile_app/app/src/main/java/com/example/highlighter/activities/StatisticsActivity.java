package com.example.highlighter.activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build.VERSION_CODES;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.TextView;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import com.example.highlighter.R;
import com.example.highlighter.data.ApplicationDatabase;
import com.example.highlighter.data.Quote;
import com.example.highlighter.utils.Configuration;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.formatter.ValueFormatter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Activity displaying statistics about the quotes
 */
public class StatisticsActivity extends AppCompatActivity {

  private List<Quote> quotesList = null;

  /**
   * Starts the activity.
   *
   * @param savedInstanceState Saved data
   */
  @RequiresApi(api = VERSION_CODES.N)
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_statistics);

    // Get the quotes from the database
    ApplicationDatabase database = ApplicationDatabase.getInstance(this);
    this.quotesList = database.quoteDAO().getAll();

    // Compute the statistics about the labels and for their dates
    HashMap<String, LabelDetails> labelsDetails = new HashMap<>();
    int[] quotesPerDay = new int[Configuration.UI_DAILY_QUOTES_TO_DISPLAY];
    Date now = new Date();
    for (Quote quote : quotesList) {
      Date quoteCreationDate = quote.getCreationDateAsDate();

      // Check if the quote needs to be considered into the chart
      int dayDifference = (int) ((now.getTime() - quoteCreationDate.getTime()) / (1000 * 60 * 60
          * 24));
      if (dayDifference < Configuration.UI_DAILY_QUOTES_TO_DISPLAY) {
        quotesPerDay[dayDifference] += 1;
      }

      // Consider each label for the ranking generation
      for (String label : quote.getLabels()) {
        if (!labelsDetails.containsKey(label)) {
          labelsDetails.put(label, new LabelDetails(quoteCreationDate));
        } else {
          LabelDetails details = labelsDetails.get(label);

          if (details != null) {
            details.occurrences += 1;
            if (quoteCreationDate.compareTo(details.lastUsage) > 0) {
              details.lastUsage = quoteCreationDate;
            }
          }
        }
      }
    }

    // Bind the information
    this.generateQuotesCount();
    this.generateLabelsRanking(labelsDetails);
    this.generateDaysChart(quotesPerDay);
  }

  @SuppressLint("StringFormatMatches")
  private void generateQuotesCount() {
    int quotesCount = quotesList.size();

    TextView quotesCountTextView = this.findViewById(R.id.tv_quotes_count);
    String quotesCountFormat = this.getResources()
        .getString(R.string.msg_statistic_total_quotes_fmt);
    quotesCountTextView.setText(Html.fromHtml(String.format(quotesCountFormat, quotesCount)));
  }

  @RequiresApi(api = VERSION_CODES.N)
  private void generateLabelsRanking(HashMap<String, LabelDetails> labelsDetails) {
    // Sort the mapping between label name - details
    Map<String, LabelDetails> sortedLabelsDetails = labelsDetails.entrySet().stream()
        .sorted(Comparator.comparingInt(e -> -e.getValue().occurrences))
        .collect(Collectors.toMap(
            Map.Entry::getKey,
            Map.Entry::getValue,
            (a, b) -> {
              throw new AssertionError();
            },
            LinkedHashMap::new
        ));

    // Generate a ranking with the most used labels
    StringBuilder rankingText = new StringBuilder(
        this.getResources().getString(R.string.msg_most_popular_labels));
    int index = 1;
    String popularLabelFormat = this.getResources().getString(R.string.msg_popular_label_fmt);
    SimpleDateFormat formatter = new SimpleDateFormat(Configuration.UI_STRINGIFIED_DATE_FORMAT,
        Locale.ENGLISH);
    for (Map.Entry<String, LabelDetails> entry : sortedLabelsDetails.entrySet()) {
      String label = entry.getKey();
      LabelDetails details = entry.getValue();

      rankingText.append(String.format(popularLabelFormat, index, label, details.occurrences,
          formatter.format(details.lastUsage)));

      index++;
      if (index > Configuration.UI_POPULAR_LABELS_COUNT) {
        break;
      }
    }
    rankingText.append(".");

    // Bind the ranking
    TextView labelsRankingTextView = this.findViewById(R.id.tv_ranking);
    labelsRankingTextView.setText(Html.fromHtml(rankingText.toString()));
  }

  @SuppressLint("DefaultLocale")
  private void generateDaysChart(int[] quotesPerDay) {
    String todayLabel = this.getResources().getString(R.string.label_chart_today);
    String yesterdayLabel = this.getResources().getString(R.string.label_chart_yesterday);
    String daysAgoLabelFormat = this.getResources().getString(R.string.label_chart_days_ago_fmt);

    List<BarEntry> entries = new ArrayList<>();
    String[] labels = new String[quotesPerDay.length];
    for (int i = 0; i < quotesPerDay.length; i++) {
      // Create the entry
      entries.add(new BarEntry(i, quotesPerDay[i]));

      // Create the corresponding label
      switch (i) {
        case 0:
          labels[i] = todayLabel;
          break;
        case 1:
          labels[i] = yesterdayLabel;
          break;
        default:
          labels[i] = String.format(daysAgoLabelFormat, i);
      }
    }

    // Define a value formatter to be used for all axis
    ValueFormatter formatter = new ValueFormatter() {
      @Override
      public String getFormattedValue(float value) {
        return "" + (int) value;
      }
    };

    // Get the primary color of the chart
    SharedPreferences sharedPreferences = this.getSharedPreferences(
        Configuration.SHARED_PREFERENCES_NAME, MODE_PRIVATE);
    boolean darkThemeEnabled = sharedPreferences
        .getBoolean(Configuration.SHARED_PREFERENCES_MEMBER_DARK_THEME, false);
    int color;
    if (darkThemeEnabled) {
      color = ContextCompat.getColor(this, R.color.white);
    } else {
      color = ContextCompat.getColor(this, R.color.blue_nights_gray);
    }

    // Create the dataset and set its color
    String description = this.getResources().getString(R.string.desc_quotes_per_day_chart);
    BarDataSet dataset = new BarDataSet(entries, description);
    dataset.setColor(color);

    // Convert the dataset to raw data used by the chart
    BarData data = new BarData(dataset);
    data.setValueFormatter(formatter);

    // Generate and format the chart
    BarChart labelsBarChart = this.findViewById(R.id.bc_chart);
    labelsBarChart.getDescription().setEnabled(false);
    XAxis xAxis = labelsBarChart.getXAxis();
    xAxis.setValueFormatter(new IndexAxisValueFormatter(labels));
    xAxis.setLabelRotationAngle(-30);
    xAxis.setLabelCount(labels.length);
    xAxis.setGranularity(1f);
    xAxis.setGranularityEnabled(true);
    xAxis.setGridColor(color);
    xAxis.setTextColor(color);
    xAxis.setAxisLineColor(color);
    YAxis leftYAxis = labelsBarChart.getAxisLeft();
    leftYAxis.setValueFormatter(formatter);
    leftYAxis.setGranularity(1f);
    leftYAxis.setGranularityEnabled(true);
    leftYAxis.setGridColor(color);
    leftYAxis.setTextColor(color);
    leftYAxis.setAxisLineColor(color);
    leftYAxis.setZeroLineColor(color);
    YAxis rightYAxis = labelsBarChart.getAxisRight();
    rightYAxis.setValueFormatter(formatter);
    rightYAxis.setGranularity(1f);
    rightYAxis.setGranularityEnabled(true);
    rightYAxis.setGridColor(color);
    rightYAxis.setTextColor(color);
    rightYAxis.setAxisLineColor(color);
    rightYAxis.setZeroLineColor(color);

    // Set the data and refresh the chart
    labelsBarChart.setData(data);
    labelsBarChart.invalidate();
  }

  /**
   * Goes back to the calling activity.
   *
   * @param view View representing the clicked back button
   */
  public void goBack(View view) {
    Intent intent = new Intent();
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

  private static class LabelDetails {

    public int occurrences;
    public Date lastUsage;

    public LabelDetails(Date lastUsage) {
      this.occurrences = 1;
      this.lastUsage = lastUsage;
    }

  }
}