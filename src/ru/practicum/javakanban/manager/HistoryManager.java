package ru.practicum.javakanban.manager;

import ru.practicum.javakanban.model.Task;

import java.util.ArrayList;
import java.util.LinkedList;


public interface HistoryManager {
    void add(Task task);

    LinkedList<Task> getHistory();
}
