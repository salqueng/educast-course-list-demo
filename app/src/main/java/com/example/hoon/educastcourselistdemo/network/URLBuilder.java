package com.example.hoon.educastcourselistdemo.network;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by hoon on 15. 1. 9..
 */
public class URLBuilder {
    public static final String KEY_DEFAULT = "DEFAULT";

    private static final Map<String, URLBuilder> builderMap = new HashMap<>();

    private URLBuilder() {}

    public static URLBuilder get(String key) {
        if (builderMap.containsKey(key)) {
            return builderMap.get(key);
        } else {
            URLBuilder result = new URLBuilder();
            builderMap.put(key, result);
            return result;
        }
    }

    public static URLBuilder get() {
        return get(KEY_DEFAULT);
    }

    public String build(String url) {
        return "https://educast.pro" + "/api/latest/" + url;
    }

    public String build(boolean isStatic, String url){
        return "https://educast.pro" + "/static/" + url;
    }
}
