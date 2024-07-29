/*
 * Decompiled with CFR 0.152.
 */
package com.jagrosh.discordipc.mod.nzxter.exceptions;

public class NoDiscordClientException
extends Exception {
    private static final long serialVersionUID = 1L;

    public NoDiscordClientException() {
        super("No Valid Discord Client was found for this Instance");
    }
}

