// 
// Decompiled by Procyon v0.6.0
// 

package me.nzxtercode.bettercraft.client.misc.altmanager.thealtening;

import com.google.gson.annotations.SerializedName;

public class AlteningAlt
{
    @SerializedName("token")
    private String token;
    @SerializedName("username")
    private String username;
    @SerializedName("expires")
    private String expiryDate;
    @SerializedName("limit")
    private boolean isLimitReached;
    @SerializedName("skin")
    private String skinHash;
    
    public String getToken() {
        return this.token;
    }
    
    public String getUsername() {
        return this.username;
    }
    
    public String getExpiryDate() {
        return this.expiryDate;
    }
    
    public boolean isLimitReached() {
        return this.isLimitReached;
    }
    
    public String getSkinHash() {
        return this.skinHash;
    }
}
