package com.example.highlighter.data;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;
import java.util.ArrayList;
import java.util.List;

@Dao
public interface QuoteDAO {

  @Query("SELECT * FROM quotes")
  List<Quote> getAll();

  @Query("SELECT * FROM quotes WHERE content LIKE '%' || :contentPart || '%'")
  List<Quote> searchByContent(String contentPart);

  @Query("SELECT * FROM quotes WHERE labels LIKE '%' || :labelPart || '%'")
  List<Quote> searchByLabel(String labelPart);

  @Insert
  void insert(Quote quote);

  @Insert
  void insertAll(ArrayList<Quote> quotes);

  @Update
  void updateAll(Quote... quotes);

  @Delete
  void delete(Quote quote);

  @Query("DELETE FROM quotes")
  void deleteAll();

}
