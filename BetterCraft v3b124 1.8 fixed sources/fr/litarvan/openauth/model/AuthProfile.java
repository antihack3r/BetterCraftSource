/*
 * Decompiled with CFR 0.152.
 */
package fr.litarvan.openauth.model;

public class AuthProfile {
    private String name;
    private String id;

    public AuthProfile() {
        this.name = "";
        this.id = "";
    }

    public AuthProfile(String name, String id2) {
        this.name = name;
        this.id = id2;
    }

    public String getName() {
        return this.name;
    }

    public String getId() {
        return this.id;
    }
}

