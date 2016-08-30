package com.example.hoon.educastcourselistdemo.network;

import java.util.List;

/**
 * Created by hoon on 15. 1. 10..
 */
public class QueryResult<T> {
    private int totalLength;
    private String next;
    private String previous;
    private List<T> results;

    public int getTotalLength() {
        return totalLength;
    }

    public String getNextURL() {
        return next;
    }

    public String getPreviousURL() {
        return previous;
    }

    public List<T> getResults() {
        return results;
    }
}
