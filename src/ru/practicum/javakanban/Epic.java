package ru.practicum.javakanban;

import java.util.ArrayList;

public class Epic extends Task {
    private ArrayList<Subtask> subtasks = new ArrayList<>();

    public Epic(String name, String description, Status status) {
        super(name, description, status);
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

        if (statuses.contains(Status.IN_PROGRESS) || statuses.contains(Status.NEW) && statuses.contains(Status.DONE)) {
            setStatus(Status.IN_PROGRESS);
            return;
        } else if (!(statuses.contains(Status.NEW))) {
            setStatus(Status.DONE);
            return;
        }

        setStatus(Status.NEW);
    }

    public void removeSubtask(Subtask subtask) {
        subtasks.remove(subtask);
    }

    public void clearSubtasks() {
        subtasks.clear();
    }
}
