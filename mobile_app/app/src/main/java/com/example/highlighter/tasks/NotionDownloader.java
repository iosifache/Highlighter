package com.example.highlighter.tasks;

import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.os.Build.VERSION_CODES;
import androidx.annotation.RequiresApi;
import com.example.highlighter.activities.MainActivity;
import com.example.highlighter.data.APIWorker;
import com.example.highlighter.data.Quote;
import java.util.ArrayList;

/**
 * Class implementing an asynchronous task for downloading quotes from Notion.so
 */
public class NotionDownloader extends AsyncTask<Void, Void, ArrayList<Quote>> {

  @SuppressLint("StaticFieldLeak")
  private final MainActivity parentActivity;
  private final APIWorker worker;

  /**
   * Initializes a NotionDownloader object.
   *
   * @param parentActivity Activity using the task
   */
  public NotionDownloader(MainActivity parentActivity) {
    this.parentActivity = parentActivity;
    this.worker = new APIWorker(parentActivity);
  }

  @RequiresApi(api = VERSION_CODES.N)
  @Override
  protected ArrayList<Quote> doInBackground(Void... parameters) {
    return worker.downloadData();
  }

  @Override
  protected void onPostExecute(ArrayList<Quote> result) {
    super.onPostExecute(result);

    this.parentActivity.setQuotesList(result, false);
  }

}

