package ru.practicum.javakanban.model;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

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

    public void updateTimes() {
        setStartTime(getStartTime());
        setDuration(getDuration());
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
    public LocalDateTime getStartTime() {
        Optional<LocalDateTime> startEpicTime = subtasks.stream()
                .filter(subtask -> subtask.getStartTime() != null)
                .map(subtask -> subtask.getStartTime())
                .min(LocalDateTime::compareTo);

        if (startEpicTime.isPresent()) {
            return startEpicTime.get();
        } else {return null;}
    }

    @Override
    public Duration getDuration() {
        return subtasks
                .stream()
                .filter(subtask -> subtask.getDuration() != null)
                .map(subtask -> subtask.getDuration())
                .reduce(Duration.ofSeconds(0), Duration::plus);
    }

    public LocalDateTime getEndTime() {
        Optional<LocalDateTime> endEpicTime = subtasks.stream()
                .filter(subtask -> subtask.getStartTime() != null && subtask.getDuration() != null)
                .map(subtask -> subtask.getEndTime())
                .max(LocalDateTime::compareTo);
        return endEpicTime.get();
    }

    @Override
    public String convertToString() {
        return getId().toString() + "," + getType().toString() + "," + getName() + "," + getStatus().toString()
                + "," + getDescription();
    }
}
