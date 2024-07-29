/*
 * Decompiled with CFR 0.152.
 */
package org.jibble.pircbot;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InterruptedIOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.Socket;
import java.util.StringTokenizer;
import org.jibble.pircbot.OutputThread;
import org.jibble.pircbot.PircBot;

public class InputThread
extends Thread {
    private PircBot _bot = null;
    private Socket _socket = null;
    private BufferedReader _breader = null;
    private BufferedWriter _bwriter = null;
    private boolean _isConnected = true;
    private boolean _disposed = false;
    public static final int MAX_LINE_LENGTH = 512;

    InputThread(PircBot pircBot, Socket socket, BufferedReader bufferedReader, BufferedWriter bufferedWriter) {
        this._bot = pircBot;
        this._socket = socket;
        this._breader = bufferedReader;
        this._bwriter = bufferedWriter;
        this.setName(this.getClass() + "-Thread");
    }

    void sendRawLine(String string) {
        OutputThread.sendRawLine(this._bot, this._bwriter, string);
    }

    boolean isConnected() {
        return this._isConnected;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void run() {
        try {
            boolean bl2 = true;
            while (bl2) {
                try {
                    String string = null;
                    while ((string = this._breader.readLine()) != null) {
                        try {
                            this._bot.handleLine(string);
                        }
                        catch (Throwable throwable) {
                            StringWriter stringWriter = new StringWriter();
                            PrintWriter printWriter = new PrintWriter(stringWriter);
                            throwable.printStackTrace(printWriter);
                            printWriter.flush();
                            StringTokenizer stringTokenizer = new StringTokenizer(stringWriter.toString(), "\r\n");
                            PircBot pircBot = this._bot;
                            synchronized (pircBot) {
                                this._bot.log("### Your implementation of PircBot is faulty and you have");
                                this._bot.log("### allowed an uncaught Exception or Error to propagate in your");
                                this._bot.log("### code. It may be possible for PircBot to continue operating");
                                this._bot.log("### normally. Here is the stack trace that was produced: -");
                                this._bot.log("### ");
                                while (stringTokenizer.hasMoreTokens()) {
                                    this._bot.log("### " + stringTokenizer.nextToken());
                                }
                            }
                        }
                    }
                    if (string != null) continue;
                    bl2 = false;
                }
                catch (InterruptedIOException interruptedIOException) {
                    this.sendRawLine("PING " + System.currentTimeMillis() / 1000L);
                }
            }
        }
        catch (Exception exception) {
            // empty catch block
        }
        try {
            this._socket.close();
        }
        catch (Exception exception) {
            // empty catch block
        }
        if (!this._disposed) {
            this._bot.log("*** Disconnected.");
            this._isConnected = false;
            this._bot.onDisconnect();
        }
    }

    public void dispose() {
        try {
            this._disposed = true;
            this._socket.close();
        }
        catch (Exception exception) {
            // empty catch block
        }
    }
}

