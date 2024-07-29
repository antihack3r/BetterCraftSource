/*
 * Decompiled with CFR 0.152.
 */
package org.jibble.pircbot;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.ServerSocket;
import java.net.Socket;
import org.jibble.pircbot.PircBot;

public class IdentServer
extends Thread {
    private PircBot _bot;
    private String _login;
    private ServerSocket _ss = null;

    IdentServer(PircBot pircBot, String string) {
        this._bot = pircBot;
        this._login = string;
        try {
            this._ss = new ServerSocket(113);
            this._ss.setSoTimeout(60000);
        }
        catch (Exception exception) {
            this._bot.log("*** Could not start the ident server on port 113.");
            return;
        }
        this._bot.log("*** Ident server running on port 113 for the next 60 seconds...");
        this.setName(this.getClass() + "-Thread");
        this.start();
    }

    public void run() {
        try {
            Socket socket = this._ss.accept();
            socket.setSoTimeout(60000);
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            String string = bufferedReader.readLine();
            if (string != null) {
                this._bot.log("*** Ident request received: " + string);
                string = string + " : USERID : UNIX : " + this._login;
                bufferedWriter.write(string + "\r\n");
                bufferedWriter.flush();
                this._bot.log("*** Ident reply sent: " + string);
                bufferedWriter.close();
            }
        }
        catch (Exception exception) {
            // empty catch block
        }
        try {
            this._ss.close();
        }
        catch (Exception exception) {
            // empty catch block
        }
        this._bot.log("*** The Ident server has been shut down.");
    }
}

