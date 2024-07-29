/*
 * Decompiled with CFR 0.152.
 */
package net.labymod.utils.texture;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLSocketFactory;
import net.labymod.support.util.Debug;
import net.labymod.utils.Consumer;
import net.minecraft.client.renderer.texture.SimpleTexture;
import net.minecraft.client.renderer.texture.TextureUtil;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.util.ResourceLocation;

public class ThreadDownloadTextureImage
extends SimpleTexture {
    private static ExecutorService executorService = Executors.newFixedThreadPool(5);
    private final String imageUrl;
    private final Consumer<Boolean> consumer;
    private String userAgent = "Unknown";
    private SSLSocketFactory socketFactory;
    private TextureImageParser textureImageParser;
    private Debug.EnumDebugMode debugMode = Debug.EnumDebugMode.GENERAL;
    private BufferedImage bufferedImage;
    private boolean textureLoaded = false;

    public ThreadDownloadTextureImage(String imageUrl, ResourceLocation textureResourceLocation, Consumer<Boolean> consumer, String userAgent) {
        super(textureResourceLocation);
        this.imageUrl = imageUrl;
        this.consumer = consumer;
        this.userAgent = userAgent;
    }

    @Override
    public int getGlTextureId() {
        int textureId = super.getGlTextureId();
        if (!this.textureLoaded && this.bufferedImage != null) {
            this.textureLoaded = true;
            TextureUtil.uploadTextureImage(textureId, this.bufferedImage);
        }
        return textureId;
    }

    @Override
    public void loadTexture(IResourceManager resourceManager) throws IOException {
        this.downloadTexture(null);
    }

    public void downloadTexture(final Consumer<ThreadDownloadTextureImage> loadTextureCallback) {
        executorService.execute(new Runnable(){

            @Override
            public void run() {
                try {
                    HttpURLConnection httpURLConnection = (HttpURLConnection)new URL(ThreadDownloadTextureImage.this.imageUrl).openConnection();
                    httpURLConnection.setRequestProperty("User-Agent", ThreadDownloadTextureImage.this.userAgent);
                    if (ThreadDownloadTextureImage.this.socketFactory != null && httpURLConnection instanceof HttpsURLConnection) {
                        ((HttpsURLConnection)httpURLConnection).setSSLSocketFactory(ThreadDownloadTextureImage.this.socketFactory);
                    }
                    httpURLConnection.connect();
                    int responseCode = httpURLConnection.getResponseCode();
                    if (responseCode / 100 == 2) {
                        BufferedImage loadedImage = TextureUtil.readBufferedImage(httpURLConnection.getInputStream());
                        if (ThreadDownloadTextureImage.this.textureImageParser != null) {
                            loadedImage = ThreadDownloadTextureImage.this.textureImageParser.parse(loadedImage);
                        }
                        ThreadDownloadTextureImage.this.bufferedImage = loadedImage;
                        if (ThreadDownloadTextureImage.this.bufferedImage == null) {
                            Debug.log(ThreadDownloadTextureImage.this.debugMode, "Failed to download texture " + ThreadDownloadTextureImage.this.imageUrl + " (" + responseCode + ")");
                        } else {
                            Debug.log(ThreadDownloadTextureImage.this.debugMode, "Downloaded texture " + ThreadDownloadTextureImage.this.imageUrl);
                        }
                    } else {
                        Debug.log(ThreadDownloadTextureImage.this.debugMode, "Response code for " + ThreadDownloadTextureImage.this.imageUrl + " is " + responseCode);
                    }
                }
                catch (Exception error) {
                    error.printStackTrace();
                }
                ThreadDownloadTextureImage.this.consumer.accept(ThreadDownloadTextureImage.this.bufferedImage != null);
                if (loadTextureCallback != null) {
                    loadTextureCallback.accept(ThreadDownloadTextureImage.this);
                }
            }
        });
    }

    public void setSocketFactory(SSLSocketFactory socketFactory) {
        this.socketFactory = socketFactory;
    }

    public void setTextureImageParser(TextureImageParser textureImageParser) {
        this.textureImageParser = textureImageParser;
    }

    public void setDebugMode(Debug.EnumDebugMode debugMode) {
        this.debugMode = debugMode;
    }

    public static interface TextureImageParser {
        public BufferedImage parse(BufferedImage var1);
    }
}

