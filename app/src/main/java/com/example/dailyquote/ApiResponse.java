package com.example.dailyquote;

import java.util.List;

public class ApiResponse {
    private boolean success;
    private String msg;
    private List<Quote> data;

    public boolean isSuccess() {
        return success;
    }

    public String getMsg() {
        return msg;
    }

    public List<Quote> getData() {
        return data;
    }

    public class Quote {
        private String _id;
        private int id;
        private String quote;
        private String author;

        public String get_id() {
            return _id;
        }

        public int getId() {
            return id;
        }

        public String getQuote() {
            return quote;
        }

        public String getAuthor() {
            return author;
        }
    }
}

