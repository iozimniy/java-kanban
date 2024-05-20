package ru.practicum.javakanban.manager;

import ru.practicum.javakanban.model.Task;

import java.util.ArrayList;

public class InMemoryHistoryManager implements HistoryManager {

    private final ArrayList<Task> historyList = new ArrayList<>();
    private static final int HISTORY_SIZE = 10;

    @Override
    public void add(Task task) {
        if (historyList.size() == HISTORY_SIZE) {
            historyList.removeFirst();
        }

        historyList.add(task);
    }

    @Override
    public ArrayList<Task> getHistory() {
        return historyList;
    }

    public static int getHistorySize() {
        return HISTORY_SIZE;
    }
}
