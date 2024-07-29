/*
 * Decompiled with CFR 0.152.
 */
package com.mcf.davidee.nbtedit.nbt;

import java.util.ArrayList;
import java.util.List;

public class Node<T> {
    private List<Node<T>> children;
    private Node<T> parent;
    private T obj;
    private boolean drawChildren;

    public Node() {
        this(null);
    }

    public Node(T obj) {
        this.children = new ArrayList<Node<T>>();
        this.obj = obj;
    }

    public boolean shouldDrawChildren() {
        return this.drawChildren;
    }

    public void setDrawChildren(boolean draw) {
        this.drawChildren = draw;
    }

    public Node(Node<T> parent) {
        this(parent, null);
    }

    public Node(Node<T> parent, T obj) {
        this.parent = parent;
        this.children = new ArrayList<Node<T>>();
        this.obj = obj;
    }

    public void addChild(Node<T> n2) {
        this.children.add(n2);
    }

    public boolean removeChild(Node<T> n2) {
        return this.children.remove(n2);
    }

    public List<Node<T>> getChildren() {
        return this.children;
    }

    public Node<T> getParent() {
        return this.parent;
    }

    public T getObject() {
        return this.obj;
    }

    public String toString() {
        return "" + this.obj;
    }

    public boolean hasChildren() {
        return this.children.size() > 0;
    }

    public boolean hasParent() {
        return this.parent != null;
    }
}

