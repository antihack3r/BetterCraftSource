/*
 * Decompiled with CFR 0.152.
 */
package com.jagrosh.discordipc.mod.nzxter;

import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.jagrosh.discordipc.mod.nzxter.IPCListener;
import com.jagrosh.discordipc.mod.nzxter.entities.Callback;
import com.jagrosh.discordipc.mod.nzxter.entities.DiscordBuild;
import com.jagrosh.discordipc.mod.nzxter.entities.Packet;
import com.jagrosh.discordipc.mod.nzxter.entities.RichPresence;
import com.jagrosh.discordipc.mod.nzxter.entities.User;
import com.jagrosh.discordipc.mod.nzxter.entities.pipe.Pipe;
import com.jagrosh.discordipc.mod.nzxter.entities.pipe.PipeStatus;
import com.jagrosh.discordipc.mod.nzxter.exceptions.NoDiscordClientException;
import com.jagrosh.discordipc.mod.nzxter.impl.Backoff;
import java.io.Closeable;
import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.util.HashMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class IPCClient
implements Closeable {
    private static final Logger LOGGER = LoggerFactory.getLogger(IPCClient.class);
    private final Backoff RECONNECT_TIME_MS = new Backoff(500L, 60000L);
    private final long clientId;
    private final boolean autoRegister;
    private final HashMap<String, Callback> callbacks = new HashMap();
    private final String applicationId;
    private final String optionalSteamId;
    private volatile Pipe pipe;
    private Logger forcedLogger = null;
    private IPCListener listener = null;
    private Thread readThread = null;
    private String encoding = "UTF-8";
    private long nextDelay = 0L;
    private boolean debugMode;
    private boolean verboseLogging;

    public IPCClient(long clientId, boolean debugMode, boolean verboseLogging, boolean autoRegister, String applicationId, String optionalSteamId) {
        this.clientId = clientId;
        this.debugMode = debugMode;
        this.verboseLogging = verboseLogging;
        this.applicationId = applicationId;
        this.autoRegister = autoRegister;
        this.optionalSteamId = optionalSteamId;
    }

    public IPCClient(long clientId, boolean debugMode, boolean verboseLogging, boolean autoRegister, String applicationId) {
        this(clientId, debugMode, verboseLogging, autoRegister, applicationId, null);
    }

    public IPCClient(long clientId, boolean debugMode, boolean verboseLogging) {
        this(clientId, debugMode, verboseLogging, false, null);
    }

    public IPCClient(long clientId, boolean debugMode, boolean autoRegister, String applicationId, String optionalSteamId) {
        this(clientId, debugMode, false, autoRegister, applicationId, optionalSteamId);
    }

    public IPCClient(long clientId, boolean debugMode, boolean autoRegister, String applicationId) {
        this(clientId, debugMode, autoRegister, applicationId, null);
    }

    public IPCClient(long clientId, boolean debugMode) {
        this(clientId, debugMode, false, null);
    }

    public IPCClient(long clientId, boolean autoRegister, String applicationId, String optionalSteamId) {
        this(clientId, false, autoRegister, applicationId, optionalSteamId);
    }

    public IPCClient(long clientId, boolean autoRegister, String applicationId) {
        this(clientId, autoRegister, applicationId, null);
    }

    public IPCClient(long clientId) {
        this(clientId, false, null);
    }

    private static int getPID() {
        String pr2 = ManagementFactory.getRuntimeMXBean().getName();
        return Integer.parseInt(pr2.substring(0, pr2.indexOf(64)));
    }

    public Logger getCurrentLogger(Logger instance) {
        return this.forcedLogger != null ? this.forcedLogger : instance;
    }

    public void setForcedLogger(Logger forcedLogger) {
        this.forcedLogger = forcedLogger;
    }

    public void setListener(IPCListener listener) {
        this.listener = listener;
        if (this.pipe != null) {
            this.pipe.setListener(listener);
        }
    }

    public String getApplicationId() {
        return this.applicationId;
    }

    public String getOptionalSteamId() {
        return this.optionalSteamId;
    }

    public boolean isAutoRegister() {
        return this.autoRegister;
    }

    public String getEncoding() {
        return this.encoding;
    }

    public void setEncoding(String encoding) {
        this.encoding = encoding;
    }

    public long getClientID() {
        return this.clientId;
    }

    public boolean isDebugMode() {
        return this.debugMode;
    }

    public void setDebugMode(boolean debugMode) {
        this.debugMode = debugMode;
    }

    public boolean isVerboseLogging() {
        return this.verboseLogging;
    }

    public void setVerboseLogging(boolean verboseLogging) {
        this.verboseLogging = verboseLogging;
    }

    public void connect(DiscordBuild ... preferredOrder) throws NoDiscordClientException, InterruptedException {
        if (!this.isConnected()) {
            long timeToConnect;
            while ((timeToConnect = this.nextDelay - System.currentTimeMillis()) > 0L) {
                if (this.debugMode) {
                    this.getCurrentLogger(LOGGER).info("[DEBUG] Attempting connection in: " + timeToConnect + "ms");
                }
                Thread.sleep(timeToConnect);
            }
            this.callbacks.clear();
            this.pipe = null;
            try {
                this.pipe = Pipe.openPipe(this, this.clientId, this.callbacks, preferredOrder);
            }
            catch (Exception ex2) {
                this.updateReconnectTime();
            }
            if (this.debugMode) {
                this.getCurrentLogger(LOGGER).info("[DEBUG] Client is now connected and ready!");
            }
            if (this.listener != null) {
                this.listener.onReady(this);
                this.pipe.setListener(this.listener);
            }
            this.startReading();
        }
    }

    public void sendRichPresence(RichPresence presence) {
        this.sendRichPresence(presence, null);
    }

    public void sendRichPresence(RichPresence presence, Callback callback) {
        if (this.isConnected()) {
            if (this.debugMode) {
                this.getCurrentLogger(LOGGER).info("[DEBUG] Sending RichPresence to discord: " + (presence == null ? null : presence.toDecodedJson(this.encoding)));
            }
            JsonObject finalObject = new JsonObject();
            JsonObject args = new JsonObject();
            finalObject.addProperty("cmd", "SET_ACTIVITY");
            args.addProperty("pid", IPCClient.getPID());
            args.add("activity", presence == null ? new JsonObject() : presence.toJson());
            finalObject.add("args", args);
            this.pipe.send(Packet.OpCode.FRAME, finalObject, callback);
        }
    }

    public void subscribe(Event sub) {
        this.subscribe(sub, null);
    }

    public void subscribe(Event sub, Callback callback) {
        if (this.isConnected()) {
            if (!sub.isSubscribable()) {
                throw new IllegalStateException("Cannot subscribe to " + (Object)((Object)sub) + " event!");
            }
            if (this.debugMode) {
                this.getCurrentLogger(LOGGER).info(String.format("[DEBUG] Subscribing to Event: %s", sub.name()));
            }
            JsonObject pipeData = new JsonObject();
            pipeData.addProperty("cmd", "SUBSCRIBE");
            pipeData.addProperty("evt", sub.name());
            this.pipe.send(Packet.OpCode.FRAME, pipeData, callback);
        }
    }

    public void respondToJoinRequest(User user, ApprovalMode approvalMode, Callback callback) {
        if (this.isConnected() && user != null) {
            if (this.debugMode) {
                this.getCurrentLogger(LOGGER).info(String.format("[DEBUG] Sending response to %s as %s", user.getName(), approvalMode.name()));
            }
            JsonObject pipeData = new JsonObject();
            pipeData.addProperty("cmd", approvalMode == ApprovalMode.ACCEPT ? "SEND_ACTIVITY_JOIN_INVITE" : "CLOSE_ACTIVITY_JOIN_REQUEST");
            JsonObject args = new JsonObject();
            args.addProperty("user_id", user.getId());
            pipeData.add("args", args);
            this.pipe.send(Packet.OpCode.FRAME, pipeData, callback);
        }
    }

    public void respondToJoinRequest(User user, ApprovalMode approvalMode) {
        this.respondToJoinRequest(user, approvalMode, null);
    }

    public PipeStatus getStatus() {
        if (this.pipe == null) {
            return PipeStatus.UNINITIALIZED;
        }
        return this.pipe.getStatus();
    }

    @Override
    public void close() {
        block3: {
            if (this.isConnected()) {
                try {
                    this.pipe.close();
                }
                catch (IOException e2) {
                    if (!this.debugMode) break block3;
                    this.getCurrentLogger(LOGGER).info(String.format("[DEBUG] Failed to close pipe: %s", e2));
                }
            }
        }
    }

    public DiscordBuild getDiscordBuild() {
        if (this.pipe == null) {
            return null;
        }
        return this.pipe.getDiscordBuild();
    }

    public User getCurrentUser() {
        if (this.pipe == null) {
            return null;
        }
        return this.pipe.getCurrentUser();
    }

    public boolean isConnected() {
        return this.getStatus() == PipeStatus.CONNECTED;
    }

    private void startReading() {
        final IPCClient localInstance = this;
        this.readThread = new Thread(new Runnable(){

            @Override
            public void run() {
                IPCClient.this.readPipe(localInstance);
            }
        }, "IPCClient-Reader");
        this.readThread.setDaemon(true);
        if (this.debugMode) {
            this.getCurrentLogger(LOGGER).info("[DEBUG] Starting IPCClient reading thread!");
        }
        this.readThread.start();
    }

    /*
     * Unable to fully structure code
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    private void readPipe(IPCClient instance) {
        try {
            while (true) lbl-1000:
            // 5 sources

            {
                if ((p = this.pipe.read()).getOp() == Packet.OpCode.CLOSE) {
                    this.pipe.setStatus(PipeStatus.DISCONNECTED);
                    if (this.listener == null) return;
                    this.listener.onClose(instance, p.getJson());
                    return;
                }
                json = p.getJson();
                if (json == null) continue;
                event = Event.of(json.has("evt") != false && json.get("evt").isJsonNull() == false ? json.getAsJsonPrimitive("evt").getAsString() : null);
                nonce = json.has("nonce") != false && json.get("nonce").isJsonNull() == false ? json.getAsJsonPrimitive("nonce").getAsString() : null;
                switch (_2.$SwitchMap$com$jagrosh$discordipc$mod$nzxter$IPCClient$Event[event.ordinal()]) {
                    case 1: {
                        if (nonce == null || !this.callbacks.containsKey(nonce)) break;
                        this.callbacks.remove(nonce).succeed(p);
                        break;
                    }
                    case 2: {
                        if (nonce == null || !this.callbacks.containsKey(nonce)) break;
                        this.callbacks.remove(nonce).fail(json.has("data") != false && json.getAsJsonObject("data").has("message") != false ? json.getAsJsonObject("data").getAsJsonObject("message").getAsString() : null);
                        break;
                    }
                    case 3: {
                        if (!this.debugMode) break;
                        this.getCurrentLogger(IPCClient.LOGGER).info("[DEBUG] Reading thread received a 'join' event.");
                        break;
                    }
                    case 4: {
                        if (!this.debugMode) break;
                        this.getCurrentLogger(IPCClient.LOGGER).info("[DEBUG] Reading thread received a 'spectate' event.");
                        break;
                    }
                    case 5: {
                        if (!this.debugMode) break;
                        this.getCurrentLogger(IPCClient.LOGGER).info("[DEBUG] Reading thread received a 'join request' event.");
                        break;
                    }
                    case 6: {
                        if (!this.debugMode) break;
                        this.getCurrentLogger(IPCClient.LOGGER).info("[DEBUG] Reading thread encountered an event with an unknown type: " + json.getAsJsonPrimitive("evt").getAsString());
                        break;
                    }
                }
                if (this.listener == null || !json.has("cmd") || !json.getAsJsonPrimitive("cmd").getAsString().equals("DISPATCH")) continue;
                try {
                    data = json.getAsJsonObject("data");
                    switch (_2.$SwitchMap$com$jagrosh$discordipc$mod$nzxter$IPCClient$Event[Event.of(json.getAsJsonPrimitive("evt").getAsString()).ordinal()]) {
                        case 3: {
                            this.listener.onActivityJoin(instance, data.getAsJsonPrimitive("secret").getAsString());
                            break;
                        }
                        case 4: {
                            this.listener.onActivitySpectate(instance, data.getAsJsonPrimitive("secret").getAsString());
                            break;
                        }
                        case 5: {
                            u = data.getAsJsonObject("user");
                            user = new User(u.getAsJsonPrimitive("username").getAsString(), u.has("global_name") != false && u.get("global_name").isJsonPrimitive() != false ? u.getAsJsonPrimitive("global_name").getAsString() : null, u.has("discriminator") != false && u.get("discriminator").isJsonPrimitive() != false ? u.getAsJsonPrimitive("discriminator").getAsString() : "0", Long.parseLong(u.getAsJsonPrimitive("id").getAsString()), u.has("avatar") != false && u.get("avatar").isJsonPrimitive() != false ? u.getAsJsonPrimitive("avatar").getAsString() : null);
                            this.listener.onActivityJoinRequest(instance, data.has("secret") != false ? data.getAsJsonObject("secret").getAsString() : null, user);
                            break;
                        }
                    }
                }
                catch (Exception e) {
                    this.getCurrentLogger(IPCClient.LOGGER).error(String.format("Exception when handling event: %s", new Object[]{e}));
                    continue;
                }
                break;
            }
        }
        catch (JsonParseException | IOException ex) {
            if (this.debugMode) {
                this.getCurrentLogger(IPCClient.LOGGER).error(String.format("Reading thread encountered an Exception: %s", new Object[]{ex}));
            }
            this.pipe.setStatus(PipeStatus.DISCONNECTED);
            if (this.listener == null) return;
            this.RECONNECT_TIME_MS.reset();
            this.updateReconnectTime();
            this.listener.onDisconnect(instance, ex);
        }
        ** GOTO lbl-1000
    }

    private void updateReconnectTime() {
        this.nextDelay = System.currentTimeMillis() + this.RECONNECT_TIME_MS.nextDelay();
    }

    public static enum Event {
        NULL(false),
        READY(false),
        ERROR(false),
        ACTIVITY_JOIN(true),
        ACTIVITY_SPECTATE(true),
        ACTIVITY_JOIN_REQUEST(true),
        UNKNOWN(false);

        private final boolean subscribable;

        private Event(boolean subscribable) {
            this.subscribable = subscribable;
        }

        static Event of(String str) {
            if (str == null) {
                return NULL;
            }
            for (Event s2 : Event.values()) {
                if (s2 == UNKNOWN || !s2.name().equalsIgnoreCase(str)) continue;
                return s2;
            }
            return UNKNOWN;
        }

        public boolean isSubscribable() {
            return this.subscribable;
        }
    }

    public static enum ApprovalMode {
        ACCEPT,
        DENY;

    }
}

