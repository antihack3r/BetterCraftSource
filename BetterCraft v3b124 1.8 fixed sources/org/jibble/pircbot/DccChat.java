/*
 * Decompiled with CFR 0.152.
 */
package org.jibble.pircbot;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import org.jibble.pircbot.PircBot;

public class DccChat {
    private PircBot _bot;
    private String _nick;
    private String _login = null;
    private String _hostname = null;
    private BufferedReader _reader;
    private BufferedWriter _writer;
    private Socket _socket;
    private boolean _acceptable;
    private long _address = 0L;
    private int _port = 0;

    DccChat(PircBot pircBot, String string, String string2, String string3, long l2, int n2) {
        this._bot = pircBot;
        this._address = l2;
        this._port = n2;
        this._nick = string;
        this._login = string2;
        this._hostname = string3;
        this._acceptable = true;
    }

    DccChat(PircBot pircBot, String string, Socket socket) throws IOException {
        this._bot = pircBot;
        this._nick = string;
        this._socket = socket;
        this._reader = new BufferedReader(new InputStreamReader(this._socket.getInputStream()));
        this._writer = new BufferedWriter(new OutputStreamWriter(this._socket.getOutputStream()));
        this._acceptable = false;
    }

    public synchronized void accept() throws IOException {
        if (this._acceptable) {
            this._acceptable = false;
            int[] nArray = this._bot.longToIp(this._address);
            String string = nArray[0] + "." + nArray[1] + "." + nArray[2] + "." + nArray[3];
            this._socket = new Socket(string, this._port);
            this._reader = new BufferedReader(new InputStreamReader(this._socket.getInputStream()));
            this._writer = new BufferedWriter(new OutputStreamWriter(this._socket.getOutputStream()));
        }
    }

    public String readLine() throws IOException {
        if (this._acceptable) {
            throw new IOException("You must call the accept() method of the DccChat request before you can use it.");
        }
        return this._reader.readLine();
    }

    public void sendLine(String string) throws IOException {
        if (this._acceptable) {
            throw new IOException("You must call the accept() method of the DccChat request before you can use it.");
        }
        this._writer.write(string + "\r\n");
        this._writer.flush();
    }

    public void close() throws IOException {
        if (this._acceptable) {
            throw new IOException("You must call the accept() method of the DccChat request before you can use it.");
        }
        this._socket.close();
    }

    public String getNick() {
        return this._nick;
    }

    public String getLogin() {
        return this._login;
    }

    public String getHostname() {
        return this._hostname;
    }

    public BufferedReader getBufferedReader() {
        return this._reader;
    }

    public BufferedWriter getBufferedWriter() {
        return this._writer;
    }

    public Socket getSocket() {
        return this._socket;
    }

    public long getNumericalAddress() {
        return this._address;
    }
}

