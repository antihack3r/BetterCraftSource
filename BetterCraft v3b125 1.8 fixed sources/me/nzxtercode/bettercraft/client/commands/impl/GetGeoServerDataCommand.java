/*
 * Decompiled with CFR 0.152.
 */
package me.nzxtercode.bettercraft.client.commands.impl;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import me.nzxtercode.bettercraft.client.BetterCraft;
import me.nzxtercode.bettercraft.client.commands.Command;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiNewChat;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;
import org.apache.commons.lang3.StringUtils;

public class GetGeoServerDataCommand
extends Command {
    public GetGeoServerDataCommand() {
        super("getgeo", "checkhost", "checkgeodata", "getgeodata", "checkip");
    }

    @Override
    public void run(String alias, String[] args) {
        CompletableFuture.runAsync(() -> {
            try {
                URL servergeodata = new URL("http://ip-api.com/json/" + Minecraft.getMinecraft().getCurrentServerData().serverIP + "?fields=status,message,continent,continentCode,country,countryCode,region,regionName,city,district,zgeodata,lat,lon,timezone,currency,isp,org,as,asname,reverse,mobile,proxy,query");
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(servergeodata.openStream()));
                String geodata = bufferedReader.readLine();
                JsonObject json = new JsonParser().parse(geodata).getAsJsonObject();
                json.entrySet().forEach(entry -> {
                    GuiNewChat guiNewChat = Minecraft.getMinecraft().ingameGUI.getChatGUI();
                    Object[] objectArray = new Object[4];
                    BetterCraft.getInstance();
                    objectArray[0] = BetterCraft.clientPrefix;
                    objectArray[1] = StringUtils.capitalize((String)entry.getKey());
                    objectArray[2] = EnumChatFormatting.RESET;
                    objectArray[3] = ((JsonElement)entry.getValue()).getAsString();
                    guiNewChat.printChatMessage(new ChatComponentText(String.format("%s %s: %s%s", objectArray)));
                });
            }
            catch (Exception exception) {
                // empty catch block
            }
        });
    }

    @Override
    public List<String> autocomplete(int arg2, String[] args) {
        return new ArrayList<String>();
    }
}

