/*
 * Decompiled with CFR 0.152.
 */
package net.labymod.labyplay.party;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonParser;
import java.net.ProtocolException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;
import me.nzxtercode.bettercraft.client.events.ClientTickEvent;
import net.labymod.labyconnect.packets.PacketAddonMessage;
import net.labymod.labyconnect.user.ChatUser;
import net.labymod.labyplay.gui.GuiPlayLayout;
import net.labymod.labyplay.party.PartyResponseHandler;
import net.labymod.labyplay.party.model.PartyActionTypes;
import net.labymod.labyplay.party.model.PartyInvite;
import net.labymod.labyplay.party.model.PartyListener;
import net.labymod.labyplay.party.model.PartyMember;
import net.labymod.labyplay.party.model.PartyMessage;
import net.labymod.main.LabyMod;
import net.labymod.support.util.Debug;
import net.labymod.utils.Consumer;
import net.lenni0451.eventapi.events.EventTarget;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;

public class PartySystem
implements Consumer<PacketAddonMessage> {
    public static final long TOTAL_INVITE_DURATION = 60000L;
    private final JsonParser jsonParser = new JsonParser();
    private final Gson gson = new Gson();
    private final PartyListener partyResponse = new PartyResponseHandler(this);
    private final List<PartyInvite> partyInvites;
    protected UUID partyId;
    protected PartyMember[] members = new PartyMember[0];
    protected PartyMember clientMember = null;
    protected ChatUser chatUserDummy;

    public PartySystem() {
        this.partyInvites = new ArrayList<PartyInvite>();
        LabyMod.getInstance().getEventManager().registerAddonMessage(this);
        LabyMod.getInstance().getLabyModAPI().registerForgeListener(this);
    }

    public boolean hasParty() {
        return this.partyId != null && this.members.length > 1;
    }

    @EventTarget
    public void handleEvent(ClientTickEvent event) {
        if (!LabyMod.getInstance().getLabyConnect().isOnline()) {
            this.partyId = null;
            this.clientMember = null;
            if (this.members == null || this.members.length != 0) {
                this.members = new PartyMember[0];
            }
            if (this.partyInvites == null || this.partyInvites.size() != 0) {
                assert (this.partyInvites != null);
                this.partyInvites.clear();
            }
        }
    }

    @Override
    public void accept(PacketAddonMessage packet) {
        if (!packet.getKey().equals("party")) {
            return;
        }
        String json = packet.getJson();
        System.out.println("[IN] " + json);
        PartyMessage partyMessage = this.gson.fromJson(json, PartyMessage.class);
        PartyActionTypes.Server actionType = PartyActionTypes.Server.getByKey(partyMessage.getAction());
        if (actionType == null) {
            Debug.log(Debug.EnumDebugMode.LABY_PLAY, "Unknown party action type by server: " + partyMessage.getAction());
            return;
        }
        try {
            switch (actionType) {
                case INVITED_PLAYER: {
                    String name = partyMessage.getString("name");
                    UUID party = partyMessage.getUUID("party");
                    String partyName = partyMessage.getString("partyName");
                    this.partyResponse.onInvitedPlayer(name, party, partyName);
                    break;
                }
                case INVITE_SUCCESS: {
                    String name = partyMessage.getString("name");
                    UUID uuid = partyMessage.getUUID("uuid");
                    this.partyResponse.onInviteSuccess(name, uuid);
                    break;
                }
                case CHAT: {
                    String sender = partyMessage.getString("sender");
                    String message = partyMessage.getString("message");
                    this.partyResponse.onChatMessage(sender, message);
                    break;
                }
                case SYSTEM_MESSAGE: {
                    PartyActionTypes.Message type = PartyActionTypes.Message.getByKey(partyMessage.getString("key"));
                    JsonArray jsonArray = partyMessage.getElement("args").getAsJsonArray();
                    String[] messageArgs = new String[jsonArray.size()];
                    int i2 = 0;
                    while (i2 < jsonArray.size()) {
                        messageArgs[i2] = jsonArray.get(i2).getAsString();
                        ++i2;
                    }
                    this.partyResponse.onSystemMessage(type, messageArgs);
                    break;
                }
                case YOU_LEFT: {
                    UUID partyUUID = partyMessage.getUUID("party");
                    this.partyResponse.onPartyLeft(partyUUID);
                    break;
                }
                case MEMBER_LIST: {
                    PartyMember[] currentPartyMembers = this.gson.fromJson(partyMessage.getElement("members"), PartyMember[].class);
                    UUID partyId = partyMessage.getUUID("uuid");
                    this.partyResponse.onMemberList(partyId, currentPartyMembers);
                }
            }
        }
        catch (ProtocolException exception) {
            exception.printStackTrace();
        }
    }

    public void invitePlayer(String target) {
        if (target.equalsIgnoreCase(LabyMod.getInstance().getPlayerName())) {
            return;
        }
        Iterator<PartyInvite> iterator = this.partyInvites.iterator();
        while (iterator.hasNext()) {
            PartyInvite party = iterator.next();
            if (!party.getUsername().equalsIgnoreCase(target)) continue;
            iterator.remove();
        }
        this.updatePartyGui();
        new PartyMessage.Builder(PartyActionTypes.Client.INVITE_PLAYER).putString("target", target).send();
    }

    public void sendInvitePlayerResponse(UUID uuid, boolean acceptInvite) {
        Iterator<PartyInvite> iterator = this.partyInvites.iterator();
        while (iterator.hasNext()) {
            PartyInvite party = iterator.next();
            if (!party.getPartyUUID().equals(uuid)) continue;
            iterator.remove();
        }
        this.updatePartyGui();
        new PartyMessage.Builder(PartyActionTypes.Client.INVITE_PLAYER_RESPONSE).putUUID("party", uuid).putBoolean("accepted", acceptInvite).send();
    }

    public void sendChatMessage(String message) {
        new PartyMessage.Builder(PartyActionTypes.Client.CHAT).putString("message", message).send();
    }

    public void leaveParty() {
        new PartyMessage.Builder(PartyActionTypes.Client.LEAVE_PARTY).send();
    }

    public void kickPlayer(UUID uuid) {
        new PartyMessage.Builder(PartyActionTypes.Client.KICK_PLAYER).putUUID("target", uuid).send();
    }

    public void changeOwner(UUID uuid) {
        new PartyMessage.Builder(PartyActionTypes.Client.CHANGE_OWNER).putUUID("new_owner", uuid).send();
    }

    protected void updatePartyGui() {
        GuiScreen currentScreen = Minecraft.getMinecraft().currentScreen;
        if (currentScreen != null && currentScreen instanceof GuiPlayLayout) {
            GuiPlayLayout partyLayout = (GuiPlayLayout)currentScreen;
            partyLayout.initLayout();
        }
    }

    public JsonParser getJsonParser() {
        return this.jsonParser;
    }

    public Gson getGson() {
        return this.gson;
    }

    public PartyListener getPartyResponse() {
        return this.partyResponse;
    }

    public UUID getPartyId() {
        return this.partyId;
    }

    public PartyMember[] getMembers() {
        return this.members;
    }

    public List<PartyInvite> getPartyInvites() {
        return this.partyInvites;
    }

    public PartyMember getClientMember() {
        return this.clientMember;
    }

    public ChatUser getChatUserDummy() {
        return this.chatUserDummy;
    }
}

