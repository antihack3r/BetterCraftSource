/*
 * Decompiled with CFR 0.152.
 */
package net.optifine.http;

import java.io.BufferedOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Proxy;
import java.net.Socket;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Pattern;
import net.minecraft.src.Config;
import net.optifine.http.HttpListener;
import net.optifine.http.HttpPipeline;
import net.optifine.http.HttpPipelineReceiver;
import net.optifine.http.HttpPipelineRequest;
import net.optifine.http.HttpPipelineSender;
import net.optifine.http.HttpRequest;
import net.optifine.http.HttpResponse;

public class HttpPipelineConnection {
    private String host = null;
    private int port = 0;
    private Proxy proxy = Proxy.NO_PROXY;
    private List<HttpPipelineRequest> listRequests = new LinkedList<HttpPipelineRequest>();
    private List<HttpPipelineRequest> listRequestsSend = new LinkedList<HttpPipelineRequest>();
    private Socket socket = null;
    private InputStream inputStream = null;
    private OutputStream outputStream = null;
    private HttpPipelineSender httpPipelineSender = null;
    private HttpPipelineReceiver httpPipelineReceiver = null;
    private int countRequests = 0;
    private boolean responseReceived = false;
    private long keepaliveTimeoutMs = 5000L;
    private int keepaliveMaxCount = 1000;
    private long timeLastActivityMs = System.currentTimeMillis();
    private boolean terminated = false;
    private static final String LF = "\n";
    public static final int TIMEOUT_CONNECT_MS = 5000;
    public static final int TIMEOUT_READ_MS = 5000;
    private static final Pattern patternFullUrl = Pattern.compile("^[a-zA-Z]+://.*");

    public HttpPipelineConnection(String host, int port) {
        this(host, port, Proxy.NO_PROXY);
    }

    public HttpPipelineConnection(String host, int port, Proxy proxy) {
        this.host = host;
        this.port = port;
        this.proxy = proxy;
        this.httpPipelineSender = new HttpPipelineSender(this);
        this.httpPipelineSender.start();
        this.httpPipelineReceiver = new HttpPipelineReceiver(this);
        this.httpPipelineReceiver.start();
    }

    public synchronized boolean addRequest(HttpPipelineRequest pr2) {
        if (this.isClosed()) {
            return false;
        }
        this.addRequest(pr2, this.listRequests);
        this.addRequest(pr2, this.listRequestsSend);
        ++this.countRequests;
        return true;
    }

    private void addRequest(HttpPipelineRequest pr2, List<HttpPipelineRequest> list) {
        list.add(pr2);
        this.notifyAll();
    }

    public synchronized void setSocket(Socket s2) throws IOException {
        if (!this.terminated) {
            if (this.socket != null) {
                throw new IllegalArgumentException("Already connected");
            }
            this.socket = s2;
            this.socket.setTcpNoDelay(true);
            this.inputStream = this.socket.getInputStream();
            this.outputStream = new BufferedOutputStream(this.socket.getOutputStream());
            this.onActivity();
            this.notifyAll();
        }
    }

    public synchronized OutputStream getOutputStream() throws IOException, InterruptedException {
        while (this.outputStream == null) {
            this.checkTimeout();
            this.wait(1000L);
        }
        return this.outputStream;
    }

    public synchronized InputStream getInputStream() throws IOException, InterruptedException {
        while (this.inputStream == null) {
            this.checkTimeout();
            this.wait(1000L);
        }
        return this.inputStream;
    }

    public synchronized HttpPipelineRequest getNextRequestSend() throws InterruptedException, IOException {
        if (this.listRequestsSend.size() <= 0 && this.outputStream != null) {
            this.outputStream.flush();
        }
        return this.getNextRequest(this.listRequestsSend, true);
    }

    public synchronized HttpPipelineRequest getNextRequestReceive() throws InterruptedException {
        return this.getNextRequest(this.listRequests, false);
    }

    private HttpPipelineRequest getNextRequest(List<HttpPipelineRequest> list, boolean remove) throws InterruptedException {
        while (list.size() <= 0) {
            this.checkTimeout();
            this.wait(1000L);
        }
        this.onActivity();
        if (remove) {
            return list.remove(0);
        }
        return list.get(0);
    }

    private void checkTimeout() {
        if (this.socket != null) {
            long j2;
            long i2 = this.keepaliveTimeoutMs;
            if (this.listRequests.size() > 0) {
                i2 = 5000L;
            }
            if ((j2 = System.currentTimeMillis()) > this.timeLastActivityMs + i2) {
                this.terminate(new InterruptedException("Timeout " + i2));
            }
        }
    }

    private void onActivity() {
        this.timeLastActivityMs = System.currentTimeMillis();
    }

    public synchronized void onRequestSent(HttpPipelineRequest pr2) {
        if (!this.terminated) {
            this.onActivity();
        }
    }

