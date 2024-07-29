/*
 * Decompiled with CFR 0.152.
 */
package com.jagrosh.discordipc.mod.nzxter.entities.pipe;

import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;
import com.jagrosh.discordipc.mod.nzxter.IPCClient;
import com.jagrosh.discordipc.mod.nzxter.IPCListener;
import com.jagrosh.discordipc.mod.nzxter.entities.Callback;
import com.jagrosh.discordipc.mod.nzxter.entities.DiscordBuild;
import com.jagrosh.discordipc.mod.nzxter.entities.Packet;
import com.jagrosh.discordipc.mod.nzxter.entities.User;
import com.jagrosh.discordipc.mod.nzxter.entities.pipe.MacPipe;
import com.jagrosh.discordipc.mod.nzxter.entities.pipe.PipeStatus;
import com.jagrosh.discordipc.mod.nzxter.entities.pipe.UnixPipe;
import com.jagrosh.discordipc.mod.nzxter.entities.pipe.WindowsPipe;
import com.jagrosh.discordipc.mod.nzxter.exceptions.NoDiscordClientException;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class Pipe {
    private static final Logger LOGGER = LoggerFactory.getLogger(Pipe.class);
    private static final int VERSION = 1;
    private static final String[] unixPaths = new String[]{"XDG_RUNTIME_DIR", "TMPDIR", "TMP", "TEMP"};
    private static final String[] unixFolderPaths = new String[]{"/snap.discord", "/app/com.discordapp.Discord"};
    final IPCClient ipcClient;
    private final HashMap<String, Callback> callbacks;
    PipeStatus status = PipeStatus.CONNECTING;
    IPCListener listener;
    private DiscordBuild build;
    private User currentUser;

    Pipe(IPCClient ipcClient, HashMap<String, Callback> callbacks) {
        this.ipcClient = ipcClient;
        this.callbacks = callbacks;
    }

    public static Pipe openPipe(IPCClient ipcClient, long clientId, HashMap<String, Callback> callbacks, DiscordBuild ... preferredOrder) throws NoDiscordClientException {
        int i2;
        if (preferredOrder == null || preferredOrder.length == 0) {
            preferredOrder = new DiscordBuild[]{DiscordBuild.ANY};
        }
        Pipe pipe = null;
        Pipe[] open = new Pipe[DiscordBuild.values().length];
        for (i2 = 0; i2 < 10; ++i2) {
            String location = Pipe.getPipeLocation(i2);
            if (ipcClient.isDebugMode()) {
                ipcClient.getCurrentLogger(LOGGER).info(String.format("[DEBUG] Searching for IPC Pipe: \"%s\"", location));
            }
            try {
                File fileLocation = new File(location);
                if (fileLocation.exists()) {
                    if (ipcClient.isDebugMode()) {
                        ipcClient.getCurrentLogger(LOGGER).info(String.format("[DEBUG] Found valid file, attempting connection to IPC: \"%s\"", location));
                    }
                    if ((pipe = Pipe.createPipe(ipcClient, callbacks, fileLocation)) == null) continue;
                    JsonObject finalObject = new JsonObject();
                    finalObject.addProperty("v", 1);
                    finalObject.addProperty("client_id", Long.toString(clientId));
                    pipe.send(Packet.OpCode.HANDSHAKE, finalObject);
                    Packet p2 = pipe.read();
                    JsonObject parsedData = p2.getJson();
                    JsonObject data = parsedData.getAsJsonObject("data");
                    JsonObject userData = data.getAsJsonObject("user");
                    pipe.build = DiscordBuild.from(data.getAsJsonObject("config").get("api_endpoint").getAsString());
                    pipe.currentUser = new User(userData.getAsJsonPrimitive("username").getAsString(), userData.has("global_name") && userData.get("global_name").isJsonPrimitive() ? userData.getAsJsonPrimitive("global_name").getAsString() : null, userData.has("discriminator") && userData.get("discriminator").isJsonPrimitive() ? userData.getAsJsonPrimitive("discriminator").getAsString() : "0", Long.parseLong(userData.getAsJsonPrimitive("id").getAsString()), userData.has("avatar") && userData.get("avatar").isJsonPrimitive() ? userData.getAsJsonPrimitive("avatar").getAsString() : null);
                    if (ipcClient.isDebugMode()) {
                        ipcClient.getCurrentLogger(LOGGER).info(String.format("[DEBUG] Found a valid client (%s) with packet: %s", pipe.build.name(), p2));
                        ipcClient.getCurrentLogger(LOGGER).info(String.format("[DEBUG] Found a valid user (%s) with id: %s", pipe.currentUser.getName(), pipe.currentUser.getId()));
                    }
                    if (pipe.build == preferredOrder[0] || DiscordBuild.ANY == preferredOrder[0]) {
                        if (!ipcClient.isDebugMode()) break;
                        ipcClient.getCurrentLogger(LOGGER).info(String.format("[DEBUG] Found preferred client: %s", pipe.build.name()));
                        break;
                    }
                    open[pipe.build.ordinal()] = pipe;
                    open[DiscordBuild.ANY.ordinal()] = pipe;
                    pipe.build = null;
                    pipe = null;
                    continue;
                }
                if (!ipcClient.isDebugMode()) continue;
                ipcClient.getCurrentLogger(LOGGER).info(String.format("[DEBUG] Unable to locate IPC Pipe: \"%s\"", location));
                continue;
            }
            catch (JsonParseException | IOException ex2) {
                pipe = null;
            }
        }
        if (pipe == null) {
            for (i2 = 1; i2 < preferredOrder.length; ++i2) {
                DiscordBuild cb2 = preferredOrder[i2];
                if (ipcClient.isDebugMode()) {
                    ipcClient.getCurrentLogger(LOGGER).info(String.format("[DEBUG] Looking for client build: %s", cb2.name()));
                }
                if (open[cb2.ordinal()] == null) continue;
                pipe = open[cb2.ordinal()];
                open[cb2.ordinal()] = null;
                if (cb2 == DiscordBuild.ANY) {
                    for (int k2 = 0; k2 < open.length; ++k2) {
                        if (open[k2] != pipe) continue;
                        pipe.build = DiscordBuild.values()[k2];
                        open[k2] = null;
                    }
                } else {
                    pipe.build = cb2;
                }
                if (!ipcClient.isDebugMode()) break;
                ipcClient.getCurrentLogger(LOGGER).info(String.format("[DEBUG] Found preferred client: %s", pipe.build.name()));
                break;
            }
            if (pipe == null) {
                throw new NoDiscordClientException();
            }
        }
        for (i2 = 0; i2 < open.length; ++i2) {
            if (i2 == DiscordBuild.ANY.ordinal() || open[i2] == null) continue;
            try {
                open[i2].close();
                continue;
            }
            catch (IOException ex3) {
                if (!ipcClient.isDebugMode()) continue;
                ipcClient.getCurrentLogger(LOGGER).info(String.format("[DEBUG] Failed to close an open IPC pipe: %s", ex3));
            }
        }
        pipe.status = PipeStatus.CONNECTED;
        return pipe;
    }

    private static Pipe createPipe(IPCClient ipcClient, HashMap<String, Callback> callbacks, File fileLocation) {
        String osName = System.getProperty("os.name").toLowerCase();
        if (osName.contains("win")) {
            return new WindowsPipe(ipcClient, callbacks, fileLocation);
        }
        if (osName.contains("linux") || osName.contains("mac")) {
            try {
                return osName.contains("mac") ? new MacPipe(ipcClient, callbacks, fileLocation) : new UnixPipe(ipcClient, callbacks, fileLocation);
            }
            catch (IOException e2) {
                throw new RuntimeException(e2);
            }
        }
        throw new RuntimeException("Unsupported OS: " + osName);
    }

    private static String generateNonce() {
        return UUID.randomUUID().toString();
    }

    private static String getPipeLocation(int index) {
        String str2;
        String tmpPath = null;
        String pipePath = "discord-ipc-" + index;
        if (System.getProperty("os.name").contains("Win")) {
            return "\\\\?\\pipe\\" + pipePath;
        }
        String[] stringArray = unixPaths;
        int n2 = stringArray.length;
        for (int i2 = 0; i2 < n2 && (tmpPath = System.getenv(str2 = stringArray[i2])) == null; ++i2) {
        }
        if (tmpPath == null) {
            tmpPath = "/tmp";
        }
        for (String str2 : unixFolderPaths) {
            String folderPath = tmpPath + str2;
            File folderFile = new File(folderPath);
            if (!folderFile.exists() || !folderFile.isDirectory() || folderFile.list().length <= 0) continue;
            tmpPath = folderPath;
            break;
        }
        return tmpPath + "/" + pipePath;
    }

    public void send(Packet.OpCode op2, JsonObject data, Callback callback) {
        try {
            String nonce = Pipe.generateNonce();
            data.addProperty("nonce", nonce);
            Packet p2 = new Packet(op2, data, this.ipcClient.getEncoding());
            if (callback != null && !callback.isEmpty()) {
                this.callbacks.put(nonce, callback);
            }
            this.write(p2.toBytes());
            if (this.ipcClient.isDebugMode()) {
                this.ipcClient.getCurrentLogger(LOGGER).info(String.format("[DEBUG] Sent packet: %s", p2.toDecodedString()));
            }
            if (this.listener != null) {
                this.listener.onPacketSent(this.ipcClient, p2);
            }
        }
        catch (IOException ex2) {
            if (this.ipcClient.isDebugMode()) {
                this.ipcClient.getCurrentLogger(LOGGER).error("Encountered an IOException while sending a packet and disconnected!" + ex2);
            }
            this.status = PipeStatus.DISCONNECTED;
        }
    }

    public void send(Packet.OpCode op2, JsonObject data) {
        this.send(op2, data, null);
    }

    public Packet receive(Packet.OpCode op2, byte[] data) {
        JsonObject packetData = new JsonParser().parse(new String(data)).getAsJsonObject();
        Packet p2 = new Packet(op2, packetData, this.ipcClient.getEncoding());
        if (this.ipcClient.isDebugMode()) {
            this.ipcClient.getCurrentLogger(LOGGER).info(String.format("[DEBUG] Received packet: %s", p2));
        }
        if (this.listener != null) {
            this.listener.onPacketReceived(this.ipcClient, p2);
        }
        return p2;
    }

    public abstract Packet read() throws IOException, JsonParseException;

    public abstract void write(byte[] var1) throws IOException;

    public PipeStatus getStatus() {
        return this.status;
    }

    public void setStatus(PipeStatus status) {
        this.status = status;
    }

    public void setListener(IPCListener listener) {
        this.listener = listener;
    }

    public abstract void close() throws IOException;

    public DiscordBuild getDiscordBuild() {
        return this.build;
    }

    public User getCurrentUser() {
        return this.currentUser;
    }
}

