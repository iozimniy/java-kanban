package ru.practicum.javakanban.http;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import ru.practicum.javakanban.http.DurationAdapter;
import ru.practicum.javakanban.http.LocalDateTimeAdapter;

import java.time.Duration;
import java.time.LocalDateTime;

public class Constans {
    public static final Gson GSON = new GsonBuilder()
            .serializeNulls()
            .registerTypeAdapter(Duration.class, new DurationAdapter())
            .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
            .create();
}
