package edu.gvsu.cis.cis656.queue;

import java.util.Comparator;
import java.util.LinkedList;

/**
 * Priority Queue.
 */
public class PriorityQueue<T> {

    private LinkedList<T> queue;
    private Comparator<T> comparator;

    public PriorityQueue(Comparator<T> comparator) {
        queue = new LinkedList<T>();
        this.comparator = comparator;
    }

    /**
     * Inserts the specified element into this priority queue.
     * @param e
     */
    public boolean add(T e) {
        if (queue.isEmpty()) {
            return queue.add(e);
        }

        for (int i=0; i<queue.size(); i++) {
            T toCompare = queue.get(i);

            if (comparator.compare(e, toCompare) == -1) {
                queue.add(i, e);
                return true;
            }

            if (comparator.compare(e, toCompare) == 1) {
                continue;
            }

            continue;
        }

        queue.addLast(e);
        return true;
    }

    /**
     * Retrieves, but does not remove, the head of this queue, or returns null if this queue is empty.
     * @return
     */
    public T peek() {
        if (this.size() == 0) {
            return null;
        }

        return queue.peekFirst();
    }

    /**
     * Retrieves and removes the head of this queue, or returns null if this queue is empty.
     * @return
     */
    public T poll() {
        if (this.size() == 0) {
            return null;
        }

        return queue.removeFirst();
    }

    /**
     * Returns true if this queue contains the specified element.
     * @param o
     * @return
     */
    public boolean contains(Object o) {
        return queue.contains(o);
    }

    /**
     * Removes a single instance of the specified element from this queue, if it is present.
     * @param o
     * @return
     */
    public boolean remove(Object o) {
        return queue.remove(o);
    }

    /**
     * Returns the number of elements in this collection.
     */
    public int size() {
        return queue.size();
    }

    /**
     * Removes all of the elements from this priority queue.
     * @return
     */
    public void clear() {
        queue.clear();
    }

    /**
     * Checks if the priority queue is empty.
     * @return
     */
    public boolean isEmpty() {
        return size() == 0;

    }

    /**
     *
     * @return
     */
    @Override
    public String toString() {
        return queue.toString();
    }
}
