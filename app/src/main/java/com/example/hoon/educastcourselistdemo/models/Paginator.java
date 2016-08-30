package com.example.hoon.educastcourselistdemo.models;

import java.util.List;

/**
 * Created by hoon on 2016. 8. 27..
 */
public class Paginator<T> {
    public List<T> results;
    public int count;
    public String next;
    public String previous;
}
