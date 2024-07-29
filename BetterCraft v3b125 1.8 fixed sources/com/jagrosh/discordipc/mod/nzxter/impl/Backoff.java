/*
 * Decompiled with CFR 0.152.
 */
package com.jagrosh.discordipc.mod.nzxter.impl;

import java.util.Random;

public class Backoff {
    private final long minAmount;
    private final long maxAmount;
    private long current;
    private int fails;
    private final Random randGenerator;

    public Backoff(long min, long max) {
        this.minAmount = min;
        this.maxAmount = max;
        this.current = min;
        this.setFails(0);
        this.randGenerator = new Random();
    }

    public void reset() {
        this.setFails(0);
        this.current = this.minAmount;
    }

    public long nextDelay() {
        this.setFails(this.getFails() + 1);
        double delay = (double)this.current * 2.0 * this.rand01();
        this.current = Math.min(this.current + (long)delay, this.maxAmount);
        return this.current;
    }

    private double rand01() {
        return this.randGenerator.nextDouble();
    }

    public int getFails() {
        return this.fails;
    }

    public void setFails(int fails) {
        this.fails = fails;
    }
}

