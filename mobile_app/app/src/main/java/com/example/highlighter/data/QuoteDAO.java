package com.example.highlighter.data;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;
import java.util.ArrayList;
import java.util.List;

/**
 * Data access object representing a quote
 */
@Dao
public interface QuoteDAO {

  /**
   * Gets all quote from the database.
   *
   * @return List of quotes
   */
  @Query("SELECT * FROM quotes")
  List<Quote> getAll();

  /**
   * Search quotes by content.
   *
   * @param contentPart Part of the content to search by
   * @return List of matching quotes
   */
  @Query("SELECT * FROM quotes WHERE content LIKE '%' || :contentPart || '%'")
  List<Quote> searchByContent(String contentPart);

  /**
   * Search quotes by labels.
   *
   * @param labelPart Part of the labels to search by
   * @return List of matching quotes
   */
  @Query("SELECT * FROM quotes WHERE labels LIKE '%' || :labelPart || '%'")
  List<Quote> searchByLabel(String labelPart);

  /**
   * Inserts a quote into the database.
   *
   * @param quote Quote to insert
   */
  @Insert
  void insert(Quote quote);

  /**
   * Insert all quotes from a list into the database.
   *
   * @param quotes List of quotes to insert
   */
  @Insert
  void insertAll(ArrayList<Quote> quotes);

  /**
   * Updates in database all quotes from a list.
   *
   * @param quotes List of quotes to update
   */
  @Update
  void updateAll(Quote... quotes);

  /**
   * Delete a quote from the database.
   *
   * @param quote Quote to remove
   */
  @Delete
  void delete(Quote quote);

  /**
   * Deletes all quotes from the database.
   */
  @Query("DELETE FROM quotes")
  void deleteAll();

}
