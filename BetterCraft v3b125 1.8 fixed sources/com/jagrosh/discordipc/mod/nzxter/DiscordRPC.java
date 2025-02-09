/*
 * Decompiled with CFR 0.152.
 */
package com.jagrosh.discordipc.mod.nzxter;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.jagrosh.discordipc.mod.nzxter.IPCClient;
import com.jagrosh.discordipc.mod.nzxter.entities.DiscordBuild;
import com.jagrosh.discordipc.mod.nzxter.entities.RichPresence;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class DiscordRPC {
    private static DiscordRPC instance = new DiscordRPC();
    private RichPresence richPresence = new RichPresence();
    private IPCClient ipcClient = null;
    private ExecutorService executor;

    public static synchronized DiscordRPC getInstance() {
        return instance;
    }

    public synchronized RichPresence getPresence() {
        return this.richPresence;
    }

    public void bootDiscordRPC(long clientId, String details, String state, String largeKey, String largeText, String smallKey, String smallText, String ... labelsAndUrls) {
        if (this.ipcClient == null || !this.ipcClient.isConnected()) {
            this.initializeClient(clientId);
        } else {
            this.ipcClient.close();
            this.initializeClient(clientId);
        }
        this.updateRichPresence(details, state, largeKey, largeText, smallKey, smallText, labelsAndUrls);
        this.startCallbackHandler();
    }

    private void initializeClient(long clientId) {
        try {
            this.ipcClient = new IPCClient(clientId);
            this.ipcClient.connect(new DiscordBuild[0]);
        }
        catch (Exception exception) {
            // empty catch block
        }
    }

    private void updateRichPresence(String details, String state, String largeKey, String largeText, String smallKey, String smallText, String ... labelsAndUrls) {
        if (labelsAndUrls == null || labelsAndUrls.length == 0) {
            labelsAndUrls = new String[]{};
        }
        if (labelsAndUrls.length % 2 != 0) {
            throw new IllegalArgumentException("Labels and URLs must be in pairs.");
        }
        if (labelsAndUrls.length / 2 > 2) {
            throw new IllegalArgumentException("Number of labels and URLs cannot exceed 2.");
        }
        String[] labels = new String[labelsAndUrls.length / 2];
        String[] urls = new String[labelsAndUrls.length / 2];
        for (int i2 = 0; i2 < labels.length; ++i2) {
            labels[i2] = labelsAndUrls[i2 * 2];
            urls[i2] = labelsAndUrls[i2 * 2 + 1];
        }
        JsonArray buttons = new JsonArray();
        for (int i3 = 0; i3 < labels.length; ++i3) {
            buttons.add(this.createButton(labels[i3], urls[i3]));
        }
        this.richPresence.setStartTimestamp(System.currentTimeMillis() / 1000L);
        this.richPresence.setDetails(details);
        this.richPresence.setState(state);
        this.richPresence.setLargeImageKey(smallKey);
        this.richPresence.setLargeImageText(largeText);
        this.richPresence.setSmallImageKey(smallKey);
        this.richPresence.setSmallImageText(smallText);
        this.richPresence.setButtons(buttons);
        this.ipcClient.sendRichPresence(this.richPresence);
    }

    public void updateRichPresenceText(String details, String state) {
        this.richPresence.setDetails(details);
        this.richPresence.setState(state);
        this.ipcClient.sendRichPresence(this.richPresence);
    }

    private JsonObject createButton(String label, String url) {
        JsonObject button = new JsonObject();
        button.addProperty("label", label);
        button.addProperty("url", url);
        return button;
    }

    public void shutdownDiscordRPC() {
        if (this.ipcClient != null && this.ipcClient.isConnected()) {
            this.ipcClient.close();
        }
    }

    private void startCallbackHandler() {
        this.executor = Executors.newSingleThreadExecutor(runnable -> new Thread(runnable, "RPC-Callback-Handler"));
        this.executor.submit(() -> {
            while (!Thread.currentThread().isInterrupted()) {
                try {
                    Thread.sleep(2000L);
                }
                catch (InterruptedException e2) {
                    Thread.currentThread().interrupt();
                }
            }
        });
    }
}

