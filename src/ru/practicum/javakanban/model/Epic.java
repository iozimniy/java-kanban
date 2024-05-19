package ru.practicum.javakanban.model;

import java.util.ArrayList;
import java.util.Collections;

public class Epic extends Task {
    private final ArrayList<Subtask> subtasks = new ArrayList<>();

    public Epic(String name, String description) {
        super(name, description);
        status = Status.NEW;
    }

    public ArrayList<Subtask> getSubtasks() {
        return subtasks;
    }


    @Override
    public String toString() {
        return "Epic{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", subtasks=" + subtasks.size() +
                ", status=" + status +
                '}';
    }


    public void updateStatus() {
        if (subtasks.isEmpty()) {
            setStatus(Status.DONE);
            return;
        }

        ArrayList<Status> statuses = new ArrayList<>();

        for (Subtask epicSubtask : subtasks) statuses.add(epicSubtask.getStatus());

        Collections.sort(statuses);

        if (statuses.getFirst() == statuses.getLast()) {
            setStatus(statuses.getLast());
        } else {
            setStatus(Status.IN_PROGRESS);
        }
    }

    public void removeSubtask(Subtask subtask) {
        subtasks.remove(subtask);
    }

    public void clearSubtasks() {
        subtasks.clear();
    }
}
