/*
 * Decompiled with CFR 0.152.
 */
package net.labymod.addons.resourcepacks24.api;

import com.google.gson.Gson;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import net.labymod.addons.resourcepacks24.api.model.Pack;
import net.labymod.addons.resourcepacks24.api.util.enums.EnumApiAction;
import net.labymod.addons.resourcepacks24.api.util.enums.EnumFeedType;
import net.labymod.addons.resourcepacks24.api.util.enums.EnumResourcepackType;
import net.labymod.addons.resourcepacks24.api.util.interfaces.ActionResponse;
import net.labymod.addons.resourcepacks24.api.util.interfaces.RawResponse;
import net.labymod.addons.resourcepacks24.gui.elements.basement.PackElement;
import net.labymod.main.Source;
import net.labymod.utils.texture.DynamicTextureManager;
import net.labymod.utils.texture.ThreadDownloadTextureImage;
import org.apache.commons.io.IOUtils;

public class Resourcepacks24Api {
    private static final String API_URL = "https://resourcepacks24.de/api/%s/%s";
    private static final Gson GSON = new Gson();
    private final String token;
    private Executor executor = Executors.newSingleThreadExecutor();
    private Map<String, String> jsonCache = new HashMap<String, String>();
    private SSLSocketFactory socketFactory;
    private DynamicTextureManager dynamicIconManager;

    public Resourcepacks24Api(String token) {
        this.token = token;
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
        try {
            SSLContext context = SSLContext.getInstance("SSL");
            context.init(null, trustAllCerts, new SecureRandom());
            this.socketFactory = context.getSocketFactory();
        }
        catch (Exception e2) {
            e2.printStackTrace();
        }
        this.dynamicIconManager = new DynamicTextureManager("rp24", PackElement.DEFAULT_ICON);
        this.dynamicIconManager.init();
        this.dynamicIconManager.setSocketFactory(this.socketFactory);
        this.dynamicIconManager.setUserAgent(Source.getUserAgent());
        this.dynamicIconManager.setTextureImageParser(new ThreadDownloadTextureImage.TextureImageParser(){

            @Override
            public BufferedImage parse(BufferedImage input) {
                int dWidth = 64;
                int dHeight = 64;
                BufferedImage scaledImage = new BufferedImage(64, 64, input.getType());
                Graphics2D graphics2D = scaledImage.createGraphics();
                graphics2D.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
                graphics2D.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
                graphics2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                graphics2D.drawImage(input, 0, 0, 64, 64, null);
                graphics2D.dispose();
                return scaledImage;
            }
        });
    }

    public void download(int id2, final ActionResponse<String> response) {
        this.perform(EnumApiAction.DOWNLOAD, String.valueOf(id2), new RawResponse(){

            @Override
            public void success(String url) {
                response.success(url);
            }

            @Override
            public void error(String message, Exception exception) {
                exception.printStackTrace();
                response.failed(message);
            }
        });
    }

    public void category(final ActionResponse<String[]> response) {
        this.perform(EnumApiAction.CATEGORY, "", new RawResponse(){

            @Override
            public void success(String json) {
                response.success(GSON.fromJson(json, String[].class));
            }

            @Override
            public void error(String message, Exception exception) {
                exception.printStackTrace();
                response.failed(message);
            }
        });
    }

    public void category(String name, int page, final ActionResponse<Pack[]> response) {
        String param = String.valueOf(name) + "?page=" + page;
        this.perform(EnumApiAction.CATEGORY, param, new RawResponse(){

            @Override
            public void success(String json) {
                response.success(GSON.fromJson(json, Pack[].class));
            }

            @Override
            public void error(String message, Exception exception) {
                exception.printStackTrace();
                response.failed(message);
            }
        });
    }

    public void resourcepack(int id2, final ActionResponse<Pack> response) {
        this.perform(EnumApiAction.RESOURCEPACK, String.valueOf(id2), new RawResponse(){

            @Override
            public void success(String json) {
                response.success(GSON.fromJson(json, Pack.class));
            }

            @Override
            public void error(String message, Exception exception) {
                exception.printStackTrace();
                response.failed(message);
            }
        });
    }

    public void resourcepacks(EnumResourcepackType type, final ActionResponse<Pack[]> response) {
        this.perform(EnumApiAction.RESOURCEPACKS, type.getKey(), new RawResponse(){

            @Override
            public void success(String json) {
                response.success(GSON.fromJson(json, Pack[].class));
            }

            @Override
            public void error(String message, Exception exception) {
                exception.printStackTrace();
                response.failed(message);
            }
        });
    }

    public void feed(EnumFeedType type, int page, final ActionResponse<Pack[]> response) {
        String param = String.valueOf(type.getKey()) + "?page=" + page;
        this.perform(EnumApiAction.FEED, param, new RawResponse(){

            @Override
            public void success(String json) {
                response.success(GSON.fromJson(json, Pack[].class));
            }

            @Override
            public void error(String message, Exception exception) {
                exception.printStackTrace();
                response.failed(message);
            }
        });
    }

    public void search(String query, final ActionResponse<Pack[]> response) {
        try {
            this.perform(EnumApiAction.SEARCH, URLEncoder.encode(query, StandardCharsets.UTF_8.toString()), new RawResponse(){

                @Override
                public void success(String json) {
                    response.success(GSON.fromJson(json, Pack[].class));
                }

                @Override
                public void error(String message, Exception exception) {
                    exception.printStackTrace();
                    response.failed(message);
                }
            });
        }
        catch (UnsupportedEncodingException e2) {
            e2.printStackTrace();
        }
    }

    private void perform(final EnumApiAction action, final String value, final RawResponse response) {
        final String cacheKey = String.valueOf(action.name()) + "/" + value;
        String cachedJson = this.jsonCache.get(cacheKey);
        if (cachedJson != null) {
            response.success(cachedJson);
        } else {
            this.executor.execute(new Runnable(){

                @Override
                public void run() {
                    try {
                        URL url = new URL(String.format(Resourcepacks24Api.API_URL, action.getKey(), value));
                        HttpsURLConnection httpConnection = (HttpsURLConnection)url.openConnection();
                        httpConnection.setRequestProperty("Content-Type", "application/json");
                        httpConnection.setRequestProperty("RP24-Token", Resourcepacks24Api.this.token);
                        httpConnection.setRequestProperty("User-Agent", Source.getUserAgent());
                        httpConnection.setSSLSocketFactory(Resourcepacks24Api.this.socketFactory);
                        StringWriter writer = new StringWriter();
                        IOUtils.copy(httpConnection.getInputStream(), (Writer)writer, StandardCharsets.UTF_8);
                        String json = writer.toString();
                        Resourcepacks24Api.this.jsonCache.put(cacheKey, json);
                        response.success(json);
                    }
                    catch (Exception error) {
                        response.error(error.getMessage(), error);
                    }
                }
            });
        }
    }

    public SSLSocketFactory getSocketFactory() {
        return this.socketFactory;
    }

    public DynamicTextureManager getDynamicIconManager() {
        return this.dynamicIconManager;
    }
}

