// 
// Decompiled by Procyon v0.6.0
// 

package io.netty.handler.codec.mqtt;

import io.netty.util.internal.StringUtil;

public final class MqttConnectVariableHeader
{
    private final String name;
    private final int version;
    private final boolean hasUserName;
    private final boolean hasPassword;
    private final boolean isWillRetain;
    private final int willQos;
    private final boolean isWillFlag;
    private final boolean isCleanSession;
    private final int keepAliveTimeSeconds;
    
    public MqttConnectVariableHeader(final String name, final int version, final boolean hasUserName, final boolean hasPassword, final boolean isWillRetain, final int willQos, final boolean isWillFlag, final boolean isCleanSession, final int keepAliveTimeSeconds) {
        this.name = name;
        this.version = version;
        this.hasUserName = hasUserName;
        this.hasPassword = hasPassword;
        this.isWillRetain = isWillRetain;
        this.willQos = willQos;
        this.isWillFlag = isWillFlag;
        this.isCleanSession = isCleanSession;
        this.keepAliveTimeSeconds = keepAliveTimeSeconds;
    }
    
    public String name() {
        return this.name;
    }
    
    public int version() {
        return this.version;
    }
    
    public boolean hasUserName() {
        return this.hasUserName;
    }
    
    public boolean hasPassword() {
        return this.hasPassword;
    }
    
    public boolean isWillRetain() {
        return this.isWillRetain;
    }
    
    public int willQos() {
        return this.willQos;
    }
    
    public boolean isWillFlag() {
        return this.isWillFlag;
    }
    
    public boolean isCleanSession() {
        return this.isCleanSession;
    }
    
    public int keepAliveTimeSeconds() {
        return this.keepAliveTimeSeconds;
    }
    
    @Override
    public String toString() {
        return StringUtil.simpleClassName(this) + '[' + "name=" + this.name + ", version=" + this.version + ", hasUserName=" + this.hasUserName + ", hasPassword=" + this.hasPassword + ", isWillRetain=" + this.isWillRetain + ", isWillFlag=" + this.isWillFlag + ", isCleanSession=" + this.isCleanSession + ", keepAliveTimeSeconds=" + this.keepAliveTimeSeconds + ']';
    }
}
