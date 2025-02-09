// 
// Decompiled by Procyon v0.6.0
// 

package io.netty.handler.codec.mqtt;

import io.netty.handler.codec.DecoderException;

final class MqttCodecUtil
{
    private static final char[] TOPIC_WILDCARDS;
    private static final int MIN_CLIENT_ID_LENGTH = 1;
    private static final int MAX_CLIENT_ID_LENGTH = 23;
    
    static boolean isValidPublishTopicName(final String topicName) {
        for (final char c : MqttCodecUtil.TOPIC_WILDCARDS) {
            if (topicName.indexOf(c) >= 0) {
                return false;
            }
        }
        return true;
    }
    
    static boolean isValidMessageId(final int messageId) {
        return messageId != 0;
    }
    
    static boolean isValidClientId(final MqttVersion mqttVersion, final String clientId) {
        if (mqttVersion == MqttVersion.MQTT_3_1) {
            return clientId != null && clientId.length() >= 1 && clientId.length() <= 23;
        }
        if (mqttVersion == MqttVersion.MQTT_3_1_1) {
            return clientId != null;
        }
        throw new IllegalArgumentException(mqttVersion + " is unknown mqtt version");
    }
    
    static MqttFixedHeader validateFixedHeader(final MqttFixedHeader mqttFixedHeader) {
        switch (mqttFixedHeader.messageType()) {
            case PUBREL:
            case SUBSCRIBE:
            case UNSUBSCRIBE: {
                if (mqttFixedHeader.qosLevel() != MqttQoS.AT_LEAST_ONCE) {
                    throw new DecoderException(mqttFixedHeader.messageType().name() + " message must have QoS 1");
                }
                break;
            }
        }
        return mqttFixedHeader;
    }
    
    static MqttFixedHeader resetUnusedFields(final MqttFixedHeader mqttFixedHeader) {
        switch (mqttFixedHeader.messageType()) {
            case CONNECT:
            case CONNACK:
            case PUBACK:
            case PUBREC:
            case PUBCOMP:
            case SUBACK:
            case UNSUBACK:
            case PINGREQ:
            case PINGRESP:
            case DISCONNECT: {
                if (mqttFixedHeader.isDup() || mqttFixedHeader.qosLevel() != MqttQoS.AT_MOST_ONCE || mqttFixedHeader.isRetain()) {
                    return new MqttFixedHeader(mqttFixedHeader.messageType(), false, MqttQoS.AT_MOST_ONCE, false, mqttFixedHeader.remainingLength());
                }
                return mqttFixedHeader;
            }
            case PUBREL:
            case SUBSCRIBE:
            case UNSUBSCRIBE: {
                if (mqttFixedHeader.isRetain()) {
                    return new MqttFixedHeader(mqttFixedHeader.messageType(), mqttFixedHeader.isDup(), mqttFixedHeader.qosLevel(), false, mqttFixedHeader.remainingLength());
                }
                return mqttFixedHeader;
            }
            default: {
                return mqttFixedHeader;
            }
        }
    }
    
    private MqttCodecUtil() {
    }
    
    static {
        TOPIC_WILDCARDS = new char[] { '#', '+' };
    }
}
