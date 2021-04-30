package com.example.highlighter.data;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;
import com.example.highlighter.utils.Configuration;
import java.io.Serializable;
import java.net.URL;
import java.text.Normalizer;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;

@Entity(tableName = "quotes")
public class Quote implements Serializable {

  @PrimaryKey(autoGenerate = true)
  private int id;

  @ColumnInfo(name = "source_title")
  private String sourceTitle = "";

  @ColumnInfo(name = "source_url")
  private URL sourceURL = null;

  @ColumnInfo(name = "content")
  private String content = "";

  @ColumnInfo(name = "creation_date")
  private String creationDate = "";

  @ColumnInfo(name = "labels")
  private List<String> labels = null;

  public Quote() {
    super();
  }

  public Quote(int id, String sourceTitle, URL sourceURL, String content, String creationDate,
      List<String> labels) {
    this.id = id;
    this.sourceTitle = sourceTitle;
    this.sourceURL = sourceURL;
    this.content = content;
    this.labels = labels;

    this.setCreationDate(creationDate);
  }

  public int getId() {
    return id;
  }

  public void setId(int id) {
    this.id = id;
  }

  public String getSourceTitle() {
    return this.sourceTitle;
  }

  public void setSourceTitle(String sourceTitle) {
    this.sourceTitle = sourceTitle;
  }

  public URL getSourceURL() {
    return this.sourceURL;
  }

  public void setSourceURL(URL sourceURL) {
    this.sourceURL = sourceURL;
  }

  public String getSourceURLAsString() {
    if (this.sourceURL != null) {
      return this.sourceURL.toString();
    } else {
      return "";
    }
  }

  public String getContent() {
    return this.content;
  }

  public void setContent(String content) {
    this.content = content;
  }

  public String getCreationDate() {
    return this.creationDate;
  }

  public void setCreationDate(String date) {
    this.creationDate = date;
  }

  public Date getCreationDateAsDate() {
    SimpleDateFormat formatter = new SimpleDateFormat(Configuration.STRINGIFIED_DATE_FORMAT,
        Locale.ENGLISH);

    try {
      return formatter.parse(this.creationDate);
    } catch (ParseException e) {
      return null;
    }
  }

  public void setCreationDateAsDate(Date creationDate) {
    SimpleDateFormat formatter = new SimpleDateFormat(Configuration.STRINGIFIED_DATE_FORMAT,
        Locale.ENGLISH);

    this.creationDate = formatter.format(creationDate);
  }

  public List<String> getLabels() {
    return this.labels;
  }

  public void setLabels(List<String> labels) {
    this.labels = labels;
  }

  public String getStringifiedLabels() {
    if (this.labels != null) {
      StringBuilder allLabels = new StringBuilder();
      for (String label : this.labels) {
        allLabels.append(Configuration.TOPIC_PREFIX).append(label)
            .append(Configuration.TOPIC_SEPARATOR);
      }

      return allLabels.substring(0, allLabels.length() - 1);
    } else {
      return "";
    }

  }

  public void setStringifiedLabels(String stringifiedLabels) {
    String separator = Normalizer.normalize(Configuration.TOPIC_SEPARATOR, Normalizer.Form.NFKD);
    String[] new_labels = stringifiedLabels.split(separator);
    for (int i = 0; i < new_labels.length; i++) {
      new_labels[i] = new_labels[i].replace(Configuration.TOPIC_PREFIX, "");
    }

    if (this.labels != null) {
      this.labels.clear();
    } else {
      this.labels = new ArrayList<>();
    }
    this.labels.addAll(Arrays.asList(new_labels));
  }

}
