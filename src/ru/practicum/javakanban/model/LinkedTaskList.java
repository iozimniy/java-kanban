package ru.practicum.javakanban.model;

import java.util.ArrayList;
import java.util.HashMap;

public class LinkedTaskList<T> {
    public Node<Task> head;
    public Node<Task> tail;
    HashMap<Integer, Node> linkedTaskMap = new HashMap<>();

    public LinkedTaskList() {
        head = null;
        tail = null;
    }

    public void linkLast(Task task) {
        if (linkedTaskMap.containsKey(task.getId())) {
            removeNode(linkedTaskMap.get(task.getId()));
        }

        final Node<Task> oldTail = tail;
        final Node<Task> node = new Node<>(oldTail, task, null);
        tail = node;

        if (oldTail == null) {
            head = node;
        } else {
            oldTail.setNext(node);
        }
        linkedTaskMap.put(task.getId(), node);
    }

    public ArrayList<Task> getTasks() {
        ArrayList<Task> historyTasks = new ArrayList<>();

        Node<Task> currentNode;
        Node<Task> nextNode = tail;
        while (nextNode != null) {
            currentNode = nextNode;
            nextNode = currentNode.getPrev();
            historyTasks.add(currentNode.getTask());
        }

        return historyTasks;
    }

    public void removeNode(Node node) {
        final Node<Task> next = node.getNext();
        final Node<Task> prev = node.getPrev();

        if (prev == null) {
            head = next;
        } else {
            prev.setNext(next);
            node.setPrev(null);
        }

        if (next == null) {
            tail = prev;
        } else {
            next.setPrev(prev);
            node.setNext(null);
        }

        linkedTaskMap.remove(node.getTaskId());
        node.setTask(null);
    }

    public void remove(int id) {
        if (linkedTaskMap.containsKey(id)) {
            removeNode(linkedTaskMap.get(id));
        }
    }

}