    public synchronized void onResponseReceived(HttpPipelineRequest pr2, HttpResponse resp) {
        if (!this.terminated) {
            this.responseReceived = true;
            this.onActivity();
            if (this.listRequests.size() > 0 && this.listRequests.get(0) == pr2) {
                this.listRequests.remove(0);
                pr2.setClosed(true);
                String s2 = resp.getHeader("Location");
                if (resp.getStatus() / 100 == 3 && s2 != null && pr2.getHttpRequest().getRedirects() < 5) {
                    try {
                        s2 = this.normalizeUrl(s2, pr2.getHttpRequest());
                        HttpRequest httprequest = HttpPipeline.makeRequest(s2, pr2.getHttpRequest().getProxy());
                        httprequest.setRedirects(pr2.getHttpRequest().getRedirects() + 1);
                        HttpPipelineRequest httppipelinerequest = new HttpPipelineRequest(httprequest, pr2.getHttpListener());
                        HttpPipeline.addRequest(httppipelinerequest);
                    }
                    catch (IOException ioexception) {
                        pr2.getHttpListener().failed(pr2.getHttpRequest(), ioexception);
                    }
                } else {
                    HttpListener httplistener = pr2.getHttpListener();
                    httplistener.finished(pr2.getHttpRequest(), resp);
                }
                this.checkResponseHeader(resp);
            } else {
                throw new IllegalArgumentException("Response out of order: " + pr2);
            }
        }
    }

    private String normalizeUrl(String url, HttpRequest hr2) {
        if (patternFullUrl.matcher(url).matches()) {
            return url;
        }
        if (url.startsWith("//")) {
            return "http:" + url;
        }
        String s2 = hr2.getHost();
        if (hr2.getPort() != 80) {
            s2 = String.valueOf(s2) + ":" + hr2.getPort();
        }
        if (url.startsWith("/")) {
            return "http://" + s2 + url;
        }
        String s1 = hr2.getFile();
        int i2 = s1.lastIndexOf("/");
        return i2 >= 0 ? "http://" + s2 + s1.substring(0, i2 + 1) + url : "http://" + s2 + "/" + url;
    }

    private void checkResponseHeader(HttpResponse resp) {
        String s1;
        String s2 = resp.getHeader("Connection");
        if (s2 != null && !s2.toLowerCase().equals("keep-alive")) {
            this.terminate(new EOFException("Connection not keep-alive"));
        }
        if ((s1 = resp.getHeader("Keep-Alive")) != null) {
            String[] astring = Config.tokenize(s1, ",;");
            int i2 = 0;
            while (i2 < astring.length) {
                String s22 = astring[i2];
                String[] astring1 = this.split(s22, '=');
                if (astring1.length >= 2) {
                    int k2;
                    int j2;
                    if (astring1[0].equals("timeout") && (j2 = Config.parseInt(astring1[1], -1)) > 0) {
                        this.keepaliveTimeoutMs = j2 * 1000;
                    }
                    if (astring1[0].equals("max") && (k2 = Config.parseInt(astring1[1], -1)) > 0) {
                        this.keepaliveMaxCount = k2;
                    }
                }
                ++i2;
            }
        }
    }

    private String[] split(String str, char separator) {
        int i2 = str.indexOf(separator);
        if (i2 < 0) {
            return new String[]{str};
        }
        String s2 = str.substring(0, i2);
        String s1 = str.substring(i2 + 1);
        return new String[]{s2, s1};
    }

    public synchronized void onExceptionSend(HttpPipelineRequest pr2, Exception e2) {
        this.terminate(e2);
    }

    public synchronized void onExceptionReceive(HttpPipelineRequest pr2, Exception e2) {
        this.terminate(e2);
    }

    private synchronized void terminate(Exception e2) {
        if (!this.terminated) {
            this.terminated = true;
            this.terminateRequests(e2);
            if (this.httpPipelineSender != null) {
                this.httpPipelineSender.interrupt();
            }
            if (this.httpPipelineReceiver != null) {
                this.httpPipelineReceiver.interrupt();
            }
            try {
                if (this.socket != null) {
                    this.socket.close();
                }
            }
            catch (IOException iOException) {
                // empty catch block
            }
            this.socket = null;
            this.inputStream = null;
            this.outputStream = null;
        }
    }

    private void terminateRequests(Exception e2) {
        if (this.listRequests.size() > 0) {
            if (!this.responseReceived) {
                HttpPipelineRequest httppipelinerequest = this.listRequests.remove(0);
                httppipelinerequest.getHttpListener().failed(httppipelinerequest.getHttpRequest(), e2);
                httppipelinerequest.setClosed(true);
            }
            while (this.listRequests.size() > 0) {
                HttpPipelineRequest httppipelinerequest1 = this.listRequests.remove(0);
                HttpPipeline.addRequest(httppipelinerequest1);
            }
        }
    }

    public synchronized boolean isClosed() {
        return this.terminated ? true : this.countRequests >= this.keepaliveMaxCount;
    }

    public int getCountRequests() {
        return this.countRequests;
    }

    public synchronized boolean hasActiveRequests() {
        return this.listRequests.size() > 0;
    }

    public String getHost() {
        return this.host;
    }

    public int getPort() {
        return this.port;
    }

    public Proxy getProxy() {
        return this.proxy;
    }
}

