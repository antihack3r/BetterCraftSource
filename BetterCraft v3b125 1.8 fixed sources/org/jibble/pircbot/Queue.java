/*
 * Decompiled with CFR 0.152.
 */
package org.jibble.pircbot;

import java.util.Vector;

public class Queue {
    private Vector _queue = new Vector();

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void add(Object object) {
        Vector vector = this._queue;
        synchronized (vector) {
            this._queue.addElement(object);
            this._queue.notify();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void addFront(Object object) {
        Vector vector = this._queue;
        synchronized (vector) {
            this._queue.insertElementAt(object, 0);
            this._queue.notify();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public Object next() {
        Object var1_1 = null;
        Vector vector = this._queue;
        synchronized (vector) {
            if (this._queue.size() == 0) {
                try {
                    this._queue.wait();
                }
                catch (InterruptedException interruptedException) {
                    return null;
                }
            }
            try {
                var1_1 = this._queue.firstElement();
                this._queue.removeElementAt(0);
            }
            catch (ArrayIndexOutOfBoundsException arrayIndexOutOfBoundsException) {
                throw new InternalError("Race hazard in Queue object.");
            }
        }
        return var1_1;
    }

    public boolean hasNext() {
        return this.size() != 0;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void clear() {
        Vector vector = this._queue;
        synchronized (vector) {
            this._queue.removeAllElements();
        }
    }

    public int size() {
        return this._queue.size();
    }
}

