/*
 * Decompiled with CFR 0.152.
 */
package com.jagrosh.discordipc.mod.nzxter;

import com.google.gson.JsonObject;
import com.jagrosh.discordipc.mod.nzxter.IPCClient;
import com.jagrosh.discordipc.mod.nzxter.entities.Packet;
import com.jagrosh.discordipc.mod.nzxter.entities.User;

public interface IPCListener {
    default public void onPacketSent(IPCClient client, Packet packet) {
    }

    default public void onPacketReceived(IPCClient client, Packet packet) {
    }

    default public void onActivityJoin(IPCClient client, String secret) {
    }

    default public void onActivitySpectate(IPCClient client, String secret) {
    }

    default public void onActivityJoinRequest(IPCClient client, String secret, User user) {
    }

    default public void onReady(IPCClient client) {
    }

    default public void onClose(IPCClient client, JsonObject json) {
    }

    default public void onDisconnect(IPCClient client, Throwable t2) {
    }
}

