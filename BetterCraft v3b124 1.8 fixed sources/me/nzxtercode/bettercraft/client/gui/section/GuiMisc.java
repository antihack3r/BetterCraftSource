/*
 * Decompiled with CFR 0.152.
 */
package me.nzxtercode.bettercraft.client.gui.section;

import com.google.common.collect.Lists;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.TreeMap;
import java.util.concurrent.atomic.AtomicInteger;
import me.nzxtercode.bettercraft.client.Config;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.util.EnumChatFormatting;
import org.lwjgl.input.Keyboard;

public class GuiMisc
extends GuiScreen {
    private GuiScreen parent;
    public final TreeMap<String, List<String>> sectionList = new TreeMap(Collections.reverseOrder());
    public static boolean[] enabledMisc = GuiMisc.getMisc();
    public static boolean[] enabledESP = GuiMisc.getESP();

    public GuiMisc(GuiScreen parent) {
        this.parent = parent;
    }

    private static boolean[] getMisc() {
        boolean[] array = new boolean[]{Config.getInstance().getMisc("Section").get("list").getAsJsonObject().get("Misc").getAsJsonObject().get("ChunkAnim").getAsBoolean(), Config.getInstance().getMisc("Section").get("list").getAsJsonObject().get("Misc").getAsJsonObject().get("FBP").getAsBoolean()};
        return array;
    }

    private static boolean[] getESP() {
        boolean[] array = new boolean[]{Config.getInstance().getMisc("Section").get("list").getAsJsonObject().get("ESP").getAsJsonObject().get("Player").getAsBoolean(), Config.getInstance().getMisc("Section").get("list").getAsJsonObject().get("ESP").getAsJsonObject().get("Mobs").getAsBoolean(), Config.getInstance().getMisc("Section").get("list").getAsJsonObject().get("ESP").getAsJsonObject().get("Chest").getAsBoolean(), Config.getInstance().getMisc("Section").get("list").getAsJsonObject().get("ESP").getAsJsonObject().get("Enderchest").getAsBoolean()};
        return array;
    }

    @Override
    public void initGui() {
        Keyboard.enableRepeatEvents(true);
        this.sectionList.clear();
        this.buttonList.add(new GuiButton(0, width - 93, height - 23, 90, 20, "Back"));
        AtomicInteger xPosition = new AtomicInteger(25);
        Config.getInstance().getMisc("Section").get("list").getAsJsonObject().entrySet().forEach(entry -> {
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
            boolean value = Config.getInstance().getMisc("Section").get("list").getAsJsonObject().get(name).getAsJsonObject().get(element).getAsBoolean();
            Config.getInstance().editMisc("Section", json -> {
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
                    boolean valueObj = Config.getInstance().getMisc("Section").get("list").getAsJsonObject().get(nameObj).getAsJsonObject().get(elementObj).getAsBoolean();
                    buttonObj.displayString = String.valueOf(valueObj ? EnumChatFormatting.GREEN.toString() : EnumChatFormatting.RED.toString()) + elementObj;
                }
            });
        }
        enabledMisc = GuiMisc.getMisc();
        enabledESP = GuiMisc.getESP();
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        this.drawDefaultBackground();
        AtomicInteger xPosition = new AtomicInteger(25);
        this.sectionList.forEach((name, elements) -> {
            int x2 = xPosition.getAndAdd(105);
            Gui.drawRect(x2, 25, x2 + 100, height - 25, Integer.MIN_VALUE);
            GuiMisc.drawCenteredString(this.mc.fontRendererObj, name, x2 + 50, 30, -1);
        });
        super.drawScreen(mouseX, mouseY, partialTicks);
    }
}

