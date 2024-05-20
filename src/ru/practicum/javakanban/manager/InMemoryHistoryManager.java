package ru.practicum.javakanban.manager;

import ru.practicum.javakanban.model.Task;

import java.util.LinkedList;
import java.util.List;

public class InMemoryHistoryManager implements HistoryManager {

    private final List<Task> historyList = new LinkedList<>();
    private static final int HISTORY_SIZE = 10;

    @Override
    public void add(Task task) {
        if (historyList.size() == HISTORY_SIZE) {
            historyList.removeFirst();
        }

        historyList.add(task);
    }

    @Override
    public List<Task> getHistory() {
        return historyList;
    }

    public static int getHistorySize() {
        return HISTORY_SIZE;
    }
}
