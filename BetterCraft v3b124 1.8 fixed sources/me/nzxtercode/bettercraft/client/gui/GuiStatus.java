/*
 * Decompiled with CFR 0.152.
 */
package me.nzxtercode.bettercraft.client.gui;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.util.EnumChatFormatting;
import org.apache.commons.lang3.StringUtils;

public class GuiStatus
extends GuiScreen {
    private GuiScreen parent;
    private Map<String, String> lines;

    public GuiStatus(GuiScreen parent) {
        this.parent = parent;
    }

    @Override
    public void initGui() {
        CompletableFuture.runAsync(() -> {
            this.lines = this.getStatus();
            Map<String, String> map = this.lines;
        }, Executors.newSingleThreadExecutor());
        this.getStatus();
        this.buttonList.add(new GuiButton(0, width / 2 - 75, height / 2 + 80, 150, 20, "Back"));
        super.initGui();
    }

    @Override
    protected void actionPerformed(GuiButton button) throws IOException {
        switch (button.id) {
            case 0: {
                this.mc.displayGuiScreen(this.parent);
            }
        }
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        this.drawDefaultBackground();
        GuiStatus.drawCenteredString(this.fontRendererObj, "Minecraft Server Status", width / 2, height / 2 - 100, -1);
        if (Objects.nonNull(this.lines)) {
            AtomicInteger y2 = new AtomicInteger();
            this.lines.forEach((key, value) -> GuiStatus.drawCenteredString(this.fontRendererObj, String.format("%s: %s", StringUtils.capitalize(key), value), width / 2, height / 2 - 70 + y2.getAndAdd(this.mc.fontRendererObj.FONT_HEIGHT), -1));
        }
        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    public final Map<String, String> getStatus() {
        ConcurrentHashMap<String, String> status = new ConcurrentHashMap<String, String>();
        try {
            TrustManager[] trustAllCerts = new TrustManager[]{new X509TrustManager(){

                @Override
                public X509Certificate[] getAcceptedIssuers() {
                    return null;
                }

                @Override
                public void checkClientTrusted(X509Certificate[] certs, String authType) {
                }

                @Override
                public void checkServerTrusted(X509Certificate[] certs, String authType) {
                }
            }};
            SSLContext sc2 = SSLContext.getInstance("SSL");
            sc2.init(null, trustAllCerts, new SecureRandom());
            HttpsURLConnection.setDefaultSSLSocketFactory(sc2.getSocketFactory());
            URL url = new URL("https://minecraftstatus.net/");
            HttpsURLConnection connection = (HttpsURLConnection)url.openConnection();
            connection.addRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:103.0) Gecko/20100101 Firefox/103.0");
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String content = bufferedReader.lines().collect(() -> new StringBuilder(), (builder, string) -> {
                StringBuilder stringBuilder = builder.append((String)string);
            }, (builder1, builder2) -> {
                StringBuilder stringBuilder = builder1.append((CharSequence)builder2);
            }).toString();
            String[] stringArray = content.split("<h3>Minecraft Java</h3>")[1].split("</div><div class=\"w3-container w3-border w3-padding-16 w3-margin-bottom\">")[0].split("<div class=\"w3-row w3-container w3-border w3-padding-8\">");
            int n2 = stringArray.length;
            int n3 = 0;
            while (n3 < n2) {
                String string2 = stringArray[n3];
                if (!string2.isEmpty()) {
                    status.put(string2.split("<h4>")[1].split("</h4>")[0], String.format("%s%s", new Object[]{EnumChatFormatting.getValueByName(string2.split("w3-right-align w3-text-")[1].split("\"><h5>")[0]), string2.split("<h5>")[1].split("</h5>")[0]}));
                }
                ++n3;
            }
            bufferedReader.close();
        }
        catch (Exception exception) {
            exception.printStackTrace();
        }
        return status;
    }
}

