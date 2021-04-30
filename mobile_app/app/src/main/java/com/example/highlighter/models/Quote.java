package com.example.highlighter.models;

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

public class Quote implements Serializable {

  private int id = -1;
  private String sourceTitle = "";
  private URL sourceURL = null;
  private String content = "";
  private Date creationDate = null;
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

    try {
      this.setCreationDate(creationDate);
    } catch (ParseException e) {
      this.creationDate = new Date();
    }
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

  public Date getCreationDate() {
    return this.creationDate;
  }

  public void setCreationDate(String date) throws ParseException {
    SimpleDateFormat formatter = new SimpleDateFormat(Configuration.STRINGIFIED_DATE_FORMAT,
        Locale.ENGLISH);
    this.creationDate = formatter.parse(date);
  }

  public String getCreationDateAsDatetype() {
    SimpleDateFormat formatter = new SimpleDateFormat(Configuration.STRINGIFIED_DATE_FORMAT,
        Locale.ENGLISH);

    return formatter.format(this.creationDate);
  }

  public void setCreationDateAsDatetype(Date creationDate) {
    this.creationDate = creationDate;
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
