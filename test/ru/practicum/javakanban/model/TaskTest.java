package ru.practicum.javakanban.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class TaskTest {

    private final Integer commonId = 2;

    @Test
    public void equalsTasksPerId() {
        Task task = new Task("Задача", "Описание задачи");
        task.setId(commonId);
        Task task1 = new Task("Какая-то задача", "Какое-то описание");
        task1.setId(commonId);

        assertEquals(task, task1, "Задачи с одинаковым id не равны");
    }
}