package com.example.dailyquote;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;
@Dao
public interface QuoteDao {

    @Insert
    void insertQuote(Quote quote);

    @Query("Select * from Quote")
    List<Quote> getAllQuotes();

    @Delete
    void deleteQuote(Quote quote);

    @Query("Delete from Quote")
    void deleteAllQuotes();
}
