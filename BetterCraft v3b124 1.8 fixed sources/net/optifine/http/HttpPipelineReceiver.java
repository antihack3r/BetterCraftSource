/*
 * Decompiled with CFR 0.152.
 */
package net.optifine.http;

import java.io.ByteArrayOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.LinkedHashMap;
import net.minecraft.src.Config;
import net.optifine.http.HttpPipelineConnection;
import net.optifine.http.HttpPipelineRequest;
import net.optifine.http.HttpResponse;

public class HttpPipelineReceiver
extends Thread {
    private HttpPipelineConnection httpPipelineConnection = null;
    private static final Charset ASCII = Charset.forName("ASCII");
    private static final String HEADER_CONTENT_LENGTH = "Content-Length";
    private static final char CR = '\r';
    private static final char LF = '\n';

    public HttpPipelineReceiver(HttpPipelineConnection httpPipelineConnection) {
        super("HttpPipelineReceiver");
        this.httpPipelineConnection = httpPipelineConnection;
    }

    @Override
    public void run() {
        while (!Thread.interrupted()) {
            HttpPipelineRequest httppipelinerequest = null;
            try {
                httppipelinerequest = this.httpPipelineConnection.getNextRequestReceive();
                InputStream inputstream = this.httpPipelineConnection.getInputStream();
                HttpResponse httpresponse = this.readResponse(inputstream);
                this.httpPipelineConnection.onResponseReceived(httppipelinerequest, httpresponse);
            }
            catch (InterruptedException var4) {
                return;
            }
            catch (Exception exception) {
                this.httpPipelineConnection.onExceptionReceive(httppipelinerequest, exception);
            }
        }
    }

    private HttpResponse readResponse(InputStream in2) throws IOException {
        String s2 = this.readLine(in2);
        String[] astring = Config.tokenize(s2, " ");
        if (astring.length < 3) {
            throw new IOException("Invalid status line: " + s2);
        }
        String s1 = astring[0];
        int i2 = Config.parseInt(astring[1], 0);
        String s22 = astring[2];
        LinkedHashMap<String, String> map = new LinkedHashMap<String, String>();
        while (true) {
            String s3;
            if ((s3 = this.readLine(in2)).length() <= 0) {
                byte[] abyte = null;
                String s6 = (String)map.get(HEADER_CONTENT_LENGTH);
                if (s6 != null) {
                    int k2 = Config.parseInt(s6, -1);
                    if (k2 > 0) {
                        abyte = new byte[k2];
                        this.readFull(abyte, in2);
                    }
                } else {
                    String s7 = (String)map.get("Transfer-Encoding");
                    if (Config.equals(s7, "chunked")) {
                        abyte = this.readContentChunked(in2);
                    }
                }
                return new HttpResponse(i2, s2, map, abyte);
            }
            int j2 = s3.indexOf(":");
            if (j2 <= 0) continue;
            String s4 = s3.substring(0, j2).trim();
            String s5 = s3.substring(j2 + 1).trim();
            map.put(s4, s5);
        }
    }

    private byte[] readContentChunked(InputStream in2) throws IOException {
        int i2;
        ByteArrayOutputStream bytearrayoutputstream = new ByteArrayOutputStream();
        do {
            String s2 = this.readLine(in2);
            String[] astring = Config.tokenize(s2, "; ");
            i2 = Integer.parseInt(astring[0], 16);
            byte[] abyte = new byte[i2];
            this.readFull(abyte, in2);
            bytearrayoutputstream.write(abyte);
            this.readLine(in2);
        } while (i2 != 0);
        return bytearrayoutputstream.toByteArray();
    }

    private void readFull(byte[] buf, InputStream in2) throws IOException {
        int i2 = 0;
        while (i2 < buf.length) {
            int j2 = in2.read(buf, i2, buf.length - i2);
            if (j2 < 0) {
                throw new EOFException();
            }
            i2 += j2;
        }
    }

    private String readLine(InputStream in2) throws IOException {
        int j2;
        ByteArrayOutputStream bytearrayoutputstream = new ByteArrayOutputStream();
        int i2 = -1;
        boolean flag = false;
        while ((j2 = in2.read()) >= 0) {
            bytearrayoutputstream.write(j2);
            if (i2 == 13 && j2 == 10) {
                flag = true;
                break;
            }
            i2 = j2;
        }
        byte[] abyte = bytearrayoutputstream.toByteArray();
        String s2 = new String(abyte, ASCII);
        if (flag) {
            s2 = s2.substring(0, s2.length() - 2);
        }
        return s2;
    }
}

