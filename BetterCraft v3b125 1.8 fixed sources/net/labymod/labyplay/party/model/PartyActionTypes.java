/*
 * Decompiled with CFR 0.152.
 */
package net.labymod.labyplay.party.model;

public final class PartyActionTypes {

    public static enum Client {
        INVITE_PLAYER,
        INVITE_PLAYER_RESPONSE,
        CHAT,
        LEAVE_PARTY,
        KICK_PLAYER,
        CHANGE_OWNER;


        public String getKey() {
            return this.name().toLowerCase();
        }

        public static Client getByKey(String key) {
            Client[] clientArray = Client.values();
            int n2 = clientArray.length;
            int n3 = 0;
            while (n3 < n2) {
                Client type = clientArray[n3];
                if (type.getKey().equalsIgnoreCase(key)) {
                    return type;
                }
                ++n3;
            }
            return null;
        }
    }

    public static enum Message {
        UNKNOWN,
        PARTY_FULL,
        PLAYER_NOT_FOUND,
        PLAYER_IN_OTHER_PARTY,
        PLAYER_INVITED,
        PLAYER_LEFT,
        PLAYER_JOINED,
        PLAYER_INVITE_REJECTED,
        PLAYER_KICKED,
        NEW_OWNER_NOT_IN_PARTY,
        YOU_ARE_NOT_THE_OWNER,
        PARTY_DOES_NOT_EXIST,
        NO_INVITE;


        public String getKey() {
            return this.name().toLowerCase();
        }

        public static Message getByKey(String key) {
            Message[] messageArray = Message.values();
            int n2 = messageArray.length;
            int n3 = 0;
            while (n3 < n2) {
                Message type = messageArray[n3];
                if (type.getKey().equalsIgnoreCase(key)) {
                    return type;
                }
                ++n3;
            }
            return UNKNOWN;
        }
    }

    public static enum Server {
        INVITED_PLAYER,
        INVITE_SUCCESS,
        CHAT,
        SYSTEM_MESSAGE,
        YOU_LEFT,
        MEMBER_LIST;


        public String getKey() {
            return this.name().toLowerCase();
        }

        public static Server getByKey(String key) {
            Server[] serverArray = Server.values();
            int n2 = serverArray.length;
            int n3 = 0;
            while (n3 < n2) {
                Server type = serverArray[n3];
                if (type.getKey().equalsIgnoreCase(key)) {
                    return type;
                }
                ++n3;
            }
            return null;
        }
    }
}

