/*
 * Decompiled with CFR 0.152.
 */
package net.optifine.shaders;

import java.util.ArrayDeque;
import java.util.Deque;
import net.optifine.shaders.Program;

public class ProgramStack {
    private Deque<Program> stack = new ArrayDeque<Program>();

    public void push(Program p2) {
        this.stack.addLast(p2);
        if (this.stack.size() > 100) {
            throw new RuntimeException("Program stack overflow: " + this.stack.size());
        }
    }

    public Program pop() {
        if (this.stack.isEmpty()) {
            throw new RuntimeException("Program stack empty");
        }
        Program program = this.stack.pollLast();
        return program;
    }
}

