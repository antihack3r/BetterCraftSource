/*
 * Decompiled with CFR 0.152.
 */
package me.nzxtercode.bettercraft.client.gui.section;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import me.nzxtercode.bettercraft.client.Config;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.util.EnumChatFormatting;
import org.lwjgl.input.Keyboard;

public class GuiUISettings
extends GuiScreen {
    private GuiScreen parent;
    public final Map<String, List<String>> sectionList = Maps.newConcurrentMap();
    public static boolean[] enabledBackgrounds = GuiUISettings.getEnabledBackgrounds();
    public static boolean[] enabledAnimations = GuiUISettings.getEnabledAnimations();
    public static boolean[] enabledItems = GuiUISettings.getEnabledItems();
    public static boolean[] enabledUI = GuiUISettings.getEnabledUI();

    public GuiUISettings(GuiScreen parent) {
        this.parent = parent;
    }

    private static boolean[] getEnabledBackgrounds() {
        boolean[] array = new boolean[]{Config.getInstance().getSettings("Section").get("list").getAsJsonObject().get("Background").getAsJsonObject().get("Tab").getAsBoolean(), Config.getInstance().getSettings("Section").get("list").getAsJsonObject().get("Background").getAsJsonObject().get("Scoreboard").getAsBoolean(), Config.getInstance().getSettings("Section").get("list").getAsJsonObject().get("Background").getAsJsonObject().get("Chat").getAsBoolean(), Config.getInstance().getSettings("Section").get("list").getAsJsonObject().get("Background").getAsJsonObject().get("Particel").getAsBoolean()};
        return array;
    }

    private static boolean[] getEnabledAnimations() {
        boolean[] array = new boolean[]{Config.getInstance().getSettings("Section").get("list").getAsJsonObject().get("Animation").getAsJsonObject().get("Button").getAsBoolean(), Config.getInstance().getSettings("Section").get("list").getAsJsonObject().get("Animation").getAsJsonObject().get("Chat").getAsBoolean(), Config.getInstance().getSettings("Section").get("list").getAsJsonObject().get("Animation").getAsJsonObject().get("Disconnect").getAsBoolean()};
        return array;
    }

    private static boolean[] getEnabledItems() {
        boolean[] array = new boolean[]{Config.getInstance().getSettings("Section").get("list").getAsJsonObject().get("Items").getAsJsonObject().get("Physic").getAsBoolean()};
        return array;
    }

    private static boolean[] getEnabledUI() {
        boolean[] array = new boolean[]{Config.getInstance().getSettings("Section").get("list").getAsJsonObject().get("UI").getAsJsonObject().get("Hotbar").getAsBoolean(), Config.getInstance().getSettings("Section").get("list").getAsJsonObject().get("UI").getAsJsonObject().get("BlockOverlay").getAsBoolean(), Config.getInstance().getSettings("Section").get("list").getAsJsonObject().get("UI").getAsJsonObject().get("TransButton").getAsBoolean()};
        return array;
    }

    @Override
    public void initGui() {
        Keyboard.enableRepeatEvents(true);
        this.sectionList.clear();
        this.buttonList.add(new GuiButton(0, width - 93, height - 23, 90, 20, "Back"));
        AtomicInteger xPosition = new AtomicInteger(25);
        Config.getInstance().getSettings("Section").get("list").getAsJsonObject().entrySet().forEach(entry -> {
            String name = (String)entry.getKey();
            AtomicInteger yPosition = new AtomicInteger(50);
            ((JsonElement)entry.getValue()).getAsJsonObject().entrySet().forEach(elementEntry -> {
                this.sectionList.computeIfAbsent(name, elements -> Lists.newArrayList()).add((String)elementEntry.getKey());
                this.buttonList.add(new GuiButton(xPosition.get() - 24, xPosition.get() + 10, yPosition.getAndAdd(25), 80, 20, String.valueOf(((JsonElement)elementEntry.getValue()).getAsBoolean() ? EnumChatFormatting.GREEN.toString() : EnumChatFormatting.RED.toString()) + (String)elementEntry.getKey()));
            });
            xPosition.getAndAdd(105);
        });
    }

    @Override
    protected void actionPerformed(GuiButton button) throws IOException {
        if (button.id == 0) {
            this.mc.displayGuiScreen(this.parent);
        } else {
            int index = button.id / 105;
            String name = String.valueOf(this.sectionList.keySet().toArray()[index]);
            String element = button.displayString.substring(2);
            boolean value = Config.getInstance().getSettings("Section").get("list").getAsJsonObject().get(name).getAsJsonObject().get(element).getAsBoolean();
            Config.getInstance().editSettings("Section", json -> {
                JsonObject sectionJson;
                JsonObject jsonObject = json.get("list").getAsJsonObject();
                sectionJson.add(element, new JsonPrimitive(!(sectionJson = jsonObject.get(name).getAsJsonObject()).get(element).getAsBoolean()));
                jsonObject.add(name, sectionJson);
                json.add("list", jsonObject);
            });
            this.buttonList.forEach(buttonObj -> {
                if (buttonObj.id != 0) {
                    int indexObj = buttonObj.id / 105;
                    String nameObj = String.valueOf(this.sectionList.keySet().toArray()[indexObj]);
                    String elementObj = buttonObj.displayString.substring(2);
                    boolean valueObj = Config.getInstance().getSettings("Section").get("list").getAsJsonObject().get(nameObj).getAsJsonObject().get(elementObj).getAsBoolean();
                    buttonObj.displayString = String.valueOf(valueObj ? EnumChatFormatting.GREEN.toString() : EnumChatFormatting.RED.toString()) + elementObj;
                }
            });
        }
        enabledBackgrounds = GuiUISettings.getEnabledBackgrounds();
        enabledAnimations = GuiUISettings.getEnabledAnimations();
        enabledItems = GuiUISettings.getEnabledItems();
        enabledUI = GuiUISettings.getEnabledUI();
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        this.drawDefaultBackground();
        AtomicInteger xPosition = new AtomicInteger(25);
        this.sectionList.forEach((name, elements) -> {
            int x2 = xPosition.getAndAdd(105);
            Gui.drawRect(x2, 25, x2 + 100, height - 25, Integer.MIN_VALUE);
            GuiUISettings.drawCenteredString(this.mc.fontRendererObj, name, x2 + 50, 30, -1);
        });
        super.drawScreen(mouseX, mouseY, partialTicks);
    }
}

