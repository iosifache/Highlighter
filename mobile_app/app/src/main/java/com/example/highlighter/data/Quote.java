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

/**
 * Class encapsulating details about a quote
 */
@Entity(tableName = "quotes")
public class Quote implements Serializable {

  @PrimaryKey(autoGenerate = true)
  private int id;

  @ColumnInfo(name = "source_title")
  private String sourceTitle = "";

  @ColumnInfo(name = "source_url")
  private URL sourceUrl = null;

  @ColumnInfo(name = "content")
  private String content = "";

  @ColumnInfo(name = "creation_date")
  private String creationDate = "";

  @ColumnInfo(name = "labels")
  private List<String> labels = null;

  /**
   * Default constructor
   */
  public Quote() {
    super();
  }

  /**
   * Initializes the Quote instance.
   *
   * @param id           Quote ID
   * @param sourceTitle  Title of the source
   * @param sourceUrl    URL of the source
   * @param content      Content of the quote
   * @param creationDate Creation date of the quote
   * @param labels       List of labels
   */
  public Quote(int id, String sourceTitle, URL sourceUrl, String content, String creationDate,
      List<String> labels) {
    this.id = id;
    this.sourceTitle = sourceTitle;
    this.sourceUrl = sourceUrl;
    this.content = content;
    this.labels = labels;

    this.setCreationDate(creationDate);
  }

  /**
   * Gets the identification number.
   *
   * @return Identification number
   */
  public int getId() {
    return id;
  }

  /**
   * Sets the identification number.
   *
   * @param id Identification number
   */
  public void setId(int id) {
    this.id = id;
  }

  /**
   * Gets the title of the source.
   *
   * @return Title of the source
   */
  public String getSourceTitle() {
    return this.sourceTitle;
  }

  /**
   * Sets the title of the source.
   *
   * @param sourceTitle Title of the source
   */
  public void setSourceTitle(String sourceTitle) {
    this.sourceTitle = sourceTitle;
  }

  /**
   * Gets the URL of the source.
   *
   * @return URL of the source
   */
  public URL getSourceUrl() {
    return this.sourceUrl;
  }

  /**
   * Sets the URL of the source.
   *
   * @param sourceUrl URL of the source
   */
  public void setSourceUrl(URL sourceUrl) {
    this.sourceUrl = sourceUrl;
  }

  /**
   * Gets the URL of the source as a string.
   *
   * @return String representing the URL of the source
   */
  public String getSourceUrlAsString() {
    if (this.sourceUrl != null) {
      return this.sourceUrl.toString();
    } else {
      return "";
    }
  }

  /**
   * Gets the content.
   *
   * @return Content
   */
  public String getContent() {
    return this.content;
  }

  /**
   * Sets the content.
   *
   * @param content Content
   */
  public void setContent(String content) {
    this.content = content;
  }

  /**
   * Gets the creation date as a string.
   *
   * @return String representing the creation date
   */
  public String getCreationDate() {
    return this.creationDate;
  }

  /**
   * Sets the creation date from a string.
   *
   * @param date String representing the creation date
   */
  public void setCreationDate(String date) {
    this.creationDate = date;
  }

  /**
   * Gets the creation date as a Date object.
   *
   * @return Creation date
   */
  public Date getCreationDateAsDate() {
    SimpleDateFormat formatter = new SimpleDateFormat(Configuration.UI_STRINGIFIED_DATE_FORMAT,
        Locale.ENGLISH);

    try {
      return formatter.parse(this.creationDate);
    } catch (ParseException e) {
      e.printStackTrace();

      return null;
    }
  }

  /**
   * Sets the creation date from a Date object.
   *
   * @param creationDate Creation date
   */
  public void setCreationDateAsDate(Date creationDate) {
    SimpleDateFormat formatter = new SimpleDateFormat(Configuration.UI_STRINGIFIED_DATE_FORMAT,
        Locale.ENGLISH);

    this.creationDate = formatter.format(creationDate);
  }

  /**
   * Gets the labels as a list.
   *
   * @return List of labels
   */
  public List<String> getLabels() {
    return this.labels;
  }

  /**
   * Sets the labels from a list.
   *
   * @param labels List of labels
   */
  public void setLabels(List<String> labels) {
    this.labels = labels;
  }

  /**
   * Gets the labels as a string obtained by concatenation.
   *
   * @return String representing the labels.
   */
  public String getStringifiedLabels() {
    if (this.labels != null) {
      StringBuilder allLabels = new StringBuilder();
      for (String label : this.labels) {
        allLabels.append(Configuration.UI_TOPIC_PREFIX).append(label)
            .append(Configuration.UI_TOPIC_SEPARATOR);
      }

      return allLabels.substring(0, allLabels.length() - 1);
    } else {
      return "";
    }

  }

  /**
   * Sets the labels by splitting a string.
   *
   * @param stringifiedLabels String representing the labels
   */
  public void setStringifiedLabels(String stringifiedLabels) {
    String separator = Normalizer.normalize(Configuration.UI_TOPIC_SEPARATOR, Normalizer.Form.NFKD);
    String[] new_labels = stringifiedLabels.split(separator);
    for (int i = 0; i < new_labels.length; i++) {
      new_labels[i] = new_labels[i].replace(Configuration.UI_TOPIC_PREFIX, "");
    }

    if (this.labels != null) {
      this.labels.clear();
    } else {
      this.labels = new ArrayList<>();
    }
    this.labels.addAll(Arrays.asList(new_labels));
  }

}
