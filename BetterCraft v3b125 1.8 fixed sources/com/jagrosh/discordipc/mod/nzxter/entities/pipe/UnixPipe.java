/*
 * Decompiled with CFR 0.152.
 */
package com.jagrosh.discordipc.mod.nzxter.entities.pipe;

import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.jagrosh.discordipc.mod.nzxter.IPCClient;
import com.jagrosh.discordipc.mod.nzxter.entities.Callback;
import com.jagrosh.discordipc.mod.nzxter.entities.Packet;
import com.jagrosh.discordipc.mod.nzxter.entities.pipe.Pipe;
import com.jagrosh.discordipc.mod.nzxter.entities.pipe.PipeStatus;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.HashMap;
import org.newsclub.net.unix.AFUNIXSocket;
import org.newsclub.net.unix.AFUNIXSocketAddress;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UnixPipe
extends Pipe {
    private static final Logger LOGGER = LoggerFactory.getLogger(UnixPipe.class);
    private final AFUNIXSocket socket = AFUNIXSocket.newInstance();

    public UnixPipe(IPCClient ipcClient, HashMap<String, Callback> callbacks, File fileLocation) throws IOException {
        super(ipcClient, callbacks);
        AFUNIXSocketAddress socketAddress = new AFUNIXSocketAddress(fileLocation);
        this.socket.connect(socketAddress);
    }

    @Override
    public Packet read() throws IOException, JsonParseException {
        InputStream is2 = this.socket.getInputStream();
        while ((this.status == PipeStatus.CONNECTED || this.status == PipeStatus.CLOSING) && is2.available() == 0) {
            try {
                Thread.sleep(50L);
            }
            catch (InterruptedException interruptedException) {}
        }
        if (this.status == PipeStatus.DISCONNECTED) {
            throw new IOException("Disconnected!");
        }
        if (this.status == PipeStatus.CLOSED) {
            return new Packet(Packet.OpCode.CLOSE, null, this.ipcClient.getEncoding());
        }
        byte[] d2 = new byte[8];
        int readResult = is2.read(d2);
        ByteBuffer bb2 = ByteBuffer.wrap(d2);
        if (this.ipcClient.isDebugMode() && this.ipcClient.isVerboseLogging()) {
            this.ipcClient.getCurrentLogger(LOGGER).info(String.format("[DEBUG] Read Byte Data: %s with result %s", new String(d2), readResult));
        }
        Packet.OpCode op2 = Packet.OpCode.values()[Integer.reverseBytes(bb2.getInt())];
        d2 = new byte[Integer.reverseBytes(bb2.getInt())];
        int reversedResult = is2.read(d2);
        if (this.ipcClient.isDebugMode() && this.ipcClient.isVerboseLogging()) {
            this.ipcClient.getCurrentLogger(LOGGER).info(String.format("[DEBUG] Read Reversed Byte Data: %s with result %s", new String(d2), reversedResult));
        }
        return this.receive(op2, d2);
    }

    @Override
    public void write(byte[] b2) throws IOException {
        this.socket.getOutputStream().write(b2);
    }

    @Override
    public void close() throws IOException {
        if (this.ipcClient.isDebugMode()) {
            this.ipcClient.getCurrentLogger(LOGGER).info("[DEBUG] Closing IPC pipe...");
        }
        this.status = PipeStatus.CLOSING;
        this.send(Packet.OpCode.CLOSE, new JsonObject());
        this.status = PipeStatus.CLOSED;
        this.socket.close();
    }

    public boolean mkdir(String path) {
        File file = new File(path);
        return file.exists() && file.isDirectory() || file.mkdir();
    }
}

