/*
 * Decompiled with CFR 0.152.
 */
package net.labymod.addons.resourcepacks24.gui.elements;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.net.URL;
import javax.net.ssl.HttpsURLConnection;
import net.labymod.addons.resourcepacks24.Resourcepacks24;
import net.labymod.addons.resourcepacks24.api.model.Pack;
import net.labymod.addons.resourcepacks24.api.util.interfaces.ActionResponse;
import net.labymod.addons.resourcepacks24.gui.GuiResourcepacks24;
import net.labymod.addons.resourcepacks24.gui.elements.basement.PackElement;
import net.labymod.addons.resourcepacks24.gui.views.OnlineView;
import net.labymod.addons.resourcepacks24.gui.views.View;
import net.labymod.gui.elements.GuiImageButton;
import net.labymod.main.LabyMod;
import net.labymod.main.Source;
import net.labymod.utils.DrawUtils;
import net.labymod.utils.ModColor;
import net.labymod.utils.ModUtils;
import net.labymod.utils.manager.TooltipHelper;
import net.labymod.utils.texture.DynamicTextureManager;
import net.minecraft.util.ResourceLocation;

public class OnlinePackElement
extends PackElement {
    private Pack pack;
    private DynamicTextureManager dynamicIconManager;
    private View view;
    protected GuiImageButton buttonDownload;
    private double downloadProgress = 0.0;
    private boolean downloadStarted = false;

    public OnlinePackElement(Pack pack, DynamicTextureManager dynamicIconManager, View view) {
        super(false);
        this.pack = pack;
        this.dynamicIconManager = dynamicIconManager;
        this.view = view;
        this.buttonDownload = new GuiImageButton(OnlineView.TEXTURE_ONLINE, 127, 127, 127, 127);
        this.buttonDownload.setImageAlpha(0.3f);
    }

    @Override
    public ResourceLocation getIcon() {
        return this.dynamicIconManager.getTexture("icon/" + this.pack.rp_id, this.pack.thumbnail);
    }

    @Override
    public String getDisplayName() {
        return this.pack.ingame_name;
    }

    @Override
    public String getDescription() {
        return this.pack.description;
    }

    @Override
    public boolean draw(double x2, double y2, double width, double height, int mouseX, int mouseY) {
        boolean mouseOver = super.draw(x2, y2, width, height, mouseX, mouseY);
        DrawUtils draw = LabyMod.getInstance().getDrawUtils();
        DrawUtils.drawRect(x2, y2 + height - 6.0, x2 + height, y2 + height, Integer.MIN_VALUE);
        if (this.downloadStarted) {
            String status = String.valueOf(Math.round(this.downloadProgress)) + "%";
            if (this.downloadProgress == 0.0) {
                status = "Preparing..";
            }
            if (this.downloadProgress == 100.0) {
                status = "Finished";
            }
            DrawUtils.drawRect(x2, y2 + height - 1.0, x2 + height / 100.0 * this.downloadProgress, y2 + height, ModColor.toRGB(80, 255, 80, 255));
            draw.drawString(String.valueOf(ModColor.cl('f')) + status, x2 + 1.0, y2 + height - 5.0, 0.5);
        } else {
            draw.drawString(String.valueOf(ModColor.cl('f')) + ModUtils.humanReadableByteCount(this.pack.size, true, true), x2 + 1.0, y2 + height - 5.0, 0.5);
        }
        if (mouseOver && this.view.isInsideView(mouseX, mouseY) && (double)mouseX < x2 + height) {
            String key = ModColor.cl('e');
            String bracket = ModColor.cl('7');
            String value = ModColor.cl('f');
            String toolTip = "";
            toolTip = String.valueOf(toolTip) + key + "Name" + bracket + ": " + value + this.pack.website_name + "\n";
            toolTip = String.valueOf(toolTip) + key + "Downloads" + bracket + ": " + value + this.pack.download + "\n";
            toolTip = String.valueOf(toolTip) + key + "Category" + bracket + ": " + value + this.pack.category + "\n\n";
            toolTip = String.valueOf(toolTip) + key + "Tags" + bracket + ": ";
            String[] stringArray = this.pack.getTags();
            int n2 = stringArray.length;
            int n3 = 0;
            while (n3 < n2) {
                String tag = stringArray[n3];
                toolTip = String.valueOf(toolTip) + value + tag + ", ";
                ++n3;
            }
            TooltipHelper.getHelper().pointTooltip(mouseX, mouseY, 2000L, toolTip);
        }
        return mouseOver;
    }

    @Override
    public void drawControls(double x2, double y2, double width, double height, boolean isFirstEntry, boolean isLastEntry, boolean isSelected, int mouseX, int mouseY, GuiResourcepacks24 gui) {
        super.drawControls(x2, y2, width, height, isFirstEntry, isLastEntry, isSelected, mouseX, mouseY, gui);
        if (gui.getSharedView().draggingElement == null && !this.downloadStarted) {
            this.buttonDownload.draw(x2 + width - 18.0, y2, 10.0, mouseX, mouseY);
        }
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int mouseButton, final GuiResourcepacks24 gui) {
        super.mouseClicked(mouseX, mouseY, mouseButton, gui);
        if (this.buttonDownload.isMouseOver(mouseX, mouseY) && !this.downloadStarted && gui.getSharedView().isInsideView(mouseX, mouseY)) {
            this.downloadStarted = true;
            gui.getResourcepacks24().getRp24Api().download(this.pack.rp_id, new ActionResponse<String>(){

                @Override
                public void success(String url) {
                    String fileName = ((OnlinePackElement)OnlinePackElement.this).pack.ingame_name.replaceAll("[\\\\/:*?\"<>|]", "");
                    File file = new File(Resourcepacks24.getInstance().resourcepacksDir, String.valueOf(fileName) + ".zip");
                    OnlinePackElement.this.downloadAsync(url, file, gui);
                }

                @Override
                public void failed(String message) {
                    gui.getSharedView().lastErrorMessage = message;
                }
            });
        }
    }

    protected void downloadAsync(final String urlString, final File file, final GuiResourcepacks24 gui) {
        new Thread(new Runnable(){

            @Override
            public void run() {
                try {
                    File tmpFile = new File(file.getParentFile(), String.valueOf(file.getName()) + ".download");
                    URL url = new URL(urlString);
                    HttpsURLConnection httpConnection = (HttpsURLConnection)url.openConnection();
                    httpConnection.setSSLSocketFactory(gui.getResourcepacks24().getRp24Api().getSocketFactory());
                    httpConnection.setRequestProperty("User-Agent", Source.getUserAgent());
                    long contentLength = httpConnection.getContentLength();
                    BufferedInputStream bufferedInputStream = new BufferedInputStream(httpConnection.getInputStream());
                    FileOutputStream fileOutputStream = new FileOutputStream(tmpFile);
                    BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(fileOutputStream, 1024);
                    byte[] data = new byte[6144];
                    long currentLength = 0L;
                    int length = 0;
                    while ((length = bufferedInputStream.read(data, 0, data.length)) >= 0) {
                        bufferedOutputStream.write(data, 0, length);
                        OnlinePackElement.this.downloadProgress = 100.0 / (double)contentLength * (double)(currentLength += (long)length);
                    }
                    bufferedOutputStream.close();
                    bufferedInputStream.close();
                    tmpFile.renameTo(file);
                    gui.reloadRepositories();
                }
                catch (Exception e2) {
                    e2.printStackTrace();
                    gui.getSharedView().lastErrorMessage = e2.getMessage();
                }
            }
        }).start();
    }
}

