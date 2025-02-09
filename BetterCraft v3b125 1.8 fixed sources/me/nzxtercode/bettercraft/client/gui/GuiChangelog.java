/*
 * Decompiled with CFR 0.152.
 */
package me.nzxtercode.bettercraft.client.gui;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;

public class GuiChangelog
extends GuiScreen {
    private GuiScreen parent;
    private List<String> lines;

    public GuiChangelog(GuiScreen parent) {
        this.parent = parent;
    }

    @Override
    public void initGui() {
        CompletableFuture.runAsync(() -> {
            this.lines = this.getStatus();
            List<String> list = this.lines;
        }, Executors.newSingleThreadExecutor());
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
        GuiChangelog.drawCenteredString(this.fontRendererObj, "Changelog/Info", width / 2, height / 2 - 85, -1);
        if (Objects.nonNull(this.lines)) {
            AtomicInteger y2 = new AtomicInteger();
            this.lines.forEach(line -> GuiChangelog.drawCenteredString(this.fontRendererObj, line, width / 2, height / 2 - 40 + y2.getAndAdd(this.mc.fontRendererObj.FONT_HEIGHT), -1));
        } else {
            GuiChangelog.drawCenteredString(this.fontRendererObj, "Loading...", width / 2, height / 2 - 40, -1);
        }
        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    public final List<String> getStatus() {
        AtomicReference returnValue = new AtomicReference();
        try {
            URL url = new URL("https://pastebin.com/raw/1wgquMNW");
            URLConnection connection = url.openConnection();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            returnValue.set(bufferedReader.lines().collect(Collectors.toList()));
            bufferedReader.close();
        }
        catch (IOException exception) {
            exception.printStackTrace();
        }
        return (List)returnValue.get();
    }
}

