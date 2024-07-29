/*
 * Decompiled with CFR 0.152.
 */
package org.jibble.pircbot;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FilterOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import org.jibble.pircbot.DccManager;
import org.jibble.pircbot.PircBot;

public class DccFileTransfer {
    public static final int BUFFER_SIZE = 1024;
    private PircBot _bot;
    private DccManager _manager;
    private String _nick;
    private String _login = null;
    private String _hostname = null;
    private String _type;
    private long _address;
    private int _port;
    private long _size;
    private boolean _received;
    private Socket _socket = null;
    private long _progress = 0L;
    private File _file = null;
    private int _timeout = 0;
    private boolean _incoming;
    private long _packetDelay = 0L;
    private long _startTime = 0L;

    DccFileTransfer(PircBot pircBot, DccManager dccManager, String string, String string2, String string3, String string4, String string5, long l2, int n2, long l3) {
        this._bot = pircBot;
        this._manager = dccManager;
        this._nick = string;
        this._login = string2;
        this._hostname = string3;
        this._type = string4;
        this._file = new File(string5);
        this._address = l2;
        this._port = n2;
        this._size = l3;
        this._received = false;
        this._incoming = true;
    }

    DccFileTransfer(PircBot pircBot, DccManager dccManager, File file, String string, int n2) {
        this._bot = pircBot;
        this._manager = dccManager;
        this._nick = string;
        this._file = file;
        this._size = file.length();
        this._timeout = n2;
        this._received = true;
        this._incoming = false;
    }

    public synchronized void receive(File file, boolean bl2) {
        if (!this._received) {
            this._received = true;
            this._file = file;
            if (this._type.equals("SEND") && bl2) {
                this._progress = file.length();
                if (this._progress == 0L) {
                    this.doReceive(file, false);
                } else {
                    this._bot.sendCTCPCommand(this._nick, "DCC RESUME file.ext " + this._port + " " + this._progress);
                    this._manager.addAwaitingResume(this);
                }
            } else {
                this._progress = file.length();
                this.doReceive(file, bl2);
            }
        }
    }

    void doReceive(final File file, final boolean bl2) {
        new Thread(){

            /*
             * WARNING - Removed try catching itself - possible behaviour change.
             * Loose catch block
             */
            public void run() {
                Exception exception;
                block9: {
                    FilterOutputStream filterOutputStream = null;
                    exception = null;
                    int[] nArray = DccFileTransfer.this._bot.longToIp(DccFileTransfer.this._address);
                    String string = nArray[0] + "." + nArray[1] + "." + nArray[2] + "." + nArray[3];
                    DccFileTransfer.this._socket = new Socket(string, DccFileTransfer.this._port);
                    DccFileTransfer.this._socket.setSoTimeout(30000);
                    DccFileTransfer.this._startTime = System.currentTimeMillis();
                    DccFileTransfer.this._manager.removeAwaitingResume(DccFileTransfer.this);
                    BufferedInputStream bufferedInputStream = new BufferedInputStream(DccFileTransfer.this._socket.getInputStream());
                    BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(DccFileTransfer.this._socket.getOutputStream());
                    filterOutputStream = new BufferedOutputStream(new FileOutputStream(file.getCanonicalPath(), bl2));
                    byte[] byArray = new byte[1024];
                    byte[] byArray2 = new byte[4];
                    int n2 = 0;
                    while ((n2 = bufferedInputStream.read(byArray, 0, byArray.length)) != -1) {
                        ((BufferedOutputStream)filterOutputStream).write(byArray, 0, n2);
                        DccFileTransfer.this._progress += n2;
                        byArray2[0] = (byte)(DccFileTransfer.this._progress >> 24 & 0xFFL);
                        byArray2[1] = (byte)(DccFileTransfer.this._progress >> 16 & 0xFFL);
                        byArray2[2] = (byte)(DccFileTransfer.this._progress >> 8 & 0xFFL);
                        byArray2[3] = (byte)(DccFileTransfer.this._progress >> 0 & 0xFFL);
                        bufferedOutputStream.write(byArray2);
                        bufferedOutputStream.flush();
                        DccFileTransfer.this.delay();
                    }
                    ((BufferedOutputStream)filterOutputStream).flush();
                    Object var11_11 = null;
                    try {
                        filterOutputStream.close();
                        DccFileTransfer.this._socket.close();
                    }
                    catch (Exception exception2) {}
                    break block9;
                    {
                        catch (Exception exception3) {
                            exception = exception3;
                            Object var11_12 = null;
                            try {
                                filterOutputStream.close();
                                DccFileTransfer.this._socket.close();
                            }
                            catch (Exception exception4) {}
                        }
                    }
                    catch (Throwable throwable) {
                        Object var11_13 = null;
                        try {
                            filterOutputStream.close();
                            DccFileTransfer.this._socket.close();
                        }
                        catch (Exception exception5) {
                            // empty catch block
                        }
                        throw throwable;
                    }
                }
                DccFileTransfer.this._bot.onFileTransferFinished(DccFileTransfer.this, exception);
            }
        }.start();
    }

