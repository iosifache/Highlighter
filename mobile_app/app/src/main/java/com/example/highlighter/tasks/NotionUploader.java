package com.example.highlighter.tasks;

import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.os.Build.VERSION_CODES;
import androidx.annotation.RequiresApi;
import com.example.highlighter.MainActivity;
import com.example.highlighter.utils.APIWorker;

public class NotionUploader extends AsyncTask<Void, Void, Void> {

  @SuppressLint("StaticFieldLeak")
  private final MainActivity parentActivity;
  private final APIWorker worker;

  public NotionUploader(MainActivity parentActivity) {
    this.parentActivity = parentActivity;
    this.worker = new APIWorker(parentActivity);
  }

  @RequiresApi(api = VERSION_CODES.N)
  @Override
  protected Void doInBackground(Void... parameters) {
    worker.uploadData(this.parentActivity.jsonifyQuotesList());

    return null;
  }

  @Override
  protected void onPostExecute(Void result) {
    super.onPostExecute(result);
  }
}

