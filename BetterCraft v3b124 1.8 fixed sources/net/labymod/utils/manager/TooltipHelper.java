/*
 * Decompiled with CFR 0.152.
 */
package net.labymod.utils.manager;

import java.util.Arrays;
import java.util.List;
import me.nzxtercode.bettercraft.client.events.ClientTickEvent;
import net.labymod.main.LabyMod;
import net.labymod.utils.DrawUtils;
import net.lenni0451.eventapi.events.EventTarget;

public class TooltipHelper {
    private static final long HOLD_TIME = 1000L;
    private static TooltipHelper instance;
    private String[] runningTooltipArray;
    private int runningTooltipId = -1;
    private long runningTooltipDuration = -1L;
    private long keepAlive = -1L;
    private int mouseX = 0;
    private int mouseY = 0;

    public TooltipHelper() {
        instance = this;
    }

    public static TooltipHelper getHelper() {
        return instance;
    }

    public boolean pointTooltip(int mouseX, int mouseY, long customHoldTime, String[] lines) {
        this.mouseX = mouseX;
        this.mouseY = mouseY;
        if (!this.isRunning(lines) || customHoldTime == 0L) {
            this.runningTooltipDuration = System.currentTimeMillis() + customHoldTime;
            this.runningTooltipId = Arrays.deepHashCode(lines);
            this.runningTooltipArray = lines;
            if (customHoldTime != 0L) {
                return false;
            }
        }
        this.keepAlive = System.currentTimeMillis() + 100L;
        return System.currentTimeMillis() > this.runningTooltipDuration;
    }

    public boolean pointTooltip(int mouseX, int mouseY, long customHoldTime, String line) {
        DrawUtils draw = LabyMod.getInstance().getDrawUtils();
        List<String> list = draw.listFormattedStringToWidth(line, LabyMod.getInstance().getDrawUtils().getWidth() / 3);
        return this.pointTooltip(mouseX, mouseY, customHoldTime, (String[])list.toArray());
    }

    public boolean pointTooltip(int mouseX, int mouseY, String ... lines) {
        return lines.length == 1 ? this.pointTooltip(mouseX, mouseY, 1000L, lines[0]) : this.pointTooltip(mouseX, mouseY, 1000L, lines);
    }

    public boolean isRunning(String ... lines) {
        return this.runningTooltipId == Arrays.deepHashCode(lines);
    }

    @EventTarget
    public void handleEvent(ClientTickEvent event) {
        if (System.currentTimeMillis() < this.runningTooltipDuration) {
            return;
        }
        if (this.runningTooltipId == -1) {
            return;
        }
        if (System.currentTimeMillis() > this.keepAlive) {
            this.runningTooltipId = -1;
            this.runningTooltipDuration = -1L;
            this.runningTooltipArray = null;
            return;
        }
        DrawUtils utils = LabyMod.getInstance().getDrawUtils();
        utils.drawHoveringText(this.mouseX, this.mouseY, this.runningTooltipArray);
    }
}

