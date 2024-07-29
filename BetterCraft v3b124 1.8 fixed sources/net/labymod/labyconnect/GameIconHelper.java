/*
 * Decompiled with CFR 0.152.
 */
package net.labymod.labyconnect;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.concurrent.atomic.AtomicBoolean;
import javax.imageio.ImageIO;
import net.labymod.labyconnect.user.ChatUser;
import net.labymod.main.LabyMod;
import net.minecraft.client.Minecraft;
import net.minecraft.util.Util;
import org.lwjgl.opengl.Display;

public class GameIconHelper {
    private static AtomicBoolean iconModified = new AtomicBoolean(false);

    public static void updateIcon(boolean force, boolean increase) {
        if (!LabyMod.getSettings().unreadMessageIcon) {
            return;
        }
        Util.EnumOS util$enumos = Util.getOSType();
        if (util$enumos == Util.EnumOS.OSX) {
            return;
        }
        if (Display.isActive() && !force) {
            return;
        }
        if (!iconModified.get() && !increase) {
            return;
        }
        new Thread(new Runnable(){

            @Override
            public void run() {
                try {
                    int unreadMessages = 0;
                    for (ChatUser chatUser : LabyMod.getInstance().getLabyConnect().getFriends()) {
                        unreadMessages += chatUser.getUnreadMessages();
                    }
                    final ByteBuffer smallIcon = GameIconHelper.provideIconBuffer(unreadMessages == 0 ? "assets/minecraft/labymod/data/icons/icon_16x16.png" : "assets/minecraft/labymod/data/icons/icon_notify_16x16.png", unreadMessages);
                    final ByteBuffer normalIcon = GameIconHelper.provideIconBuffer(unreadMessages == 0 ? "assets/minecraft/labymod/data/icons/icon_32x32.png" : "assets/minecraft/labymod/data/icons/icon_notify_32x32.png", unreadMessages);
                    final boolean modified = unreadMessages != 0;
                    Minecraft.getMinecraft().addScheduledTask(new Runnable(){

                        @Override
                        public void run() {
                            iconModified.set(modified);
                            Display.setIcon(new ByteBuffer[]{smallIcon, normalIcon});
                        }
                    });
                }
                catch (Exception ioexception) {
                    ioexception.printStackTrace();
                }
            }
        }).start();
    }

    private static BufferedImage addUnreadMessageNumber(BufferedImage bufferedImage, int unreadMessages) {
        if (unreadMessages == 0) {
            return bufferedImage;
        }
        if (unreadMessages > 99) {
            unreadMessages = 99;
        }
        int width = bufferedImage.getWidth();
        int height = bufferedImage.getHeight();
        BufferedImage newBufferedImage = new BufferedImage(width, height, 2);
        Graphics2D graphics = newBufferedImage.createGraphics();
        graphics.drawImage(bufferedImage, 0, 0, width, height, null);
        graphics.setPaint(Color.WHITE);
        graphics.setFont(new Font("Arial", 1, 16));
        String string = String.valueOf(unreadMessages);
        int stringwidth = graphics.getFontMetrics().stringWidth(string);
        graphics.drawString(String.valueOf(unreadMessages), 22 - stringwidth / 2, 28);
        graphics.dispose();
        return newBufferedImage;
    }

    private static ByteBuffer provideIconBuffer(String source, int unreadMessages) throws IOException {
        int bytesRead;
        File file = new File("LabyMod/", "icon.png");
        InputStream inputStream = Thread.currentThread().getContextClassLoader().getResourceAsStream(source);
        FileOutputStream fileOutputStream = new FileOutputStream(file);
        byte[] buffer = new byte[1024];
        while ((bytesRead = inputStream.read(buffer)) != -1) {
            fileOutputStream.write(buffer, 0, bytesRead);
        }
        inputStream.close();
        fileOutputStream.close();
        ByteBuffer byteBuffer = GameIconHelper.readImageToBuffer(new FileInputStream(file), unreadMessages);
        return byteBuffer;
    }

    private static ByteBuffer readImageToBuffer(InputStream imageStream, int unreadMessages) throws IOException {
        BufferedImage bufferedimage = ImageIO.read(imageStream);
        bufferedimage = GameIconHelper.addUnreadMessageNumber(bufferedimage, unreadMessages);
        int[] aint = bufferedimage.getRGB(0, 0, bufferedimage.getWidth(), bufferedimage.getHeight(), null, 0, bufferedimage.getWidth());
        ByteBuffer bytebuffer = ByteBuffer.allocate(4 * aint.length);
        int[] nArray = aint;
        int n2 = aint.length;
        int n3 = 0;
        while (n3 < n2) {
            int i2 = nArray[n3];
            bytebuffer.putInt(i2 << 8 | i2 >> 24 & 0xFF);
            ++n3;
        }
        bytebuffer.flip();
        return bytebuffer;
    }
}

