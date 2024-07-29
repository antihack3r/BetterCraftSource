/*
 * Decompiled with CFR 0.152.
 */
package com.jagrosh.discordipc.mod.nzxter.entities.pipe;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;
import com.jagrosh.discordipc.mod.nzxter.IPCClient;
import com.jagrosh.discordipc.mod.nzxter.entities.Callback;
import com.jagrosh.discordipc.mod.nzxter.entities.Packet;
import com.jagrosh.discordipc.mod.nzxter.entities.pipe.Pipe;
import com.jagrosh.discordipc.mod.nzxter.entities.pipe.PipeStatus;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.HashMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class WindowsPipe
extends Pipe {
    private static final Logger LOGGER = LoggerFactory.getLogger(WindowsPipe.class);
    private final RandomAccessFile file;

    WindowsPipe(IPCClient ipcClient, HashMap<String, Callback> callbacks, File fileLocation) {
        super(ipcClient, callbacks);
        try {
            this.file = new RandomAccessFile(fileLocation, "rw");
        }
        catch (FileNotFoundException e2) {
            throw new RuntimeException(e2);
        }
    }

    @Override
    public void write(byte[] b2) throws IOException {
        this.file.write(b2);
    }

    @Override
    public Packet read() throws IOException {
        JsonObject jsonObject;
        while (this.file.length() == 0L && this.status == PipeStatus.CONNECTED) {
            try {
                Thread.sleep(50L);
            }
            catch (InterruptedException interruptedException) {}
        }
        if (this.status == PipeStatus.DISCONNECTED) {
            throw new IOException("Disconnected!");
        }
        if (this.status == PipeStatus.CLOSED) {
            return new Packet(Packet.OpCode.CLOSE, null);
        }
        Packet.OpCode op2 = Packet.OpCode.values()[Integer.reverseBytes(this.file.readInt())];
        int len = Integer.reverseBytes(this.file.readInt());
        byte[] d2 = new byte[len];
        this.file.readFully(d2);
        try {
            jsonObject = this.parseJson(new String(d2));
        }
        catch (JsonSyntaxException e2) {
            throw new IOException("Failed to parse JSON data", e2);
        }
        Packet p2 = new Packet(op2, jsonObject);
        LOGGER.debug(String.format("Received packet: %s", p2.toString()));
        if (this.listener != null) {
            this.listener.onPacketReceived(this.ipcClient, p2);
        }
        return p2;
    }

    @Override
    public void close() throws IOException {
        LOGGER.debug("Closing IPC pipe...");
        JsonObject jsonObject = new JsonObject();
        this.send(Packet.OpCode.CLOSE, jsonObject, null);
        this.status = PipeStatus.CLOSED;
        this.file.close();
    }

    private JsonObject parseJson(String jsonString) {
        try {
            return JsonParser.parseString((String)jsonString).getAsJsonObject();
        }
        catch (NoSuchMethodError e2) {
            try {
                JsonParser parser = new JsonParser();
                return parser.parse(jsonString).getAsJsonObject();
            }
            catch (JsonSyntaxException e1) {
                throw new RuntimeException("Failed to parse JSON data", e1);
            }
        }
        catch (JsonSyntaxException e3) {
            throw new RuntimeException("Failed to parse JSON data", e3);
        }
    }
}

