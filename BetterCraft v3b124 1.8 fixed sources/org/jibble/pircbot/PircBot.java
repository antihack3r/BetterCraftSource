/*
 * Decompiled with CFR 0.152.
 */
package org.jibble.pircbot;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Date;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.StringTokenizer;
import org.jibble.pircbot.DccChat;
import org.jibble.pircbot.DccFileTransfer;
import org.jibble.pircbot.DccManager;
import org.jibble.pircbot.IdentServer;
import org.jibble.pircbot.InputThread;
import org.jibble.pircbot.IrcException;
import org.jibble.pircbot.NickAlreadyInUseException;
import org.jibble.pircbot.OutputThread;
import org.jibble.pircbot.Queue;
import org.jibble.pircbot.ReplyConstants;
import org.jibble.pircbot.User;

public abstract class PircBot
implements ReplyConstants {
    public static final String VERSION = "1.5.0";
    private static final int OP_ADD = 1;
    private static final int OP_REMOVE = 2;
    private static final int VOICE_ADD = 3;
    private static final int VOICE_REMOVE = 4;
    private InputThread _inputThread = null;
    private OutputThread _outputThread = null;
    private String _charset = null;
    private InetAddress _inetAddress = null;
    private String _server = null;
    private int _port = -1;
    private String _password = null;
    private Queue _outQueue = new Queue();
    private long _messageDelay = 1000L;
    private Hashtable _channels = new Hashtable();
    private Hashtable _topics = new Hashtable();
    private DccManager _dccManager = new DccManager(this);
    private int[] _dccPorts = null;
    private InetAddress _dccInetAddress = null;
    private boolean _autoNickChange = false;
    private boolean _verbose = false;
    private String _name;
    private String _nick = this._name = "PircBot";
    private String _login = "PircBot";
    private String _version = "PircBot 1.5.0 Java IRC Bot - www.jibble.org";
    private String _finger = "You ought to be arrested for fingering a bot!";
    private String _channelPrefixes = "#&+!";

    public final synchronized void connect(String string) throws IOException, IrcException, NickAlreadyInUseException {
        this.connect(string, 6667, null);
    }

    public final synchronized void connect(String string, int n2) throws IOException, IrcException, NickAlreadyInUseException {
        this.connect(string, n2, null);
    }

    /*
     * Enabled aggressive block sorting
     */
    public final synchronized void connect(String string, int n2, String string2) throws IOException, IrcException, NickAlreadyInUseException {
        this._server = string;
        this._port = n2;
        this._password = string2;
        if (this.isConnected()) {
            throw new IOException("The PircBot is already connected to an IRC server.  Disconnect first.");
        }
        this.removeAllChannels();
        Socket socket = new Socket(string, n2);
        this.log("*** Connected to server.");
        this._inetAddress = socket.getLocalAddress();
        InputStreamReader inputStreamReader = null;
        OutputStreamWriter outputStreamWriter = null;
        if (this.getEncoding() != null) {
            inputStreamReader = new InputStreamReader(socket.getInputStream(), this.getEncoding());
            outputStreamWriter = new OutputStreamWriter(socket.getOutputStream(), this.getEncoding());
        } else {
            inputStreamReader = new InputStreamReader(socket.getInputStream());
            outputStreamWriter = new OutputStreamWriter(socket.getOutputStream());
        }
        BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
        BufferedWriter bufferedWriter = new BufferedWriter(outputStreamWriter);
        if (string2 != null && !string2.equals("")) {
            OutputThread.sendRawLine(this, bufferedWriter, "PASS " + string2);
        }
        String string3 = this.getName();
        OutputThread.sendRawLine(this, bufferedWriter, "NICK " + string3);
        OutputThread.sendRawLine(this, bufferedWriter, "USER " + this.getLogin() + " 8 * :" + this.getVersion());
        this._inputThread = new InputThread(this, socket, bufferedReader, bufferedWriter);
        String string4 = null;
        int n3 = 1;
        while ((string4 = bufferedReader.readLine()) != null) {
            this.handleLine(string4);
            int n4 = string4.indexOf(" ");
            int n5 = string4.indexOf(" ", n4 + 1);
            if (n5 >= 0) {
                String string5 = string4.substring(n4 + 1, n5);
                if (string5.equals("004")) break;
                if (string5.equals("433")) {
                    if (!this._autoNickChange) {
                        socket.close();
                        this._inputThread = null;
                        throw new NickAlreadyInUseException(string4);
                    }
                    string3 = this.getName() + ++n3;
                    OutputThread.sendRawLine(this, bufferedWriter, "NICK " + string3);
                } else if (!string5.equals("439") && (string5.startsWith("5") || string5.startsWith("4"))) {
                    socket.close();
                    this._inputThread = null;
                    throw new IrcException("Could not log into the IRC server: " + string4);
                }
            }
            this.setNick(string3);
        }
        this.log("*** Logged onto server.");
        socket.setSoTimeout(300000);
        this._inputThread.start();
        if (this._outputThread == null) {
            this._outputThread = new OutputThread(this, this._outQueue);
            this._outputThread.start();
        }
        this.onConnect();
    }

    public final synchronized void reconnect() throws IOException, IrcException, NickAlreadyInUseException {
        if (this.getServer() == null) {
            throw new IrcException("Cannot reconnect to an IRC server because we were never connected to one previously!");
        }
        this.connect(this.getServer(), this.getPort(), this.getPassword());
    }

    public final synchronized void disconnect() {
        this.quitServer();
    }

    public void setAutoNickChange(boolean bl2) {
        this._autoNickChange = bl2;
    }

    public final void startIdentServer() {
        new IdentServer(this, this.getLogin());
    }

    public final void joinChannel(String string) {
        this.sendRawLine("JOIN " + string);
    }

    public final void joinChannel(String string, String string2) {
        this.joinChannel(string + " " + string2);
    }

    public final void partChannel(String string) {
        this.sendRawLine("PART " + string);
    }

    public final void partChannel(String string, String string2) {
        this.sendRawLine("PART " + string + " :" + string2);
    }

    public final void quitServer() {
        this.quitServer("");
    }

    public final void quitServer(String string) {
        this.sendRawLine("QUIT :" + string);
    }

    public final synchronized void sendRawLine(String string) {
        if (this.isConnected()) {
            this._inputThread.sendRawLine(string);
        }
    }

    public final synchronized void sendRawLineViaQueue(String string) {
        if (string == null) {
            throw new NullPointerException("Cannot send null messages to server");
        }
        if (this.isConnected()) {
            this._outQueue.add(string);
        }
    }

    public final void sendMessage(String string, String string2) {
        this._outQueue.add("PRIVMSG " + string + " :" + string2);
    }

    public final void sendAction(String string, String string2) {
        this.sendCTCPCommand(string, "ACTION " + string2);
    }

    public final void sendNotice(String string, String string2) {
        this._outQueue.add("NOTICE " + string + " :" + string2);
    }

    public final void sendCTCPCommand(String string, String string2) {
        this._outQueue.add("PRIVMSG " + string + " :\u0001" + string2 + "\u0001");
    }

    public final void changeNick(String string) {
        this.sendRawLine("NICK " + string);
    }

    public final void identify(String string) {
        this.sendRawLine("NICKSERV IDENTIFY " + string);
    }

    public final void setMode(String string, String string2) {
        this.sendRawLine("MODE " + string + " " + string2);
    }

    public final void sendInvite(String string, String string2) {
        this.sendRawLine("INVITE " + string + " :" + string2);
    }

    public final void ban(String string, String string2) {
        this.sendRawLine("MODE " + string + " +b " + string2);
    }

    public final void unBan(String string, String string2) {
        this.sendRawLine("MODE " + string + " -b " + string2);
    }

    public final void op(String string, String string2) {
        this.setMode(string, "+o " + string2);
    }

    public final void deOp(String string, String string2) {
        this.setMode(string, "-o " + string2);
    }

    public final void voice(String string, String string2) {
        this.setMode(string, "+v " + string2);
    }

    public final void deVoice(String string, String string2) {
        this.setMode(string, "-v " + string2);
    }

    public final void setTopic(String string, String string2) {
        this.sendRawLine("TOPIC " + string + " :" + string2);
    }

    public final void kick(String string, String string2) {
        this.kick(string, string2, "");
    }

    public final void kick(String string, String string2, String string3) {
        this.sendRawLine("KICK " + string + " " + string2 + " :" + string3);
    }

    public final void listChannels() {
        this.listChannels(null);
    }

    public final void listChannels(String string) {
        if (string == null) {
            this.sendRawLine("LIST");
        } else {
            this.sendRawLine("LIST " + string);
        }
    }

    public final DccFileTransfer dccSendFile(File file, String string, int n2) {
        DccFileTransfer dccFileTransfer = new DccFileTransfer(this, this._dccManager, file, string, n2);
        dccFileTransfer.doSend(true);
        return dccFileTransfer;
    }

    protected final void dccReceiveFile(File file, long l2, int n2, int n3) {
        throw new RuntimeException("dccReceiveFile is deprecated, please use sendFile");
    }

    public final DccChat dccSendChatRequest(String string, int n2) {
        DccChat dccChat = null;
        try {
            int n3;
            ServerSocket serverSocket = null;
            int[] nArray = this.getDccPorts();
            if (nArray == null) {
                serverSocket = new ServerSocket(0);
            } else {
                for (n3 = 0; n3 < nArray.length; ++n3) {
                    try {
                        serverSocket = new ServerSocket(nArray[n3]);
                        break;
                    }
                    catch (Exception exception) {
                        continue;
                    }
                }
                if (serverSocket == null) {
                    throw new IOException("All ports returned by getDccPorts() are in use.");
                }
            }
            serverSocket.setSoTimeout(n2);
            n3 = serverSocket.getLocalPort();
            InetAddress inetAddress = this.getDccInetAddress();
            if (inetAddress == null) {
                inetAddress = this.getInetAddress();
            }
            byte[] byArray = inetAddress.getAddress();
            long l2 = this.ipToLong(byArray);
            this.sendCTCPCommand(string, "DCC CHAT chat " + l2 + " " + n3);
            Socket socket = serverSocket.accept();
            serverSocket.close();
            dccChat = new DccChat(this, string, socket);
        }
        catch (Exception exception) {
            // empty catch block
        }
        return dccChat;
    }

    protected final DccChat dccAcceptChatRequest(String string, long l2, int n2) {
        throw new RuntimeException("dccAcceptChatRequest is deprecated, please use onIncomingChatRequest");
    }

    public void log(String string) {
        if (this._verbose) {
            System.out.println(System.currentTimeMillis() + " " + string);
        }
    }

    protected void handleLine(String string) {
        int n2;
        String string2;
        this.log(string);
        if (string.startsWith("PING ")) {
            this.onServerPing(string.substring(5));
            return;
        }
        String string3 = "";
        String string4 = "";
        String string5 = "";
        StringTokenizer stringTokenizer = new StringTokenizer(string);
        String string6 = stringTokenizer.nextToken();
        String string7 = stringTokenizer.nextToken();
        String string8 = null;
        int n3 = string6.indexOf("!");
        int n4 = string6.indexOf("@");
        if (string6.startsWith(":")) {
            if (n3 > 0 && n4 > 0 && n3 < n4) {
                string3 = string6.substring(1, n3);
                string4 = string6.substring(n3 + 1, n4);
                string5 = string6.substring(n4 + 1);
            } else if (stringTokenizer.hasMoreTokens()) {
                string2 = string7;
                n2 = -1;
                try {
                    n2 = Integer.parseInt(string2);
                }
                catch (NumberFormatException numberFormatException) {
                    // empty catch block
                }
                if (n2 != -1) {
                    String string9 = string2;
                    String string10 = string.substring(string.indexOf(string9, string6.length()) + 4, string.length());
                    this.processServerResponse(n2, string10);
                    return;
                }
                string3 = string6;
                string8 = string2;
            } else {
                this.onUnknown(string);
                return;
            }
        }
        string7 = string7.toUpperCase();
        if (string3.startsWith(":")) {
            string3 = string3.substring(1);
        }
        if (string8 == null) {
            string8 = stringTokenizer.nextToken();
        }
        if (string8.startsWith(":")) {
            string8 = string8.substring(1);
        }
        if (string7.equals("PRIVMSG") && string.indexOf(":\u0001") > 0 && string.endsWith("\u0001")) {
            string2 = string.substring(string.indexOf(":\u0001") + 2, string.length() - 1);
            if (string2.equals("VERSION")) {
                this.onVersion(string3, string4, string5, string8);
            } else if (string2.startsWith("ACTION ")) {
                this.onAction(string3, string4, string5, string8, string2.substring(7));
            } else if (string2.startsWith("PING ")) {
                this.onPing(string3, string4, string5, string8, string2.substring(5));
            } else if (string2.equals("TIME")) {
                this.onTime(string3, string4, string5, string8);
            } else if (string2.equals("FINGER")) {
                this.onFinger(string3, string4, string5, string8);
            } else {
                stringTokenizer = new StringTokenizer(string2);
                if (stringTokenizer.countTokens() >= 5 && stringTokenizer.nextToken().equals("DCC")) {
                    n2 = this._dccManager.processRequest(string3, string4, string5, string2) ? '\u0001' : '\u0000';
                    if (n2 == 0) {
                        this.onUnknown(string);
                    }
                } else {
                    this.onUnknown(string);
                }
            }
        } else if (string7.equals("PRIVMSG") && this._channelPrefixes.indexOf(string8.charAt(0)) >= 0) {
            this.onMessage(string8, string3, string4, string5, string.substring(string.indexOf(" :") + 2));
        } else if (string7.equals("PRIVMSG")) {
            this.onPrivateMessage(string3, string4, string5, string.substring(string.indexOf(" :") + 2));
        } else if (string7.equals("JOIN")) {
            string2 = string8;
            this.addUser(string2, new User("", string3));
            this.onJoin(string2, string3, string4, string5);
        } else if (string7.equals("PART")) {
            this.removeUser(string8, string3);
            if (string3.equals(this.getNick())) {
                this.removeChannel(string8);
            }
            this.onPart(string8, string3, string4, string5);
        } else if (string7.equals("NICK")) {
            string2 = string8;
            this.renameUser(string3, string2);
            if (string3.equals(this.getNick())) {
                this.setNick(string2);
            }
            this.onNickChange(string3, string4, string5, string2);
        } else if (string7.equals("NOTICE")) {
            this.onNotice(string3, string4, string5, string8, string.substring(string.indexOf(" :") + 2));
        } else if (string7.equals("QUIT")) {
            if (string3.equals(this.getNick())) {
                this.removeAllChannels();
            } else {
                this.removeUser(string3);
            }
            this.onQuit(string3, string4, string5, string.substring(string.indexOf(" :") + 2));
        } else if (string7.equals("KICK")) {
            string2 = stringTokenizer.nextToken();
            if (string2.equals(this.getNick())) {
                this.removeChannel(string8);
            }
            this.removeUser(string8, string2);
            this.onKick(string8, string3, string4, string5, string2, string.substring(string.indexOf(" :") + 2));
        } else if (string7.equals("MODE")) {
            string2 = string.substring(string.indexOf(string8, 2) + string8.length() + 1);
            if (string2.startsWith(":")) {
                string2 = string2.substring(1);
            }
            this.processMode(string8, string3, string4, string5, string2);
        } else if (string7.equals("TOPIC")) {
            this.onTopic(string8, string.substring(string.indexOf(" :") + 2), string3, System.currentTimeMillis(), true);
        } else if (string7.equals("INVITE")) {
            this.onInvite(string8, string3, string4, string5, string.substring(string.indexOf(" :") + 2));
        } else {
            this.onUnknown(string);
        }
    }

    protected void onConnect() {
    }

    protected void onDisconnect() {
    }

    private final void processServerResponse(int n2, String string) {
        if (n2 == 322) {
            int n3 = string.indexOf(32);
            int n4 = string.indexOf(32, n3 + 1);
            int n5 = string.indexOf(32, n4 + 1);
            int n6 = string.indexOf(58);
            String string2 = string.substring(n3 + 1, n4);
            int n7 = 0;
            try {
                n7 = Integer.parseInt(string.substring(n4 + 1, n5));
            }
            catch (NumberFormatException numberFormatException) {
                // empty catch block
            }
            String string3 = string.substring(n6 + 1);
            this.onChannelInfo(string2, n7, string3);
        } else if (n2 == 332) {
            int n8 = string.indexOf(32);
            int n9 = string.indexOf(32, n8 + 1);
            int n10 = string.indexOf(58);
            String string4 = string.substring(n8 + 1, n9);
            String string5 = string.substring(n10 + 1);
            this._topics.put(string4, string5);
            this.onTopic(string4, string5);
        } else if (n2 == 333) {
            StringTokenizer stringTokenizer = new StringTokenizer(string);
            stringTokenizer.nextToken();
            String string6 = stringTokenizer.nextToken();
            String string7 = stringTokenizer.nextToken();
            long l2 = 0L;
            try {
                l2 = Long.parseLong(stringTokenizer.nextToken()) * 1000L;
            }
            catch (NumberFormatException numberFormatException) {
                // empty catch block
            }
            String string8 = (String)this._topics.get(string6);
            this._topics.remove(string6);
            this.onTopic(string6, string8, string7, l2, false);
        } else if (n2 == 353) {
            int n11 = string.indexOf(" :");
            String string9 = string.substring(string.lastIndexOf(32, n11 - 1) + 1, n11);
            StringTokenizer stringTokenizer = new StringTokenizer(string.substring(string.indexOf(" :") + 2));
            while (stringTokenizer.hasMoreTokens()) {
                String string10 = stringTokenizer.nextToken();
                String string11 = "";
                if (string10.startsWith("@")) {
                    string11 = "@";
                } else if (string10.startsWith("+")) {
                    string11 = "+";
                } else if (string10.startsWith(".")) {
                    string11 = ".";
                }
                string10 = string10.substring(string11.length());
                this.addUser(string9, new User(string11, string10));
            }
        } else if (n2 == 366) {
            String string12 = string.substring(string.indexOf(32) + 1, string.indexOf(" :"));
            User[] userArray = this.getUsers(string12);
            this.onUserList(string12, userArray);
        }
        this.onServerResponse(n2, string);
    }

    protected void onServerResponse(int n2, String string) {
    }

    protected void onUserList(String string, User[] userArray) {
    }

    protected void onMessage(String string, String string2, String string3, String string4, String string5) {
    }

    protected void onPrivateMessage(String string, String string2, String string3, String string4) {
    }

    protected void onAction(String string, String string2, String string3, String string4, String string5) {
    }

    protected void onNotice(String string, String string2, String string3, String string4, String string5) {
    }

    protected void onJoin(String string, String string2, String string3, String string4) {
    }

    protected void onPart(String string, String string2, String string3, String string4) {
    }

    protected void onNickChange(String string, String string2, String string3, String string4) {
    }

    protected void onKick(String string, String string2, String string3, String string4, String string5, String string6) {
    }

    protected void onQuit(String string, String string2, String string3, String string4) {
    }

    protected void onTopic(String string, String string2) {
    }

    protected void onTopic(String string, String string2, String string3, long l2, boolean bl2) {
    }

    protected void onChannelInfo(String string, int n2, String string2) {
    }

    private final void processMode(String string, String string2, String string3, String string4, String string5) {
        if (this._channelPrefixes.indexOf(string.charAt(0)) >= 0) {
            String string6 = string;
            StringTokenizer stringTokenizer = new StringTokenizer(string5);
            String[] stringArray = new String[stringTokenizer.countTokens()];
            int n2 = 0;
            while (stringTokenizer.hasMoreTokens()) {
                stringArray[n2] = stringTokenizer.nextToken();
                ++n2;
            }
            int n3 = 32;
            int n4 = 1;
            for (int i2 = 0; i2 < stringArray[0].length(); ++i2) {
                char c2 = stringArray[0].charAt(i2);
                if (c2 == '+' || c2 == '-') {
                    n3 = c2;
                    continue;
                }
                if (c2 == 'o') {
                    if (n3 == 43) {
                        this.updateUser(string6, 1, stringArray[n4]);
                        this.onOp(string6, string2, string3, string4, stringArray[n4]);
                    } else {
                        this.updateUser(string6, 2, stringArray[n4]);
                        this.onDeop(string6, string2, string3, string4, stringArray[n4]);
                    }
                    ++n4;
                    continue;
                }
                if (c2 == 'v') {
                    if (n3 == 43) {
                        this.updateUser(string6, 3, stringArray[n4]);
                        this.onVoice(string6, string2, string3, string4, stringArray[n4]);
                    } else {
                        this.updateUser(string6, 4, stringArray[n4]);
                        this.onDeVoice(string6, string2, string3, string4, stringArray[n4]);
                    }
                    ++n4;
                    continue;
                }
                if (c2 == 'k') {
                    if (n3 == 43) {
                        this.onSetChannelKey(string6, string2, string3, string4, stringArray[n4]);
                    } else {
                        this.onRemoveChannelKey(string6, string2, string3, string4, stringArray[n4]);
                    }
                    ++n4;
                    continue;
                }
                if (c2 == 'l') {
                    if (n3 == 43) {
                        this.onSetChannelLimit(string6, string2, string3, string4, Integer.parseInt(stringArray[n4]));
                        ++n4;
                        continue;
                    }
                    this.onRemoveChannelLimit(string6, string2, string3, string4);
                    continue;
                }
                if (c2 == 'b') {
                    if (n3 == 43) {
                        this.onSetChannelBan(string6, string2, string3, string4, stringArray[n4]);
                    } else {
                        this.onRemoveChannelBan(string6, string2, string3, string4, stringArray[n4]);
                    }
                    ++n4;
                    continue;
                }
                if (c2 == 't') {
                    if (n3 == 43) {
                        this.onSetTopicProtection(string6, string2, string3, string4);
                        continue;
                    }
                    this.onRemoveTopicProtection(string6, string2, string3, string4);
                    continue;
                }
                if (c2 == 'n') {
                    if (n3 == 43) {
                        this.onSetNoExternalMessages(string6, string2, string3, string4);
                        continue;
                    }
                    this.onRemoveNoExternalMessages(string6, string2, string3, string4);
                    continue;
                }
                if (c2 == 'i') {
                    if (n3 == 43) {
                        this.onSetInviteOnly(string6, string2, string3, string4);
                        continue;
                    }
                    this.onRemoveInviteOnly(string6, string2, string3, string4);
                    continue;
                }
                if (c2 == 'm') {
                    if (n3 == 43) {
                        this.onSetModerated(string6, string2, string3, string4);
                        continue;
                    }
                    this.onRemoveModerated(string6, string2, string3, string4);
                    continue;
                }
                if (c2 == 'p') {
                    if (n3 == 43) {
                        this.onSetPrivate(string6, string2, string3, string4);
                        continue;
                    }
                    this.onRemovePrivate(string6, string2, string3, string4);
                    continue;
                }
                if (c2 != 's') continue;
                if (n3 == 43) {
                    this.onSetSecret(string6, string2, string3, string4);
                    continue;
                }
                this.onRemoveSecret(string6, string2, string3, string4);
            }
            this.onMode(string6, string2, string3, string4, string5);
        } else {
            String string7 = string;
            this.onUserMode(string7, string2, string3, string4, string5);
        }
    }

    protected void onMode(String string, String string2, String string3, String string4, String string5) {
    }

    protected void onUserMode(String string, String string2, String string3, String string4, String string5) {
    }

    protected void onOp(String string, String string2, String string3, String string4, String string5) {
    }

    protected void onDeop(String string, String string2, String string3, String string4, String string5) {
    }

    protected void onVoice(String string, String string2, String string3, String string4, String string5) {
    }

    protected void onDeVoice(String string, String string2, String string3, String string4, String string5) {
    }

    protected void onSetChannelKey(String string, String string2, String string3, String string4, String string5) {
    }

    protected void onRemoveChannelKey(String string, String string2, String string3, String string4, String string5) {
    }

    protected void onSetChannelLimit(String string, String string2, String string3, String string4, int n2) {
    }

    protected void onRemoveChannelLimit(String string, String string2, String string3, String string4) {
    }

    protected void onSetChannelBan(String string, String string2, String string3, String string4, String string5) {
    }

    protected void onRemoveChannelBan(String string, String string2, String string3, String string4, String string5) {
    }

    protected void onSetTopicProtection(String string, String string2, String string3, String string4) {
    }

    protected void onRemoveTopicProtection(String string, String string2, String string3, String string4) {
    }

    protected void onSetNoExternalMessages(String string, String string2, String string3, String string4) {
    }

    protected void onRemoveNoExternalMessages(String string, String string2, String string3, String string4) {
    }

    protected void onSetInviteOnly(String string, String string2, String string3, String string4) {
    }

    protected void onRemoveInviteOnly(String string, String string2, String string3, String string4) {
    }

    protected void onSetModerated(String string, String string2, String string3, String string4) {
    }

    protected void onRemoveModerated(String string, String string2, String string3, String string4) {
    }

    protected void onSetPrivate(String string, String string2, String string3, String string4) {
    }

    protected void onRemovePrivate(String string, String string2, String string3, String string4) {
    }

    protected void onSetSecret(String string, String string2, String string3, String string4) {
    }

    protected void onRemoveSecret(String string, String string2, String string3, String string4) {
    }

    protected void onInvite(String string, String string2, String string3, String string4, String string5) {
    }

    protected void onDccSendRequest(String string, String string2, String string3, String string4, long l2, int n2, int n3) {
    }

    protected void onDccChatRequest(String string, String string2, String string3, long l2, int n2) {
    }

    protected void onIncomingFileTransfer(DccFileTransfer dccFileTransfer) {
    }

    protected void onFileTransferFinished(DccFileTransfer dccFileTransfer, Exception exception) {
    }

    protected void onIncomingChatRequest(DccChat dccChat) {
    }

    protected void onVersion(String string, String string2, String string3, String string4) {
        this.sendRawLine("NOTICE " + string + " :\u0001VERSION " + this._version + "\u0001");
    }

    protected void onPing(String string, String string2, String string3, String string4, String string5) {
        this.sendRawLine("NOTICE " + string + " :\u0001PING " + string5 + "\u0001");
    }

    protected void onServerPing(String string) {
        this.sendRawLine("PONG " + string);
    }

    protected void onTime(String string, String string2, String string3, String string4) {
        this.sendRawLine("NOTICE " + string + " :\u0001TIME " + new Date().toString() + "\u0001");
    }

    protected void onFinger(String string, String string2, String string3, String string4) {
        this.sendRawLine("NOTICE " + string + " :\u0001FINGER " + this._finger + "\u0001");
    }

    protected void onUnknown(String string) {
    }

    public final void setVerbose(boolean bl2) {
        this._verbose = bl2;
    }

    protected final void setName(String string) {
        this._name = string;
    }

    private final void setNick(String string) {
        this._nick = string;
    }

    protected final void setLogin(String string) {
        this._login = string;
    }

    protected final void setVersion(String string) {
        this._version = string;
    }

    protected final void setFinger(String string) {
        this._finger = string;
    }

    public final String getName() {
        return this._name;
    }

    public String getNick() {
        return this._nick;
    }

    public final String getLogin() {
        return this._login;
    }

    public final String getVersion() {
        return this._version;
    }

    public final String getFinger() {
        return this._finger;
    }

    public final synchronized boolean isConnected() {
        return this._inputThread != null && this._inputThread.isConnected();
    }

    public final void setMessageDelay(long l2) {
        if (l2 < 0L) {
            throw new IllegalArgumentException("Cannot have a negative time.");
        }
        this._messageDelay = l2;
    }

    public final long getMessageDelay() {
        return this._messageDelay;
    }

    public final int getMaxLineLength() {
        return 512;
    }

    public final int getOutgoingQueueSize() {
        return this._outQueue.size();
    }

    public final String getServer() {
        return this._server;
    }

    public final int getPort() {
        return this._port;
    }

    public final String getPassword() {
        return this._password;
    }

    public int[] longToIp(long l2) {
        int[] nArray = new int[4];
        for (int i2 = 3; i2 >= 0; --i2) {
            nArray[i2] = (int)(l2 % 256L);
            l2 /= 256L;
        }
        return nArray;
    }

    public long ipToLong(byte[] byArray) {
        if (byArray.length != 4) {
            throw new IllegalArgumentException("byte array must be of length 4");
        }
        long l2 = 0L;
        long l3 = 1L;
        for (int i2 = 3; i2 >= 0; --i2) {
            int n2 = (byArray[i2] + 256) % 256;
            l2 += (long)n2 * l3;
            l3 *= 256L;
        }
        return l2;
    }

    public void setEncoding(String string) throws UnsupportedEncodingException {
        "".getBytes(string);
        this._charset = string;
    }

    public String getEncoding() {
        return this._charset;
    }

    public InetAddress getInetAddress() {
        return this._inetAddress;
    }

    public void setDccInetAddress(InetAddress inetAddress) {
        this._dccInetAddress = inetAddress;
    }

    public InetAddress getDccInetAddress() {
        return this._dccInetAddress;
    }

    public int[] getDccPorts() {
        if (this._dccPorts == null || this._dccPorts.length == 0) {
            return null;
        }
        return (int[])this._dccPorts.clone();
    }

    public void setDccPorts(int[] nArray) {
        this._dccPorts = (int[])(nArray == null || nArray.length == 0 ? null : (int[])nArray.clone());
    }

    public boolean equals(Object object) {
        if (object instanceof PircBot) {
            PircBot pircBot = (PircBot)object;
            return pircBot == this;
        }
        return false;
    }

    public int hashCode() {
        return super.hashCode();
    }

    public String toString() {
        return "Version{" + this._version + "}" + " Connected{" + this.isConnected() + "}" + " Server{" + this._server + "}" + " Port{" + this._port + "}" + " Password{" + this._password + "}";
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public final User[] getUsers(String string) {
        string = string.toLowerCase();
        User[] userArray = new User[]{};
        Hashtable hashtable = this._channels;
        synchronized (hashtable) {
            Hashtable hashtable2 = (Hashtable)this._channels.get(string);
            if (hashtable2 != null) {
                userArray = new User[hashtable2.size()];
                Enumeration enumeration = hashtable2.elements();
                for (int i2 = 0; i2 < userArray.length; ++i2) {
                    User user;
                    userArray[i2] = user = (User)enumeration.nextElement();
                }
            }
        }
        return userArray;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public final String[] getChannels() {
        String[] stringArray = new String[]{};
        Hashtable hashtable = this._channels;
        synchronized (hashtable) {
            stringArray = new String[this._channels.size()];
            Enumeration enumeration = this._channels.keys();
            for (int i2 = 0; i2 < stringArray.length; ++i2) {
                stringArray[i2] = (String)enumeration.nextElement();
            }
        }
        return stringArray;
    }

    public synchronized void dispose() {
        this._outputThread.interrupt();
        this._inputThread.dispose();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private final void addUser(String string, User user) {
        string = string.toLowerCase();
        Hashtable hashtable = this._channels;
        synchronized (hashtable) {
            Hashtable<User, User> hashtable2 = (Hashtable<User, User>)this._channels.get(string);
            if (hashtable2 == null) {
                hashtable2 = new Hashtable<User, User>();
                this._channels.put(string, hashtable2);
            }
            hashtable2.put(user, user);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private final User removeUser(String string, String string2) {
        string = string.toLowerCase();
        User user = new User("", string2);
        Hashtable hashtable = this._channels;
        synchronized (hashtable) {
            Hashtable hashtable2 = (Hashtable)this._channels.get(string);
            if (hashtable2 != null) {
                return (User)hashtable2.remove(user);
            }
        }
        return null;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private final void removeUser(String string) {
        Hashtable hashtable = this._channels;
        synchronized (hashtable) {
            Enumeration enumeration = this._channels.keys();
            while (enumeration.hasMoreElements()) {
                String string2 = (String)enumeration.nextElement();
                this.removeUser(string2, string);
            }
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private final void renameUser(String string, String string2) {
        Hashtable hashtable = this._channels;
        synchronized (hashtable) {
            Enumeration enumeration = this._channels.keys();
            while (enumeration.hasMoreElements()) {
                String string3 = (String)enumeration.nextElement();
                User user = this.removeUser(string3, string);
                if (user == null) continue;
                user = new User(user.getPrefix(), string2);
                this.addUser(string3, user);
            }
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private final void removeChannel(String string) {
        string = string.toLowerCase();
        Hashtable hashtable = this._channels;
        synchronized (hashtable) {
            this._channels.remove(string);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private final void removeAllChannels() {
        Hashtable hashtable = this._channels;
        synchronized (hashtable) {
            this._channels = new Hashtable();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private final void updateUser(String string, int n2, String string2) {
        string = string.toLowerCase();
        Hashtable hashtable = this._channels;
        synchronized (hashtable) {
            Hashtable hashtable2 = (Hashtable)this._channels.get(string);
            User user = null;
            if (hashtable2 != null) {
                Enumeration enumeration = hashtable2.elements();
                while (enumeration.hasMoreElements()) {
                    User user2 = (User)enumeration.nextElement();
                    if (!user2.getNick().equalsIgnoreCase(string2)) continue;
                    if (n2 == 1) {
                        if (user2.hasVoice()) {
                            user = new User("@+", string2);
                            continue;
                        }
                        user = new User("@", string2);
                        continue;
                    }
                    if (n2 == 2) {
                        if (user2.hasVoice()) {
                            user = new User("+", string2);
                            continue;
                        }
                        user = new User("", string2);
                        continue;
                    }
                    if (n2 == 3) {
                        if (user2.isOp()) {
                            user = new User("@+", string2);
                            continue;
                        }
                        user = new User("+", string2);
                        continue;
                    }
                    if (n2 != 4) continue;
                    if (user2.isOp()) {
                        user = new User("@", string2);
                        continue;
                    }
                    user = new User("", string2);
                }
            }
            if (user != null) {
                hashtable2.put(user, user);
            } else {
                user = new User("", string2);
                hashtable2.put(user, user);
            }
        }
    }
}

