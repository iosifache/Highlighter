package com.example.highlighter.data;

import android.content.Context;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;
import com.example.highlighter.utils.Converters;

/**
 * Class representing the local database
 */
@Database(entities = {Quote.class}, version = 3)
@TypeConverters(Converters.class)
public abstract class ApplicationDatabase extends RoomDatabase {

  private static ApplicationDatabase INSTANCE;

  /**
   * Gets the instance of this object.
   *
   * @param context Context provided by the activity using the database
   * @return ApplicationDatabase instance
   */
  public static ApplicationDatabase getInstance(Context context) {
    if (ApplicationDatabase.INSTANCE == null) {
      ApplicationDatabase.INSTANCE = Room
          .databaseBuilder(context.getApplicationContext(), ApplicationDatabase.class,
              "quotes-database").allowMainThreadQueries().build();
    }

    return ApplicationDatabase.INSTANCE;
  }

  /**
   * Destroys the ApplicationDatabase instance.
   */
  public static void destroyInstance() {
    ApplicationDatabase.INSTANCE = null;
  }

  public abstract QuoteDAO quoteDAO();

}
