/*
 * Decompiled with CFR 0.152.
 */
package org.jibble.pircbot;

import java.util.StringTokenizer;
import java.util.Vector;
import org.jibble.pircbot.DccChat;
import org.jibble.pircbot.DccFileTransfer;
import org.jibble.pircbot.PircBot;

public class DccManager {
    private PircBot _bot;
    private Vector _awaitingResume = new Vector();

    DccManager(PircBot pircBot) {
        this._bot = pircBot;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    boolean processRequest(String string, String string2, String string3, String string4) {
        StringTokenizer stringTokenizer = new StringTokenizer(string4);
        stringTokenizer.nextToken();
        String string5 = stringTokenizer.nextToken();
        String string6 = stringTokenizer.nextToken();
        if (string5.equals("SEND")) {
            long l2 = Long.parseLong(stringTokenizer.nextToken());
            int n2 = Integer.parseInt(stringTokenizer.nextToken());
            long l3 = -1L;
            try {
                l3 = Long.parseLong(stringTokenizer.nextToken());
            }
            catch (Exception exception) {
                // empty catch block
            }
            DccFileTransfer dccFileTransfer = new DccFileTransfer(this._bot, this, string, string2, string3, string5, string6, l2, n2, l3);
            this._bot.onIncomingFileTransfer(dccFileTransfer);
        } else if (string5.equals("RESUME")) {
            int n3 = Integer.parseInt(stringTokenizer.nextToken());
            long l4 = Long.parseLong(stringTokenizer.nextToken());
            DccFileTransfer dccFileTransfer = null;
            Vector vector = this._awaitingResume;
            synchronized (vector) {
                for (int i2 = 0; i2 < this._awaitingResume.size(); ++i2) {
                    dccFileTransfer = (DccFileTransfer)this._awaitingResume.elementAt(i2);
                    if (!dccFileTransfer.getNick().equals(string) || dccFileTransfer.getPort() != n3) continue;
                    this._awaitingResume.removeElementAt(i2);
                    break;
                }
            }
            if (dccFileTransfer != null) {
                dccFileTransfer.setProgress(l4);
                this._bot.sendCTCPCommand(string, "DCC ACCEPT file.ext " + n3 + " " + l4);
            }
        } else if (string5.equals("ACCEPT")) {
            int n4 = Integer.parseInt(stringTokenizer.nextToken());
            long l5 = Long.parseLong(stringTokenizer.nextToken());
            DccFileTransfer dccFileTransfer = null;
            Vector vector = this._awaitingResume;
            synchronized (vector) {
                for (int i3 = 0; i3 < this._awaitingResume.size(); ++i3) {
                    dccFileTransfer = (DccFileTransfer)this._awaitingResume.elementAt(i3);
                    if (!dccFileTransfer.getNick().equals(string) || dccFileTransfer.getPort() != n4) continue;
                    this._awaitingResume.removeElementAt(i3);
                    break;
                }
            }
            if (dccFileTransfer != null) {
                dccFileTransfer.doReceive(dccFileTransfer.getFile(), true);
            }
        } else if (string5.equals("CHAT")) {
            long l6 = Long.parseLong(stringTokenizer.nextToken());
            int n5 = Integer.parseInt(stringTokenizer.nextToken());
            final DccChat dccChat = new DccChat(this._bot, string, string2, string3, l6, n5);
            new Thread(){

                public void run() {
                    DccManager.this._bot.onIncomingChatRequest(dccChat);
                }
            }.start();
        } else {
            return false;
        }
        return true;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    void addAwaitingResume(DccFileTransfer dccFileTransfer) {
        Vector vector = this._awaitingResume;
        synchronized (vector) {
            this._awaitingResume.addElement(dccFileTransfer);
        }
    }

    void removeAwaitingResume(DccFileTransfer dccFileTransfer) {
        this._awaitingResume.removeElement(dccFileTransfer);
    }
}

