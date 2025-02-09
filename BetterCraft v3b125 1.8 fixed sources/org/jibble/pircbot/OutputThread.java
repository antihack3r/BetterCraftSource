/*
 * Decompiled with CFR 0.152.
 */
package org.jibble.pircbot;

import java.io.BufferedWriter;
import org.jibble.pircbot.PircBot;
import org.jibble.pircbot.Queue;

public class OutputThread
extends Thread {
    private PircBot _bot = null;
    private Queue _outQueue = null;

    OutputThread(PircBot pircBot, Queue queue) {
        this._bot = pircBot;
        this._outQueue = queue;
        this.setName(this.getClass() + "-Thread");
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    static void sendRawLine(PircBot pircBot, BufferedWriter bufferedWriter, String string) {
        if (string.length() > pircBot.getMaxLineLength() - 2) {
            string = string.substring(0, pircBot.getMaxLineLength() - 2);
        }
        BufferedWriter bufferedWriter2 = bufferedWriter;
        synchronized (bufferedWriter2) {
            try {
                bufferedWriter.write(string + "\r\n");
                bufferedWriter.flush();
                pircBot.log(">>>" + string);
            }
            catch (Exception exception) {
                // empty catch block
            }
        }
    }

    public void run() {
        try {
            boolean bl2 = true;
            while (bl2) {
                Thread.sleep(this._bot.getMessageDelay());
                String string = (String)this._outQueue.next();
                if (string != null) {
                    this._bot.sendRawLine(string);
                    continue;
                }
                bl2 = false;
            }
        }
        catch (InterruptedException interruptedException) {
            // empty catch block
        }
    }
}