    void doSend(final boolean bl2) {
        new Thread(){

            /*
             * WARNING - Removed try catching itself - possible behaviour change.
             * Loose catch block
             */
            public void run() {
                Exception exception;
                block20: {
                    BufferedInputStream bufferedInputStream = null;
                    exception = null;
                    ServerSocket serverSocket = null;
                    int[] nArray = DccFileTransfer.this._bot.getDccPorts();
                    if (nArray == null) {
                        serverSocket = new ServerSocket(0);
                    } else {
                        for (int i2 = 0; i2 < nArray.length; ++i2) {
                            try {
                                serverSocket = new ServerSocket(nArray[i2]);
                                break;
                            }
                            catch (Exception exception2) {
                                continue;
                            }
                        }
                        if (serverSocket == null) {
                            throw new IOException("All ports returned by getDccPorts() are in use.");
                        }
                    }
                    serverSocket.setSoTimeout(DccFileTransfer.this._timeout);
                    DccFileTransfer.this._port = serverSocket.getLocalPort();
                    InetAddress inetAddress = DccFileTransfer.this._bot.getDccInetAddress();
                    if (inetAddress == null) {
                        inetAddress = DccFileTransfer.this._bot.getInetAddress();
                    }
                    byte[] byArray = inetAddress.getAddress();
                    long l2 = DccFileTransfer.this._bot.ipToLong(byArray);
                    String string = DccFileTransfer.this._file.getName().replace(' ', '_');
                    string = string.replace('\t', '_');
                    if (bl2) {
                        DccFileTransfer.this._manager.addAwaitingResume(DccFileTransfer.this);
                    }
                    DccFileTransfer.this._bot.sendCTCPCommand(DccFileTransfer.this._nick, "DCC SEND " + string + " " + l2 + " " + DccFileTransfer.this._port + " " + DccFileTransfer.this._file.length());
                    DccFileTransfer.this._socket = serverSocket.accept();
                    DccFileTransfer.this._socket.setSoTimeout(30000);
                    DccFileTransfer.this._startTime = System.currentTimeMillis();
                    if (bl2) {
                        DccFileTransfer.this._manager.removeAwaitingResume(DccFileTransfer.this);
                    }
                    serverSocket.close();
                    BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(DccFileTransfer.this._socket.getOutputStream());
                    BufferedInputStream bufferedInputStream2 = new BufferedInputStream(DccFileTransfer.this._socket.getInputStream());
                    bufferedInputStream = new BufferedInputStream(new FileInputStream(DccFileTransfer.this._file));
                    if (DccFileTransfer.this._progress > 0L) {
                        for (long i3 = 0L; i3 < DccFileTransfer.this._progress; i3 += bufferedInputStream.skip(DccFileTransfer.this._progress - i3)) {
                        }
                    }
                    byte[] byArray2 = new byte[1024];
                    byte[] byArray3 = new byte[4];
                    int n2 = 0;
                    while ((n2 = bufferedInputStream.read(byArray2, 0, byArray2.length)) != -1) {
                        bufferedOutputStream.write(byArray2, 0, n2);
                        bufferedOutputStream.flush();
                        bufferedInputStream2.read(byArray3, 0, byArray3.length);
                        DccFileTransfer.this._progress += n2;
                        DccFileTransfer.this.delay();
                    }
                    Object var16_18 = null;
                    try {
                        bufferedInputStream.close();
                        DccFileTransfer.this._socket.close();
                    }
                    catch (Exception exception3) {}
                    break block20;
                    {
                        catch (Exception exception4) {
                            exception = exception4;
                            Object var16_19 = null;
                            try {
                                bufferedInputStream.close();
                                DccFileTransfer.this._socket.close();
                            }
                            catch (Exception exception5) {}
                        }
                    }
                    catch (Throwable throwable) {
                        Object var16_20 = null;
                        try {
                            bufferedInputStream.close();
                            DccFileTransfer.this._socket.close();
                        }
                        catch (Exception exception6) {
                            // empty catch block
                        }
                        throw throwable;
                    }
                }
                DccFileTransfer.this._bot.onFileTransferFinished(DccFileTransfer.this, exception);
            }
        }.start();
    }

    void setProgress(long l2) {
        this._progress = l2;
    }

    private void delay() {
        if (this._packetDelay > 0L) {
            try {
                Thread.sleep(this._packetDelay);
            }
            catch (InterruptedException interruptedException) {
                // empty catch block
            }
        }
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

    public File getFile() {
        return this._file;
    }

    public int getPort() {
        return this._port;
    }

    public boolean isIncoming() {
        return this._incoming;
    }

    public boolean isOutgoing() {
        return !this.isIncoming();
    }

    public void setPacketDelay(long l2) {
        this._packetDelay = l2;
    }

    public long getPacketDelay() {
        return this._packetDelay;
    }

    public long getSize() {
        return this._size;
    }

    public long getProgress() {
        return this._progress;
    }

    public double getProgressPercentage() {
        return 100.0 * ((double)this.getProgress() / (double)this.getSize());
    }

    public void close() {
        try {
            this._socket.close();
        }
        catch (Exception exception) {
            // empty catch block
        }
    }

    public long getTransferRate() {
        long l2 = (System.currentTimeMillis() - this._startTime) / 1000L;
        if (l2 <= 0L) {
            return 0L;
        }
        return this.getProgress() / l2;
    }

    public long getNumericalAddress() {
        return this._address;
    }
}

