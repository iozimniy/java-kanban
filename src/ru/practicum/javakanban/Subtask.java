package ru.practicum.javakanban;

public class Subtask extends Task {

    int epic_id;

    public Subtask(String name, String description) {
        super(name, description);
    }

    public Integer getEpic_id() {
        return epic_id;
    }

    public void setEpic_id(Integer epic_id) {
        this.epic_id = epic_id;
    }

    @Override
    public String toString() {
        return "Subtask{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", epic_id=" + epic_id +
                ", status=" + status +
                '}';
    }
}
