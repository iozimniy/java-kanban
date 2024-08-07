package ru.practicum.javakanban.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Epic extends Task {
    private final List<Subtask> subtasks = new ArrayList<>();

    public Epic(String name, String description) {
        super(name, description);
        status = Status.NEW;
    }

    public Epic(String name, String description, Integer id, Status status) {
        super(name, description, id, status);
    }

    public List<Subtask> getSubtasks() {
        return subtasks;
    }

    @Override
    public TaskType getType() {
        return TaskType.EPIC;
    }

    @Override
    public String toString() {
        return getType() + "{" +
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

        List<Status> statuses = new ArrayList<>();

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

    @Override
    public String convertToString() {
        return getId().toString() + "," + getType().toString() + "," + getName() + "," + getStatus().toString()
                + "," + getDescription();
    }
}
