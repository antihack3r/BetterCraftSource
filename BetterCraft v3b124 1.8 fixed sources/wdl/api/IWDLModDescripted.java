/*
 * Decompiled with CFR 0.152.
 */
package wdl.api;

import wdl.api.IWDLMod;

public interface IWDLModDescripted
extends IWDLMod {
    public String getDisplayName();

    public String getMainAuthor();

    public String[] getAuthors();

    public String getURL();

    public String getDescription();
}

