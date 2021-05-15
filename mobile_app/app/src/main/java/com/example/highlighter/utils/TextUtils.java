package com.example.highlighter.utils;

import android.text.Spannable;
import android.text.TextPaint;
import android.text.style.URLSpan;
import android.widget.TextView;

/**
 * Utility class for text-based objects
 */
public class TextUtils {

  /**
   * Removes the underlined links from a TextView.
   *
   * For more details, see [1].
   *
   * [1] https://stackoverflow.com/questions/9852184/android-textview-hyperlink#answer-9852280
   *
   * @param textView TextView from where the underlined links are removed
   */
  public static void stripUnderlines(TextView textView) {
    Spannable spannable = (Spannable) textView.getText();
    URLSpan[] spans = spannable.getSpans(0, spannable.length(), URLSpan.class);

    for (URLSpan span : spans) {
      int start = spannable.getSpanStart(span);
      int end = spannable.getSpanEnd(span);

      spannable.removeSpan(span);
      span = new URLSpanNoUnderline(span.getURL());
      spannable.setSpan(span, start, end, 0);
    }

    textView.setText(spannable);
  }

  /**
   * Trims a string to a maximum length
   *
   * @param string String to trim
   * @param maxLength Maximum length of the string
   * @param replacement Replacement string for the overflow
   * @return Trimmed string
   */
  public static String trimString(String string, int maxLength, String replacement) {
    if (string.length() > maxLength) {
      string = string.substring(0, maxLength) + replacement;
    }

    return string;
  }

  /**
   * Encodes as hexadecimal a string.
   *
   * @param string String to encode
   * @return Encoded string
   */
  public static String encodeHexadecimal(String string) {
    final char[] stringCharArray = string.toCharArray();

    StringBuilder encodedString = new StringBuilder();
    for (char c : stringCharArray) {
      String hexString = Integer.toHexString(c);
      encodedString.append(hexString);
    }

    return encodedString.toString();
  }

  private static class URLSpanNoUnderline extends URLSpan {

    public URLSpanNoUnderline(String url) {
      super(url);
    }

    @Override
    public void updateDrawState(TextPaint text) {
      super.updateDrawState(text);

      text.setUnderlineText(false);
    }
  }

}