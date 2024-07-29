/*
 * Decompiled with CFR 0.152.
 */
package org.jibble.pircbot;

import org.jibble.pircbot.IrcException;

public class NickAlreadyInUseException
extends IrcException {
    public NickAlreadyInUseException(String string) {
        super(string);
    }
}

