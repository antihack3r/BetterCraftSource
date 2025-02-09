/*
 * Decompiled with CFR 0.152.
 */
package com.jagrosh.discordipc.mod.nzxter.entities;

import com.jagrosh.discordipc.mod.nzxter.impl.ExtendedLong;

public class User {
    private final String username;
    private final String nickname;
    private final String discriminator;
    private final long id;
    private final String avatar;

    public User(String username, String nickname, String discriminator, long id2, String avatar) {
        this.username = username;
        this.nickname = nickname;
        this.discriminator = discriminator;
        this.id = id2;
        this.avatar = avatar;
    }

    public String getName() {
        return this.username;
    }

    public String getNickname() {
        return this.nickname;
    }

    public String getEffectiveName() {
        return this.nickname == null ? this.username : this.nickname;
    }

    public String getDiscriminator() {
        return this.discriminator;
    }

    public long getIdLong() {
        return this.id;
    }

    public String getId() {
        return Long.toString(this.id);
    }

    public String getAvatarId() {
        return this.avatar;
    }

    public String getAvatarUrl() {
        return this.getAvatarId() == null ? null : "https://cdn.discordapp.com/avatars/" + this.getId() + "/" + this.getAvatarId() + (this.getAvatarId().startsWith("a_") ? ".gif" : ".png");
    }

    public String getDefaultAvatarId() {
        return DefaultAvatar.values()[(this.getDiscriminator().equals("0") ? (int)this.getIdLong() >> 22 : Integer.parseInt(this.getDiscriminator())) % DefaultAvatar.values().length].toString();
    }

    public String getDefaultAvatarUrl() {
        return "https://discord.com/assets/" + this.getDefaultAvatarId() + ".png";
    }

    public String getEffectiveAvatarUrl() {
        return this.getAvatarUrl() == null ? this.getDefaultAvatarUrl() : this.getAvatarUrl();
    }

    public boolean isBot() {
        return false;
    }

    public String getAsMention() {
        return "<@" + this.id + '>';
    }

    public boolean equals(Object o2) {
        if (!(o2 instanceof User)) {
            return false;
        }
        User oUser = (User)o2;
        return this == oUser || this.id == oUser.id;
    }

    public int hashCode() {
        return ExtendedLong.hashCode(this.id);
    }

    public String toString() {
        return "U:" + this.getName() + '(' + this.id + ')';
    }

    public static enum DefaultAvatar {
        BLURPLE("6debd47ed13483642cf09e832ed0bc1b"),
        GREY("322c936a8c8be1b803cd94861bdfa868"),
        GREEN("dd4dbc0016779df1378e7812eabaa04d"),
        ORANGE("0e291f67c9274a1abdddeb3fd919cbaa"),
        RED("1cbd08c76f8af6dddce02c5138971129");

        private final String text;

        private DefaultAvatar(String text) {
            this.text = text;
        }

        public String toString() {
            return this.text;
        }
    }
}

