package com.example.highlighter;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Build.VERSION_CODES;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.TextView;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import com.example.highlighter.data.ApplicationDatabase;
import com.example.highlighter.data.Quote;
import com.example.highlighter.utils.Configuration;
import com.github.mikephil.charting.charts.BarChart;
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

public class StatisticsActivity extends AppCompatActivity {

  private List<Quote> quotesList = null;

  @RequiresApi(api = VERSION_CODES.N)
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_statistics);

    // Get the quotes
    ApplicationDatabase database = ApplicationDatabase.getInstance(this);
    this.quotesList = database.quoteDAO().getAll();

    // Get statistics about the labels and for the dates
    HashMap<String, LabelDetails> labelsDetails = new HashMap<>();
    int[] quotesPerDay = new int[Configuration.DAILY_QUOTES_TO_DISPLAY];
    Date now = new Date();
    for (Quote quote : quotesList) {
      Date quoteCreationDate = quote.getCreationDateAsDate();

      // Check if the quote needs to be considered into the chart
      int dayDifference = (int) ((now.getTime() - quoteCreationDate.getTime()) / (1000 * 60 * 60
          * 24));
      if (dayDifference < Configuration.DAILY_QUOTES_TO_DISPLAY) {
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

    // Bind the information into the user interface
    this.generateQuotesCount();
    this.generateLabelsRanking(labelsDetails);
    this.generateDaysChart(quotesPerDay);

  }

  private void generateQuotesCount() {
    int quotesCount = quotesList.size();

    TextView quotesCountTextView = this.findViewById(R.id.tv_quotes_count);
    quotesCountTextView.setText(
        Html.fromHtml(String.format(Configuration.QUOTES_COUNT_MSG_FMT, quotesCount)));
  }

  @RequiresApi(api = VERSION_CODES.N)
  private void generateLabelsRanking(HashMap<String, LabelDetails> labelsDetails) {
    // Sort the mapping
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
    int index = 1;
    StringBuilder rankingTextBuilder = new StringBuilder();
    rankingTextBuilder.append(Configuration.POPULAR_LABELS);
    SimpleDateFormat formatter = new SimpleDateFormat(Configuration.STRINGIFIED_DATE_FORMAT,
        Locale.ENGLISH);
    for (Map.Entry<String, LabelDetails> entry : sortedLabelsDetails.entrySet()) {
      String label = entry.getKey();
      LabelDetails details = entry.getValue();

      rankingTextBuilder
          .append(String.format(Configuration.POPULAR_LABEL_FMT, index, label, details.occurrences,
              formatter.format(details.lastUsage)));

      index++;
      if (index > Configuration.POPULAR_LABELS_COUNT) {
        break;
      }
    }
    rankingTextBuilder.append(".");

    // Bind the ranking
    TextView labelsRankingTextView = this.findViewById(R.id.tv_labels_ranking);
    labelsRankingTextView.setText(Html.fromHtml(rankingTextBuilder.toString()));
  }

  @SuppressLint("DefaultLocale")
  private void generateDaysChart(int[] quotesPerDay) {
    List<BarEntry> entries = new ArrayList<>();
    String[] labels = new String[quotesPerDay.length];
    for (int i = 0; i < quotesPerDay.length; i++) {
      // Create the entry
      entries.add(new BarEntry(i, quotesPerDay[i]));

      // Create the corresponding label
      switch (i) {
        case 0:
          labels[i] = Configuration.TODAY_CHART_LABEL;
          break;
        case 1:
          labels[i] = Configuration.YESTERDAY_CHART_LABEL;
          break;
        default:
          labels[i] = String.format(Configuration.DAYS_AGO_LABEL_FMT, i);
      }
    }

    // Define a value formatter for all axis
    ValueFormatter formatter = new ValueFormatter() {
      @Override
      public String getFormattedValue(float value) {
        return "" + (int) value;
      }
    };

    // Create the dataset and set its color
    int color = ContextCompat.getColor(this, R.color.blue_nights_gray);
    BarDataSet dataset = new BarDataSet(entries, Configuration.QUOTES_PER_DAY_CHART_LABEL);
    dataset.setColor(color);

    // Convert the dataset to raw data used by the chart
    BarData data = new BarData(dataset);
    data.setValueFormatter(formatter);

    // Generate and format the chart
    BarChart chart = this.findViewById(R.id.chart);
    chart.getXAxis().setLabelRotationAngle(-30);
    chart.getAxisLeft().setValueFormatter(formatter);
    chart.getAxisRight().setValueFormatter(formatter);
    chart.getXAxis().setValueFormatter(new IndexAxisValueFormatter(labels));
    chart.getXAxis().setLabelCount(labels.length);
    chart.getXAxis().setGranularity(1f);
    chart.getAxisLeft().setGranularity(1f);
    chart.getAxisRight().setGranularity(1f);
    chart.getAxisLeft().setGranularityEnabled(true);
    chart.getAxisRight().setGranularityEnabled(true);
    chart.getXAxis().setGranularityEnabled(true);
    chart.getDescription().setEnabled(false);

    // Set the data and refresh the chart
    chart.setData(data);
    chart.invalidate();
  }

  public void goBack(View view) {
    Intent intent = new Intent();

    setResult(RESULT_OK, intent);
    finish();
    overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
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