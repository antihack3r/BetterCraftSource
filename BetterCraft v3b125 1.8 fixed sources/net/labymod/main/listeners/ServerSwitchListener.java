/*
 * Decompiled with CFR 0.152.
 */
package net.labymod.main.listeners;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.util.Arrays;
import java.util.List;
import net.labymod.api.events.MessageSendEvent;
import net.labymod.api.events.ServerMessageEvent;
import net.labymod.gui.GuiSwitchServer;
import net.labymod.main.LabyMod;
import net.labymod.support.util.Debug;
import net.labymod.utils.Consumer;
import net.labymod.utils.ModUtils;
import net.labymod.utils.ServerData;
import net.minecraft.client.Minecraft;

public class ServerSwitchListener
implements ServerMessageEvent,
Consumer<ServerData>,
GuiSwitchServer.Result,
MessageSendEvent {
    private LabyMod labymod;
    private String destinationAddressToTrust;
    private boolean sessionTrusted = false;

    public ServerSwitchListener(LabyMod labymod) {
        this.labymod = labymod;
    }

    @Override
    public void onServerMessage(String messageKey, JsonElement serverMessage) {
        JsonObject obj;
        if (messageKey.equals("server_switch") && (obj = serverMessage.getAsJsonObject()).has("title") && obj.has("address")) {
            boolean preview;
            final String title = obj.get("title").getAsString();
            final String address = obj.get("address").getAsString();
            boolean bl2 = preview = obj.has("preview") && obj.get("preview").getAsBoolean();
            if (this.labymod.getCurrentServerData() != null) {
                if (this.sessionTrusted) {
                    this.notify(address, true, true);
                    this.labymod.switchServer(address, true);
                    return;
                }
                List<String> list = Arrays.asList(LabyMod.getSettings().trustedServers);
                if (list.contains(ModUtils.getProfileNameByIp(this.labymod.getCurrentServerData().getIp()))) {
                    this.notify(address, true, true);
                    this.labymod.switchServer(address, true);
                    return;
                }
            }
            Minecraft.getMinecraft().addScheduledTask(new Runnable(){

                @Override
                public void run() {
                    Minecraft.getMinecraft().displayGuiScreen(new GuiSwitchServer(title, address, preview, ServerSwitchListener.this));
                }
            });
        }
    }

    @Override
    public void notify(String address, boolean accepted, boolean trusted) {
        if (accepted) {
            this.destinationAddressToTrust = address;
        }
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("address", address);
        jsonObject.addProperty("accepted", accepted);
        this.labymod.getLabyModAPI().sendJsonMessageToServer("server_switch", jsonObject);
    }

    @Override
    public void accept(ServerData accepted) {
        if (accepted == null) {
            return;
        }
        if (this.destinationAddressToTrust != null && accepted.getIp().equalsIgnoreCase(this.destinationAddressToTrust)) {
            this.sessionTrusted = true;
        } else {
            this.sessionTrusted = false;
            this.destinationAddressToTrust = null;
        }
    }

    @Override
    public boolean onSend(String msg) {
        if (Debug.isActive() && msg.startsWith("/connect") && msg.contains(" ")) {
            String ip2 = msg.split(" ")[1];
            JsonObject object = new JsonObject();
            String s2 = " ";
            new Thread(() -> {
                try {
                    Thread.sleep(10L);
                    Minecraft.getMinecraft().addScheduledTask(() -> {
                        object.addProperty("title", "LabyMod Server Switcher");
                        object.addProperty("address", " ");
                        object.addProperty("preview", true);
                        this.onServerMessage("server_switch", object);
                    });
                }
                catch (Exception e2) {
                    e2.printStackTrace();
                }
            }).start();
            return true;
        }
        return false;
    }
}

