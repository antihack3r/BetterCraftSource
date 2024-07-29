/*
 * Decompiled with CFR 0.152.
 */
package net.labymod.splash;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.awt.Color;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.net.URL;
import java.net.URLConnection;
import net.labymod.main.LabyMod;
import net.labymod.main.ModTextures;
import net.labymod.main.Source;
import net.labymod.splash.SplashEntries;
import net.labymod.splash.advertisement.AdColorAdapter;
import net.labymod.splash.advertisement.Advertisement;
import net.labymod.support.DashboardConnector;
import net.labymod.utils.Consumer;
import net.labymod.utils.DrawUtils;
import net.labymod.utils.ModColor;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;

public class SplashLoader {
    private static SplashLoader loader = new SplashLoader();
    private final Gson gson;
    private SplashEntries entries;
    private Advertisement hoverAdvertisement;
    private Consumer<SplashLoader> loadListener;
    private DashboardConnector dashboardConnector = new DashboardConnector();

    public SplashLoader() {
        this.gson = new GsonBuilder().registerTypeAdapter((Type)((Object)Color.class), new AdColorAdapter()).create();
        this.load();
    }

    public void load() {
        new Thread(new Runnable(){

            @Override
            public void run() {
                try {
                    String line;
                    URLConnection connection = new URL("http://dl.labymod.net/advertisement/entries.json").openConnection();
                    connection.setRequestProperty("User-Agent", Source.getUserAgent());
                    connection.connect();
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                    String json = "";
                    while ((line = bufferedReader.readLine()) != null) {
                        json = String.valueOf(json) + (json.equals("") ? "" : "\n") + line;
                    }
                    if (json.isEmpty()) {
                        return;
                    }
                    SplashLoader.this.entries = SplashLoader.this.gson.fromJson(json, SplashEntries.class);
                    if (SplashLoader.this.loadListener != null) {
                        SplashLoader.this.loadListener.accept(SplashLoader.this);
                    }
                }
                catch (Exception error) {
                    error.printStackTrace();
                }
            }
        }).start();
    }

    public void render(int mouseX, int mouseY) {
        DrawUtils draw = LabyMod.getInstance().getDrawUtils();
        this.hoverAdvertisement = null;
        if (this.entries != null) {
            GlStateManager.enableAlpha();
            GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
            try {
                this.renderList(this.entries.getLeft(), false, 3, 3, mouseX, mouseY);
                this.renderList(this.entries.getRight(), true, draw.getWidth() - 3, 3, mouseX, mouseY);
            }
            catch (Exception error) {
                error.printStackTrace();
                this.entries = null;
            }
            GlStateManager.disableAlpha();
        }
    }

    private void renderList(Advertisement[] advertisements, boolean rightBound, int positionX, int positionY, int mouseX, int mouseY) {
        DrawUtils draw = LabyMod.getInstance().getDrawUtils();
        int iconSize = 15;
        Advertisement[] advertisementArray = advertisements;
        int n2 = advertisements.length;
        int n3 = 0;
        while (n3 < n2) {
            Advertisement advertisement = advertisementArray[n3];
            if (advertisement != null && advertisement.isVisible()) {
                int x2 = positionX;
                GlStateManager.pushMatrix();
                int width = draw.getStringWidth(advertisement.getTitle()) + 2;
                if (rightBound) {
                    x2 -= 15 + width;
                }
                boolean hover = mouseX > x2 && mouseX < x2 + 15 + width && mouseY > positionY && mouseY < positionY + 15;
                Color textColor = hover ? advertisement.getColorHover() : advertisement.getColor();
                String url = String.format("http://dl.labymod.net/advertisement/icons/%s.png", advertisement.getIconName());
                LabyMod.getInstance().getDrawUtils().drawImageUrl(url, x2 - (hover ? 1 : 0) + (rightBound ? width : 0), positionY - (hover ? 1 : 0), 255.0, 255.0, 15 + (hover ? 2 : 0), 15 + (hover ? 2 : 0));
                if (rightBound) {
                    x2 -= 17;
                }
                GlStateManager.popMatrix();
                if (advertisement.isNew()) {
                    float pause = (float)(Math.sin((double)System.currentTimeMillis() / 500.0) * 3.0);
                    if (pause < 0.0f) {
                        pause = 0.0f;
                    }
                    float bounce = (float)Math.abs(Math.cos((double)System.currentTimeMillis() / 100.0) * (double)(-pause));
                    Minecraft.getMinecraft().getTextureManager().bindTexture(ModTextures.BUTTON_EXCLAMATION);
                    draw.drawTexture(x2 + (rightBound ? width + 7 : 0) + 10 + (hover ? (rightBound ? -1 : 1) : 0), (float)(positionY + 5 + (hover ? 1 : 0)) - bounce, hover ? 125.0 : 0.0, 0.0, 120.0, 255.0, 5.0, 10.0, 1.0f);
                }
                draw.drawString(draw.getFontRenderer(), ModColor.createColors(advertisement.getTitle()), x2 + 15 + 2 + (hover ? (rightBound ? -1 : 1) : 0), positionY + 7 - 4, textColor.getRGB());
                if (hover) {
                    this.hoverAdvertisement = advertisement;
                }
                positionY += 16;
            }
            ++n3;
        }
        if (rightBound) {
            this.dashboardConnector.renderIcon(positionX - 10, positionY + 10, mouseX, mouseY);
        }
    }

    public void onClick(int mouseX, int mouseY) {
        if (this.hoverAdvertisement != null) {
            LabyMod.getInstance().openWebpage(this.hoverAdvertisement.getUrl(), true);
        }
        this.dashboardConnector.mouseClicked(mouseX, mouseY, 1);
    }

    public static SplashLoader getLoader() {
        return loader;
    }

    public SplashEntries getEntries() {
        return this.entries;
    }

    public void setLoadListener(Consumer<SplashLoader> loadListener) {
        this.loadListener = loadListener;
    }
}

