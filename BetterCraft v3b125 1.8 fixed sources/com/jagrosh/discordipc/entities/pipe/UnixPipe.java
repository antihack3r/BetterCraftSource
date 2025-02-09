/*
 * Decompiled with CFR 0.152.
 */
package com.jagrosh.discordipc.entities.pipe;

import com.jagrosh.discordipc.IPCClient;
import com.jagrosh.discordipc.entities.Callback;
import com.jagrosh.discordipc.entities.Packet;
import com.jagrosh.discordipc.entities.pipe.Pipe;
import com.jagrosh.discordipc.entities.pipe.PipeStatus;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.HashMap;
import org.json.JSONException;
import org.json.JSONObject;
import org.newsclub.net.unix.AFUNIXSocket;
import org.newsclub.net.unix.AFUNIXSocketAddress;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UnixPipe
extends Pipe {
    private static final Logger LOGGER = LoggerFactory.getLogger(UnixPipe.class);
    private final AFUNIXSocket socket = AFUNIXSocket.newInstance();

    UnixPipe(IPCClient ipcClient, HashMap<String, Callback> callbacks, String location) throws IOException {
        super(ipcClient, callbacks);
        this.socket.connect(new AFUNIXSocketAddress(new File(location)));
    }

    @Override
    public Packet read() throws IOException, JSONException {
        InputStream is2 = this.socket.getInputStream();
        while (is2.available() == 0 && this.status == PipeStatus.CONNECTED) {
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
        byte[] d2 = new byte[8];
        is2.read(d2);
        ByteBuffer bb2 = ByteBuffer.wrap(d2);
        Packet.OpCode op2 = Packet.OpCode.values()[Integer.reverseBytes(bb2.getInt())];
        d2 = new byte[Integer.reverseBytes(bb2.getInt())];
        is2.read(d2);
        Packet p2 = new Packet(op2, new JSONObject(new String(d2)));
        LOGGER.debug(String.format("Received packet: %s", p2.toString()));
        if (this.listener != null) {
            this.listener.onPacketReceived(this.ipcClient, p2);
        }
        return p2;
    }

    @Override
    public void write(byte[] b2) throws IOException {
        this.socket.getOutputStream().write(b2);
    }

    @Override
    public void close() throws IOException {
        LOGGER.debug("Closing IPC pipe...");
        this.send(Packet.OpCode.CLOSE, new JSONObject(), null);
        this.status = PipeStatus.CLOSED;
        this.socket.close();
    }
}

