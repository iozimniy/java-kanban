package ru.practicum.javakanban.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class LinkedTaskList {
    public Node head;
    public Node tail;
    HashMap<Integer, Node> linkedTaskMap = new HashMap<>();

    public LinkedTaskList() {
        head = null;
        tail = null;
    }

    public void linkLast(Task task) {
        if (linkedTaskMap.containsKey(task.getId())) {
            removeNode(linkedTaskMap.get(task.getId()));
        }

        final Node oldTail = tail;
        final Node node = new Node(oldTail, task, null);
        tail = node;

        if (oldTail == null) {
            head = node;
        } else {
            oldTail.setNext(node);
        }
        linkedTaskMap.put(task.getId(), node);
    }

    public List<Task> getTasks() {
        List<Task> historyTasks = new ArrayList<>();

        Node currentNode;
        Node nextNode = tail;
        while (nextNode != null) {
            currentNode = nextNode;
            nextNode = currentNode.getPrev();
            historyTasks.add(currentNode.getTask());
        }

        return historyTasks;
    }

    public void removeNode(Node node) {
        final Node next = node.getNext();
        final Node prev = node.getPrev();

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
