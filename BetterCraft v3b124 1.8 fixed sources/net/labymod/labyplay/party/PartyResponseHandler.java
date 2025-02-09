/*
 * Decompiled with CFR 0.152.
 */
package net.labymod.labyplay.party;

import java.util.Arrays;
import java.util.Comparator;
import java.util.Iterator;
import java.util.UUID;
import net.labymod.labyconnect.log.MessageChatComponent;
import net.labymod.labyconnect.log.SingleChat;
import net.labymod.labyconnect.user.ChatUser;
import net.labymod.labyconnect.user.UserStatus;
import net.labymod.labyplay.party.PartySystem;
import net.labymod.labyplay.party.model.PartyActionTypes;
import net.labymod.labyplay.party.model.PartyInvite;
import net.labymod.labyplay.party.model.PartyListener;
import net.labymod.labyplay.party.model.PartyMember;
import net.labymod.main.LabyMod;

public class PartyResponseHandler
implements PartyListener {
    private final Comparator<PartyMember> memberComparator = new Comparator<PartyMember>(){

        @Override
        public int compare(PartyMember a2, PartyMember b2) {
            return Boolean.compare(b2.isOwner(), a2.isOwner()) * 2 + Boolean.compare(b2.isMember(), a2.isMember());
        }
    };
    private PartySystem partySystem;

    public PartyResponseHandler(PartySystem partySystem) {
        this.partySystem = partySystem;
    }

    @Override
    public void onInvitedPlayer(String username, UUID party, String partyName) {
        System.out.println("invited by " + username + " to party of " + partyName);
        this.partySystem.getPartyInvites().add(new PartyInvite(username, party, partyName));
        this.partySystem.updatePartyGui();
    }

    @Override
    public void onInviteSuccess(String username, UUID uuid) {
    }

    @Override
    public void onChatMessage(String sender, String message) {
        System.out.println(String.valueOf(sender) + ": " + message);
        if (!sender.equals(LabyMod.getInstance().getPlayerName())) {
            SingleChat singleChat = LabyMod.getInstance().getLabyConnect().getChatlogManager().getChat(this.partySystem.chatUserDummy);
            singleChat.addMessage(new MessageChatComponent(sender, System.currentTimeMillis(), message));
        }
    }

    @Override
    public void onSystemMessage(PartyActionTypes.Message type, String[] args) {
        System.out.println(String.valueOf(type.getKey()) + " (" + Arrays.toString(args) + ")");
        LabyMod.getInstance().getGuiCustomAchievement().displayAchievement(type.getKey(), Arrays.toString(args));
    }

    @Override
    public void onPartyLeft(UUID party) {
        System.out.println("party left");
        if (this.partySystem.partyId != null && this.partySystem.partyId.equals(party)) {
            this.partySystem.partyId = null;
            this.partySystem.clientMember = null;
            this.partySystem.members = new PartyMember[0];
            Iterator<ChatUser> iterator = LabyMod.getInstance().getLabyConnect().getFriends().iterator();
            while (iterator.hasNext()) {
                if (!iterator.next().isParty()) continue;
                iterator.remove();
            }
        }
        this.partySystem.updatePartyGui();
    }

    @Override
    public void onMemberList(UUID partyId, PartyMember[] partyMembers) {
        System.out.println("Members:");
        UUID clientUUID = LabyMod.getInstance().getPlayerUUID();
        PartyMember[] partyMemberArray = partyMembers;
        int n2 = partyMembers.length;
        int n3 = 0;
        while (n3 < n2) {
            PartyMember member = partyMemberArray[n3];
            System.out.println("- " + member.getName());
            if (member.getUuid().equals(clientUUID)) {
                this.partySystem.clientMember = member;
            }
            ++n3;
        }
        Arrays.sort(partyMembers, this.memberComparator);
        Iterator<ChatUser> iterator = LabyMod.getInstance().getLabyConnect().getFriends().iterator();
        while (iterator.hasNext()) {
            if (!iterator.next().isParty()) continue;
            iterator.remove();
        }
        if (partyMembers.length > 1) {
            this.partySystem.chatUserDummy = new ChatUser(null, UserStatus.ONLINE, "", null, 0, System.currentTimeMillis(), 0L, "", System.currentTimeMillis(), System.currentTimeMillis(), partyMembers.length, true);
            LabyMod.getInstance().getLabyConnect().getFriends().add(this.partySystem.chatUserDummy);
            LabyMod.getInstance().getLabyConnect().sortFriendList(LabyMod.getSettings().friendSortType);
        }
        this.partySystem.partyId = partyId;
        this.partySystem.members = partyMembers;
        this.partySystem.updatePartyGui();
    }
}

