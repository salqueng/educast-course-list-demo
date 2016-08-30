package com.example.hoon.educastcourselistdemo.utils.gson;

import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;
import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.google.gson.annotations.Expose;

import java.lang.reflect.Type;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;

/**
 * Created by hoon on 15. 1. 28..
 */
public class CommonGson {

    private static final SimpleDateFormat sDateFormat =
            new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
    private static final SimpleDateFormat sDateTimeFormat =
            new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault());

    private static final JsonSerializer<Date> dateJsonSerializer = new JsonSerializer<Date>() {
        @Override
        public JsonElement serialize(Date src, Type typeOfSrc, JsonSerializationContext context) {
            if (src == null) {
                return null;
            }

            // Calendar

            GregorianCalendar cal = new GregorianCalendar();
            cal.setTime(src);

            long checkTime =
                    src.getTime() + cal.get(Calendar.ZONE_OFFSET) + cal.get(Calendar.DST_OFFSET);

            if (checkTime % (24 * 3600 * 1000L) == 0) {
                return new JsonPrimitive(sDateFormat.format(src));
            } else {
                return new JsonPrimitive(sDateTimeFormat.format(src));
            }
        }
    };

    private static final JsonDeserializer<Date> dateJsonDeserializer = new JsonDeserializer<Date>() {
        @Override
        public Date deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            String str = json.getAsString();

            try {
                return sDateTimeFormat.parse(str);
            } catch (ParseException e) {
                throw new JsonParseException(e);
            }
        }
    };

    private static final Gson gson = new GsonBuilder()
            .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
            //.setDateFormat("yyyy-MM-dd'T'HH:mm:ss")
            .registerTypeAdapter(Date.class, dateJsonSerializer)
            .registerTypeAdapter(Date.class, dateJsonDeserializer)
            .addSerializationExclusionStrategy(new ExclusionStrategy() {
                @Override
                public boolean shouldSkipField(FieldAttributes fieldAttributes) {
                    final Expose expose = fieldAttributes.getAnnotation(Expose.class);
                    return expose != null && !expose.serialize();
                }

                @Override
                public boolean shouldSkipClass(Class<?> aClass) {
                    return false;
                }
            })
            .addDeserializationExclusionStrategy(new ExclusionStrategy() {
                @Override
                public boolean shouldSkipField(FieldAttributes fieldAttributes) {
                    final Expose expose = fieldAttributes.getAnnotation(Expose.class);
                    return expose != null && !expose.deserialize();
                }

                @Override
                public boolean shouldSkipClass(Class<?> aClass) {
                    return false;
                }
            })
            .create();

    public static Gson get() {
        return gson;
    }
}
