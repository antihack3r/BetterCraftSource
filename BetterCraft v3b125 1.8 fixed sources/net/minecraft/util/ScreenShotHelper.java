/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.util;

import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.io.File;
import java.nio.IntBuffer;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.imageio.ImageIO;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.texture.TextureUtil;
import net.minecraft.client.shader.Framebuffer;
import net.minecraft.event.ClickEvent;
import net.minecraft.src.Config;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.IChatComponent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;

public class ScreenShotHelper {
    private static final Logger logger = LogManager.getLogger();
    private static final DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd_HH.mm.ss");
    private static IntBuffer pixelBuffer;
    private static int[] pixelValues;

    public static IChatComponent saveScreenshot(File gameDirectory, int width, int height, Framebuffer buffer) {
        return ScreenShotHelper.saveScreenshot(gameDirectory, null, width, height, buffer);
    }

    public static IChatComponent saveScreenshot(File gameDirectory, String screenshotName, int width, int height, Framebuffer buffer) {
        try {
            boolean flag;
            File file1 = new File(gameDirectory, "screenshots");
            file1.mkdir();
            Minecraft minecraft = Minecraft.getMinecraft();
            int i2 = Config.getGameSettings().guiScale;
            ScaledResolution scaledresolution = new ScaledResolution(minecraft);
            int j2 = scaledresolution.getScaleFactor();
            int k2 = Config.getScreenshotSize();
            boolean bl2 = flag = OpenGlHelper.isFramebufferEnabled() && k2 > 1;
            if (flag) {
                Config.getGameSettings().guiScale = j2 * k2;
                ScreenShotHelper.resize(width * k2, height * k2);
                GlStateManager.pushMatrix();
                GlStateManager.clear(16640);
                minecraft.getFramebuffer().bindFramebuffer(true);
                minecraft.entityRenderer.updateCameraAndRender(Config.renderPartialTicks, System.nanoTime());
            }
            if (OpenGlHelper.isFramebufferEnabled()) {
                width = buffer.framebufferTextureWidth;
                height = buffer.framebufferTextureHeight;
            }
            int l2 = width * height;
            if (pixelBuffer == null || pixelBuffer.capacity() < l2) {
                pixelBuffer = BufferUtils.createIntBuffer(l2);
                pixelValues = new int[l2];
            }
            GL11.glPixelStorei(3333, 1);
            GL11.glPixelStorei(3317, 1);
            pixelBuffer.clear();
            if (OpenGlHelper.isFramebufferEnabled()) {
                GlStateManager.bindTexture(buffer.framebufferTexture);
                GL11.glGetTexImage(3553, 0, 32993, 33639, pixelBuffer);
            } else {
                GL11.glReadPixels(0, 0, width, height, 32993, 33639, pixelBuffer);
            }
            pixelBuffer.get(pixelValues);
            TextureUtil.processPixelValues(pixelValues, width, height);
            BufferedImage bufferedimage = null;
            if (OpenGlHelper.isFramebufferEnabled()) {
                int i1;
                bufferedimage = new BufferedImage(buffer.framebufferWidth, buffer.framebufferHeight, 1);
                int j1 = i1 = buffer.framebufferTextureHeight - buffer.framebufferHeight;
                while (j1 < buffer.framebufferTextureHeight) {
                    int k1 = 0;
                    while (k1 < buffer.framebufferWidth) {
                        bufferedimage.setRGB(k1, j1 - i1, pixelValues[j1 * buffer.framebufferTextureWidth + k1]);
                        ++k1;
                    }
                    ++j1;
                }
            } else {
                bufferedimage = new BufferedImage(width, height, 1);
                bufferedimage.setRGB(0, 0, width, height, pixelValues, 0, width);
            }
            if (flag) {
                minecraft.getFramebuffer().unbindFramebuffer();
                GlStateManager.popMatrix();
                Config.getGameSettings().guiScale = i2;
                ScreenShotHelper.resize(width, height);
            }
            File file2 = screenshotName == null ? ScreenShotHelper.getTimestampedPNGFileForDirectory(file1) : new File(file1, screenshotName);
            file2 = file2.getCanonicalFile();
            ImageIO.write((RenderedImage)bufferedimage, "png", file2);
            ChatComponentText ichatcomponent = new ChatComponentText(file2.getName());
            ichatcomponent.getChatStyle().setChatClickEvent(new ClickEvent(ClickEvent.Action.OPEN_FILE, file2.getAbsolutePath()));
            ichatcomponent.getChatStyle().setUnderlined(true);
            return new ChatComponentTranslation("screenshot.success", ichatcomponent);
        }
        catch (Exception exception) {
            logger.warn("Couldn't save screenshot", (Throwable)exception);
            return new ChatComponentTranslation("screenshot.failure", exception.getMessage());
        }
    }

    private static File getTimestampedPNGFileForDirectory(File gameDirectory) {
        String s2 = dateFormat.format(new Date()).toString();
        int i2 = 1;
        File file1;
        while ((file1 = new File(gameDirectory, String.valueOf(s2) + (i2 == 1 ? "" : "_" + i2) + ".png")).exists()) {
            ++i2;
        }
        return file1;
    }

    private static void resize(int p_resize_0_, int p_resize_1_) {
        Minecraft minecraft = Minecraft.getMinecraft();
        minecraft.displayWidth = Math.max(1, p_resize_0_);
        minecraft.displayHeight = Math.max(1, p_resize_1_);
        if (minecraft.currentScreen != null) {
            ScaledResolution scaledresolution = new ScaledResolution(minecraft);
            minecraft.currentScreen.onResize(minecraft, scaledresolution.getScaledWidth(), scaledresolution.getScaledHeight());
        }
        ScreenShotHelper.updateFramebufferSize();
    }

    private static void updateFramebufferSize() {
        Minecraft minecraft = Minecraft.getMinecraft();
        minecraft.getFramebuffer().createBindFramebuffer(minecraft.displayWidth, minecraft.displayHeight);
        if (minecraft.entityRenderer != null) {
            minecraft.entityRenderer.updateShaderGroupSize(minecraft.displayWidth, minecraft.displayHeight);
        }
    }
}

