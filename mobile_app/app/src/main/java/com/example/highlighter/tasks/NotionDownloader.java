package com.example.highlighter.tasks;

import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.os.Build.VERSION_CODES;
import androidx.annotation.RequiresApi;
import com.example.highlighter.MainActivity;
import com.example.highlighter.data.Quote;
import com.example.highlighter.utils.APIWorker;
import java.util.ArrayList;

public class NotionDownloader extends AsyncTask<Void, Void, ArrayList<Quote>> {

  @SuppressLint("StaticFieldLeak")
  private final MainActivity parentActivity;
  private final APIWorker worker;

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

