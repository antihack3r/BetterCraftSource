/*
 * Decompiled with CFR 0.152.
 */
package com.jagrosh.discordipc.entities.pipe;

import com.jagrosh.discordipc.IPCClient;
import com.jagrosh.discordipc.entities.Callback;
import com.jagrosh.discordipc.entities.Packet;
import com.jagrosh.discordipc.entities.pipe.Pipe;
import com.jagrosh.discordipc.entities.pipe.PipeStatus;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.HashMap;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class WindowsPipe
extends Pipe {
    private static final Logger LOGGER = LoggerFactory.getLogger(WindowsPipe.class);
    private final RandomAccessFile file;

    WindowsPipe(IPCClient ipcClient, HashMap<String, Callback> callbacks, String location) {
        super(ipcClient, callbacks);
        try {
            this.file = new RandomAccessFile(location, "rw");
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
    public Packet read() throws IOException, JSONException {
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
        Packet p2 = new Packet(op2, new JSONObject(new String(d2)));
        LOGGER.debug(String.format("Received packet: %s", p2.toString()));
        if (this.listener != null) {
            this.listener.onPacketReceived(this.ipcClient, p2);
        }
        return p2;
    }

    @Override
    public void close() throws IOException {
        LOGGER.debug("Closing IPC pipe...");
        this.send(Packet.OpCode.CLOSE, new JSONObject(), null);
        this.status = PipeStatus.CLOSED;
        this.file.close();
    }
}

