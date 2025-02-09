// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.util;

import javax.annotation.Nullable;
import java.util.Locale;
import com.google.common.collect.Maps;
import java.util.Map;
import java.util.UUID;
import com.mojang.util.UUIDTypeAdapter;
import com.mojang.authlib.GameProfile;

public class Session
{
    public String username;
    private final String playerID;
    public String token;
    private final Type sessionType;
    
    public Session(final String usernameIn, final String playerIDIn, final String tokenIn, final String sessionTypeIn) {
        this.username = usernameIn;
        this.playerID = playerIDIn;
        this.token = tokenIn;
        this.sessionType = Type.setSessionType(sessionTypeIn);
    }
    
    public String getSessionID() {
        return "token:" + this.token + ":" + this.playerID;
    }
    
    public String getPlayerID() {
        return this.playerID;
    }
    
    public String getUsername() {
        return this.username;
    }
    
    public String getToken() {
        return this.token;
    }
    
    public Type getSessionType() {
        return this.sessionType;
    }
    
    public GameProfile getProfile() {
        try {
            final UUID uuid = UUIDTypeAdapter.fromString(this.getPlayerID());
            return new GameProfile(uuid, this.getUsername());
        }
        catch (final IllegalArgumentException var2) {
            return new GameProfile(null, this.getUsername());
        }
    }
    
    public enum Type
    {
        LEGACY("LEGACY", 0, "legacy"), 
        MOJANG("MOJANG", 1, "mojang");
        
        private static final Map<String, Type> SESSION_TYPES;
        private final String sessionType;
        
        static {
            SESSION_TYPES = Maps.newHashMap();
            Type[] values;
            for (int length = (values = values()).length, i = 0; i < length; ++i) {
                final Type session$type = values[i];
                Type.SESSION_TYPES.put(session$type.sessionType, session$type);
            }
        }
        
        private Type(final String s, final int n, final String sessionTypeIn) {
            this.sessionType = sessionTypeIn;
        }
        
        @Nullable
        public static Type setSessionType(final String sessionTypeIn) {
            return Type.SESSION_TYPES.get(sessionTypeIn.toLowerCase(Locale.ROOT));
        }
    }
}
