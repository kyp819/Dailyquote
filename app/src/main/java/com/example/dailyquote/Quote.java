package com.example.dailyquote;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class Quote {
    @PrimaryKey(autoGenerate = true)
    public int id;

    @ColumnInfo(name = "quote")
    public String quote;

    @ColumnInfo(name = "author")
    public String author;

}
