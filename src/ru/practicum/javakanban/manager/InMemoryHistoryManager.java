package ru.practicum.javakanban.manager;

import ru.practicum.javakanban.model.LinkedTaskList;
import ru.practicum.javakanban.model.Task;

import java.util.List;

public class InMemoryHistoryManager implements HistoryManager {

    private final LinkedTaskList<Task> historyList = new LinkedTaskList<>();

    @Override
    public void add(Task task) {
        historyList.linkLast(task);
    }

    @Override
    public List<Task> getHistory() {
        return historyList.getTasks();
    }

    @Override
    public void remove(int id) {
        historyList.remove(id);
    }
}
