/*
 * Decompiled with CFR 0.152.
 */
package net.optifine.http;

import java.io.IOException;
import java.io.InputStream;
import java.io.InterruptedIOException;
import java.net.Proxy;
import java.net.URL;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import net.minecraft.src.Config;
import net.optifine.http.HttpListener;
import net.optifine.http.HttpPipelineConnection;
import net.optifine.http.HttpPipelineRequest;
import net.optifine.http.HttpRequest;
import net.optifine.http.HttpResponse;

public class HttpPipeline {
    private static Map mapConnections = new HashMap();
    public static final String HEADER_USER_AGENT = "User-Agent";
    public static final String HEADER_HOST = "Host";
    public static final String HEADER_ACCEPT = "Accept";
    public static final String HEADER_LOCATION = "Location";
    public static final String HEADER_KEEP_ALIVE = "Keep-Alive";
    public static final String HEADER_CONNECTION = "Connection";
    public static final String HEADER_VALUE_KEEP_ALIVE = "keep-alive";
    public static final String HEADER_TRANSFER_ENCODING = "Transfer-Encoding";
    public static final String HEADER_VALUE_CHUNKED = "chunked";

    public static void addRequest(String urlStr, HttpListener listener) throws IOException {
        HttpPipeline.addRequest(urlStr, listener, Proxy.NO_PROXY);
    }

    public static void addRequest(String urlStr, HttpListener listener, Proxy proxy) throws IOException {
        HttpRequest httprequest = HttpPipeline.makeRequest(urlStr, proxy);
        HttpPipelineRequest httppipelinerequest = new HttpPipelineRequest(httprequest, listener);
        HttpPipeline.addRequest(httppipelinerequest);
    }

    public static HttpRequest makeRequest(String urlStr, Proxy proxy) throws IOException {
        URL url = new URL(urlStr);
        if (!url.getProtocol().equals("http")) {
            throw new IOException("Only protocol http is supported: " + url);
        }
        String s2 = url.getFile();
        String s1 = url.getHost();
        int i2 = url.getPort();
        if (i2 <= 0) {
            i2 = 80;
        }
        String s22 = "GET";
        String s3 = "HTTP/1.1";
        LinkedHashMap<String, String> map = new LinkedHashMap<String, String>();
        map.put(HEADER_USER_AGENT, "Java/" + System.getProperty("java.version"));
        map.put(HEADER_HOST, s1);
        map.put(HEADER_ACCEPT, "text/html, image/gif, image/png");
        map.put(HEADER_CONNECTION, HEADER_VALUE_KEEP_ALIVE);
        byte[] abyte = new byte[]{};
        HttpRequest httprequest = new HttpRequest(s1, i2, proxy, s22, s2, s3, map, abyte);
        return httprequest;
    }

    public static void addRequest(HttpPipelineRequest pr2) {
        HttpRequest httprequest = pr2.getHttpRequest();
        HttpPipelineConnection httppipelineconnection = HttpPipeline.getConnection(httprequest.getHost(), httprequest.getPort(), httprequest.getProxy());
        while (!httppipelineconnection.addRequest(pr2)) {
            HttpPipeline.removeConnection(httprequest.getHost(), httprequest.getPort(), httprequest.getProxy(), httppipelineconnection);
            httppipelineconnection = HttpPipeline.getConnection(httprequest.getHost(), httprequest.getPort(), httprequest.getProxy());
        }
    }

    private static synchronized HttpPipelineConnection getConnection(String host, int port, Proxy proxy) {
        String s2 = HttpPipeline.makeConnectionKey(host, port, proxy);
        HttpPipelineConnection httppipelineconnection = (HttpPipelineConnection)mapConnections.get(s2);
        if (httppipelineconnection == null) {
            httppipelineconnection = new HttpPipelineConnection(host, port, proxy);
            mapConnections.put(s2, httppipelineconnection);
        }
        return httppipelineconnection;
    }

    private static synchronized void removeConnection(String host, int port, Proxy proxy, HttpPipelineConnection hpc) {
        String s2 = HttpPipeline.makeConnectionKey(host, port, proxy);
        HttpPipelineConnection httppipelineconnection = (HttpPipelineConnection)mapConnections.get(s2);
        if (httppipelineconnection == hpc) {
            mapConnections.remove(s2);
        }
    }

    private static String makeConnectionKey(String host, int port, Proxy proxy) {
        String s2 = String.valueOf(host) + ":" + port + "-" + proxy;
        return s2;
    }

    public static byte[] get(String urlStr) throws IOException {
        return HttpPipeline.get(urlStr, Proxy.NO_PROXY);
    }

    public static byte[] get(String urlStr, Proxy proxy) throws IOException {
        if (urlStr.startsWith("file:")) {
            URL url = new URL(urlStr);
            InputStream inputstream = url.openStream();
            byte[] abyte = Config.readAll(inputstream);
            return abyte;
        }
        HttpRequest httprequest = HttpPipeline.makeRequest(urlStr, proxy);
        HttpResponse httpresponse = HttpPipeline.executeRequest(httprequest);
        if (httpresponse.getStatus() / 100 != 2) {
            throw new IOException("HTTP response: " + httpresponse.getStatus());
        }
        return httpresponse.getBody();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static HttpResponse executeRequest(HttpRequest req) throws IOException {
        final HashMap map = new HashMap();
        String s2 = "Response";
        String s1 = "Exception";
        HttpListener httplistener = new HttpListener(){

            /*
             * WARNING - Removed try catching itself - possible behaviour change.
             */
            @Override
            public void finished(HttpRequest req, HttpResponse resp) {
                Map map2 = map;
                synchronized (map2) {
                    map.put("Response", resp);
                    map.notifyAll();
                }
            }

            /*
             * WARNING - Removed try catching itself - possible behaviour change.
             */
            @Override
            public void failed(HttpRequest req, Exception e2) {
                Map map2 = map;
                synchronized (map2) {
                    map.put("Exception", e2);
                    map.notifyAll();
                }
            }
        };
        HashMap hashMap = map;
        synchronized (hashMap) {
            HttpPipelineRequest httppipelinerequest = new HttpPipelineRequest(req, httplistener);
            HttpPipeline.addRequest(httppipelinerequest);
            try {
                map.wait();
            }
            catch (InterruptedException var10) {
                throw new InterruptedIOException("Interrupted");
            }
            Exception exception = (Exception)map.get("Exception");
            if (exception != null) {
                if (exception instanceof IOException) {
                    throw (IOException)exception;
                }
                if (exception instanceof RuntimeException) {
                    throw (RuntimeException)exception;
                }
                throw new RuntimeException(exception.getMessage(), exception);
            }
            HttpResponse httpresponse = (HttpResponse)map.get("Response");
            if (httpresponse == null) {
                throw new IOException("Response is null");
            }
            return httpresponse;
        }
    }

    public static boolean hasActiveRequests() {
        for (Object o2 : mapConnections.values()) {
            HttpPipelineConnection httppipelineconnection = (HttpPipelineConnection)o2;
            if (!httppipelineconnection.hasActiveRequests()) continue;
            return true;
        }
        return false;
    }
}

