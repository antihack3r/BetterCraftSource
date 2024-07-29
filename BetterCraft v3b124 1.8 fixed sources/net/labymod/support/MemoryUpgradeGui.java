/*
 * Decompiled with CFR 0.152.
 */
package net.labymod.support;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;
import net.labymod.account.MCBaseFolder;
import net.labymod.main.LabyMod;
import net.labymod.main.Source;
import net.labymod.main.lang.LanguageManager;
import net.labymod.settings.elements.ControlElement;
import net.labymod.settings.elements.SliderElement;
import net.labymod.utils.Consumer;
import net.labymod.utils.DrawUtils;
import net.labymod.utils.JsonParse;
import net.labymod.utils.Material;
import net.labymod.utils.ModColor;
import net.labymod.utils.ModUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiMainMenu;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.GlStateManager;
import org.apache.commons.io.IOUtils;
import org.lwjgl.input.Keyboard;

public class MemoryUpgradeGui
extends GuiScreen {
    private static final long TRIGGER_MAX_MEMORY = 2048000000L;
    private static final int TRIGGER_SCORE = 3;
    private static final long TRIGGER_FRAME_DURATION = 100L;
    private static long maxMemoryUsed = 0L;
    private static long lastMemoryMax = 0L;
    private static boolean outOfMemoryDetected = false;
    private static long lastFrameMemory = -1L;
    private static long lastFrameTimestamp = -1L;
    private static int frameHits = 0;
    private final File launcherProfilesFile = new File(MCBaseFolder.getWorkingDirectory(), "launcher_profiles.json");
    private final GsonBuilder gsonBuilder = new GsonBuilder().setPrettyPrinting();
    private final int totalMB;
    private SliderElement element;
    private GuiButton buttonRestartGame;
    private String errorMessage = null;
    private int updatedMB;

    public MemoryUpgradeGui() {
        long maxMemory = Runtime.getRuntime().maxMemory();
        int totalMB = (int)(maxMemory / 1000000L);
        if (totalMB < 1) {
            totalMB = 1;
        }
        this.totalMB = totalMB;
        this.updatedMB = totalMB;
    }

    public static void renderTickOutOfMemoryDetector() {
        long percent;
        long maxMemory = Runtime.getRuntime().maxMemory();
        if (maxMemory >= 2048000000L) {
            return;
        }
        long totalMemory = Runtime.getRuntime().totalMemory();
        long freeMemory = Runtime.getRuntime().freeMemory();
        long usedMemory = totalMemory - freeMemory;
        maxMemoryUsed = Math.max(usedMemory, lastMemoryMax == maxMemory ? maxMemoryUsed : usedMemory);
        lastMemoryMax = maxMemory;
        if (!outOfMemoryDetected && lastFrameMemory != -1L) {
            long frameDuration = System.currentTimeMillis() - lastFrameTimestamp;
            if (maxMemoryUsed >= 99L) {
                outOfMemoryDetected = true;
            } else if (lastFrameMemory > usedMemory) {
                if (frameDuration > 100L) {
                    if (++frameHits >= 3) {
                        outOfMemoryDetected = true;
                    }
                } else if (frameHits > 0) {
                    --frameHits;
                }
            } else if (frameDuration > 100L && frameHits > 0) {
                --frameHits;
            }
        }
        if ((percent = maxMemoryUsed * 100L / maxMemory) >= 90L) {
            lastFrameMemory = usedMemory;
            lastFrameTimestamp = System.currentTimeMillis();
        } else {
            lastFrameMemory = -1L;
        }
        if (outOfMemoryDetected) {
            MemoryUpgradeGui.renderWarning(percent);
            frameHits = 0;
        }
    }

    private static void renderWarning(long percent) {
        DrawUtils draw = LabyMod.getInstance().getDrawUtils();
        GlStateManager.pushMatrix();
        GlStateManager.translate(0.0f, 0.0f, 10.0f);
        String string = LanguageManager.translate("out_of_memory_warning_explanation");
        int margin = 3;
        int warningWidth = 200;
        List<String> lines = draw.listFormattedStringToWidth(string, 191);
        int warningHeight = 10 + lines.size() * 10 + 3 + 1;
        double warningX = draw.getWidth() - 200 - 1;
        int warningY = 2;
        DrawUtils.drawRect(warningX, 2.0, warningX + 200.0, (double)(2 + warningHeight), Integer.MIN_VALUE);
        draw.drawRectBorder(warningX, 2.0, warningX + 200.0, 2 + warningHeight, Integer.MIN_VALUE, 1.0);
        draw.drawString(String.valueOf(ModColor.cl('c')) + ModColor.cl('n') + LanguageManager.translate("out_of_memory_warning_title"), warningX + 3.0, 5.0);
        draw.drawRightString(String.valueOf(ModColor.cl('4')) + percent + "%", warningX + 200.0 - 3.0, 5.0);
        int lineY = 11;
        for (String line : lines) {
            draw.drawString(String.valueOf(ModColor.cl('f')) + line, warningX + 3.0, 5 + lineY);
            lineY += 10;
        }
        if (Keyboard.isKeyDown(50)) {
            outOfMemoryDetected = false;
            Minecraft.getMinecraft().displayGuiScreen(new MemoryUpgradeGui());
        }
        if (Keyboard.isKeyDown(37)) {
            outOfMemoryDetected = false;
            LabyMod.getSettings().outOfMemoryWarning = false;
        }
        GlStateManager.translate(0.0f, 0.0f, -10.0f);
        GlStateManager.popMatrix();
    }

    @Override
    public void initGui() {
        this.buttonList.clear();
        int divY = height / 2 - 40 + 50;
        this.buttonList.add(new GuiButton(0, width / 2 - 100, divY + 30, 90, 20, LanguageManager.translate("button_cancel")));
        this.buttonRestartGame = new GuiButton(1, width / 2 + 10, divY + 30, 90, 20, LanguageManager.translate("button_restart"));
        this.buttonList.add(this.buttonRestartGame);
        this.buttonRestartGame.enabled = this.totalMB != this.updatedMB;
        this.element = new SliderElement(LanguageManager.translate("out_of_memory_slider"), new ControlElement.IconData(Material.IRON_PICKAXE), this.updatedMB).addCallback(new Consumer<Integer>(){

            @Override
            public void accept(Integer accepted) {
                ((MemoryUpgradeGui)MemoryUpgradeGui.this).buttonRestartGame.enabled = MemoryUpgradeGui.this.totalMB != accepted;
                MemoryUpgradeGui.this.updatedMB = accepted;
            }
        }).setRange(512, 4096).setSteps(512);
        super.initGui();
    }

    private boolean updateMemoryArguments(String version, int mb2) throws Exception {
        JsonObject mainObject = JsonParse.parse(IOUtils.toString(new FileInputStream(this.launcherProfilesFile))).getAsJsonObject();
        JsonObject profiles = mainObject.get("profiles").getAsJsonObject();
        if (!profiles.has(version)) {
            return false;
        }
        JsonObject versionObject = profiles.get(version).getAsJsonObject();
        String javaArgs = "javaArgs";
        if (versionObject.has("javaArgs")) {
            String arguments = versionObject.get("javaArgs").getAsString();
            String newArguments = arguments.replaceAll("-[x|X][m|M][x|X][0-9]+\\w+", "-Xmx" + mb2 + "M");
            versionObject.addProperty("javaArgs", newArguments);
        } else {
            versionObject.addProperty("javaArgs", "-Xmx" + mb2 + "M");
        }
        IOUtils.write(this.gsonBuilder.create().toJson(mainObject), (OutputStream)new FileOutputStream(this.launcherProfilesFile), StandardCharsets.UTF_8);
        return true;
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        this.drawBackground(0);
        DrawUtils draw = LabyMod.getInstance().getDrawUtils();
        int divY = height / 2 - 40;
        draw.drawCenteredString(String.valueOf(ModColor.cl('c')) + LanguageManager.translate("out_of_memory_slider"), width / 2, divY);
        draw.drawCenteredString(LanguageManager.translate("out_of_memory_subtitle"), width / 2, divY + 10);
        draw.drawCenteredString(this.errorMessage == null ? String.valueOf(ModColor.cl('a')) + LanguageManager.translate("out_of_memory_recommendation") : String.valueOf(ModColor.cl("4")) + this.errorMessage, width / 2, divY + 30);
        int centerPosX = width / 2;
        int centerPosY = divY + 55;
        int elementWidth = 200;
        int elementHeight = 22;
        String value = String.valueOf(ModColor.cl('b')) + "= " + ModUtils.humanReadableByteCount((long)this.updatedMB * 1000000L, true, true);
        draw.drawString(value, centerPosX + 100 + 5, centerPosY - 3);
        this.element.draw(centerPosX - 100, centerPosY - 11, centerPosX + 100, centerPosY + 11, mouseX, mouseY);
        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        this.element.mouseClicked(mouseX, mouseY, mouseButton);
        super.mouseClicked(mouseX, mouseY, mouseButton);
    }

    @Override
    protected void mouseReleased(int mouseX, int mouseY, int state) {
        this.element.mouseRelease(mouseX, mouseY, state);
        super.mouseReleased(mouseX, mouseY, state);
    }

    @Override
    protected void actionPerformed(GuiButton button) throws IOException {
        switch (button.id) {
            case 0: {
                Minecraft.getMinecraft().displayGuiScreen(LabyMod.getInstance().isInGame() ? null : new GuiMainMenu());
                break;
            }
            case 1: {
                try {
                    String version = Minecraft.getMinecraft().getVersion();
                    this.killMinecraftLauncher();
                    if (this.updateMemoryArguments(Source.PROFILE_VERSION_NAME, this.updatedMB) | this.updateMemoryArguments(version, this.updatedMB)) {
                        Minecraft.getMinecraft().shutdown();
                        break;
                    }
                    this.errorMessage = LanguageManager.translate("out_of_memory_profile_not_found", version);
                    break;
                }
                catch (Exception e2) {
                    e2.printStackTrace();
                    this.errorMessage = "ERROR: " + e2.getMessage();
                }
            }
        }
        super.actionPerformed(button);
    }

    private void killMinecraftLauncher() {
        try {
            Runtime runtime = Runtime.getRuntime();
            runtime.exec("taskkill /F /IM MinecraftLauncher.exe");
        }
        catch (Exception error) {
            error.printStackTrace();
        }
    }
}

