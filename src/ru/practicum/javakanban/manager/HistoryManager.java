package ru.practicum.javakanban.manager;

import ru.practicum.javakanban.model.Task;
import java.util.ArrayList;


public interface HistoryManager <T extends Task> {
    void add(Task task);
    ArrayList<Task> getHistory();
}
