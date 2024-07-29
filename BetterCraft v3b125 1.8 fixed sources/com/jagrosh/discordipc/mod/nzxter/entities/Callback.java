/*
 * Decompiled with CFR 0.152.
 */
package com.jagrosh.discordipc.mod.nzxter.entities;

import com.jagrosh.discordipc.mod.nzxter.entities.Packet;
import com.jagrosh.discordipc.mod.nzxter.impl.DataConsumer;

public class Callback {
    private final DataConsumer<Packet> success;
    private final DataConsumer<String> failure;

    public Callback() {
        this(null, null);
    }

    public Callback(DataConsumer<Packet> success) {
        this(success, null);
    }

    public Callback(DataConsumer<Packet> success, DataConsumer<String> failure) {
        this.success = success;
        this.failure = failure;
    }

    public boolean isEmpty() {
        return this.success == null && this.failure == null;
    }

    public void succeed(Packet packet) {
        if (this.success != null) {
            this.success.accept(packet);
        }
    }

    public void fail(String message) {
        if (this.failure != null) {
            this.failure.accept(message);
        }
    }
}

