package ru.practicum.javakanban.model;

import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

class TaskTest {

    private final Integer commonId = 2;
    private static LocalDateTime TASKS_DATE_TIME = LocalDateTime.of(2024,12,31,12,30);
    private static Duration TASKS_DURATION = Duration.ofMinutes(30);


    @Test
    public void equalsTasksPerId() {
        Task task = new Task("Задача", "Описание задачи", TASKS_DURATION, TASKS_DATE_TIME);
        task.setId(commonId);
        Task task1 = new Task("Какая-то задача", "Какое-то описание");
        task1.setId(commonId);

        assertEquals(task, task1, "Задачи с одинаковым id не равны");
    }
}