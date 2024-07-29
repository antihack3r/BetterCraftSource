/*
 * Decompiled with CFR 0.152.
 */
package wdl;

import com.google.common.collect.ImmutableListMultimap;
import com.google.common.collect.LinkedListMultimap;
import com.google.common.collect.ListMultimap;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.Entity;
import net.minecraft.event.HoverEvent;
import net.minecraft.util.ChatComponentStyle;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.IChatComponent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import wdl.EntityUtils;
import wdl.MessageTypeCategory;
import wdl.WDL;
import wdl.api.IWDLMessageType;

public class WDLMessages {
    private static Logger logger = LogManager.getLogger();
    public static boolean enableAllMessages = false;
    private static List<MessageRegistration> registrations = new ArrayList<MessageRegistration>();

    private static MessageRegistration getRegistration(String name) {
        for (MessageRegistration r2 : registrations) {
            if (!r2.name.equals(name)) continue;
            return r2;
        }
        return null;
    }

    private static MessageRegistration getRegistration(IWDLMessageType type) {
        for (MessageRegistration r2 : registrations) {
            if (!r2.type.equals(type)) continue;
            return r2;
        }
        return null;
    }

    public static void registerMessage(String name, IWDLMessageType type, MessageTypeCategory category) {
        registrations.add(new MessageRegistration(name, type, category));
        WDL.defaultProps.setProperty("Messages." + name, Boolean.toString(type.isEnabledByDefault()));
        WDL.defaultProps.setProperty("MessageGroup." + category.internalName, "true");
    }

    public static boolean isEnabled(IWDLMessageType type) {
        if (type == null) {
            return false;
        }
        if (!enableAllMessages) {
            return false;
        }
        MessageRegistration r2 = WDLMessages.getRegistration(type);
        if (r2 == null) {
            return false;
        }
        if (!WDLMessages.isGroupEnabled(r2.category)) {
            return false;
        }
        if (!WDL.baseProps.containsKey("Messages." + r2.name)) {
            if (WDL.baseProps.containsKey("Debug." + r2.name)) {
                WDL.baseProps.put("Messages." + r2.name, WDL.baseProps.remove("Debug." + r2.name));
            } else {
                WDL.baseProps.setProperty("Messages." + r2.name, Boolean.toString(r2.type.isEnabledByDefault()));
            }
        }
        return WDL.baseProps.getProperty("Messages." + r2.name).equals("true");
    }

    public static void toggleEnabled(IWDLMessageType type) {
        MessageRegistration r2 = WDLMessages.getRegistration(type);
        if (r2 != null) {
            WDL.baseProps.setProperty("Messages." + r2.name, Boolean.toString(!WDLMessages.isEnabled(type)));
        }
    }

    public static boolean isGroupEnabled(MessageTypeCategory group) {
        if (!enableAllMessages) {
            return false;
        }
        return WDL.baseProps.getProperty("MessageGroup." + group.internalName, "true").equals("true");
    }

    public static void toggleGroupEnabled(MessageTypeCategory group) {
        WDL.baseProps.setProperty("MessageGroup." + group.internalName, Boolean.toString(!WDLMessages.isGroupEnabled(group)));
    }

    public static ListMultimap<MessageTypeCategory, IWDLMessageType> getTypes() {
        LinkedListMultimap<MessageTypeCategory, IWDLMessageType> returned = LinkedListMultimap.create();
        for (MessageRegistration r2 : registrations) {
            returned.put(r2.category, r2.type);
        }
        return ImmutableListMultimap.copyOf(returned);
    }

    public static void resetEnabledToDefaults() {
        WDL.baseProps.setProperty("Messages.enableAll", "true");
        enableAllMessages = WDL.globalProps.getProperty("Messages.enableAll", "true").equals("true");
        for (MessageRegistration r2 : registrations) {
            WDL.baseProps.setProperty("MessageGroup." + r2.category.internalName, WDL.globalProps.getProperty("MessageGroup." + r2.category.internalName, "true"));
            WDL.baseProps.setProperty("Messages." + r2.name, WDL.globalProps.getProperty("Messages." + r2.name));
        }
    }

    public static void onNewServer() {
        if (!WDL.baseProps.containsKey("Messages.enableAll")) {
            if (WDL.baseProps.containsKey("Debug.globalDebugEnabled")) {
                WDL.baseProps.put("Messages.enableAll", WDL.baseProps.remove("Debug.globalDebugEnabled"));
            } else {
                WDL.baseProps.setProperty("Messages.enableAll", WDL.globalProps.getProperty("Messages.enableAll", "true"));
            }
        }
        enableAllMessages = WDL.baseProps.getProperty("Messages.enableAll").equals("true");
    }

    public static void chatMessage(IWDLMessageType type, String message) {
        WDLMessages.chatMessage(type, new ChatComponentText(message));
    }

    public static void chatMessageTranslated(IWDLMessageType type, String translationKey, Object ... args) {
        ArrayList<Throwable> exceptionsToPrint = new ArrayList<Throwable>();
        int i2 = 0;
        while (i2 < args.length) {
            if (args[i2] instanceof Entity) {
                Entity e2 = (Entity)args[i2];
                String entityType = EntityUtils.getEntityType(e2);
                HoverEvent event = null;
                String customName = null;
                try {
                    event = e2.getDisplayName().getChatStyle().getChatHoverEvent();
                    if (e2.hasCustomName()) {
                        customName = e2.getCustomNameTag();
                    }
                }
                catch (Exception exception) {
                    // empty catch block
                }
                ChatComponentStyle component = customName != null ? new ChatComponentTranslation("wdl.messages.entityTypeAndCustomName", entityType, customName) : new ChatComponentText(entityType);
                component.setChatStyle(component.getChatStyle().setChatHoverEvent(event));
                args[i2] = component;
            } else if (args[i2] instanceof Throwable) {
                Throwable t2 = (Throwable)args[i2];
                ChatComponentText component = new ChatComponentText(t2.toString());
                StringWriter sw2 = new StringWriter();
                t2.printStackTrace(new PrintWriter(sw2));
                String exceptionAsString = sw2.toString();
                exceptionAsString = exceptionAsString.replace("\r", "").replace("\t", "    ");
                HoverEvent event = new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ChatComponentText(exceptionAsString));
                component.setChatStyle(component.getChatStyle().setChatHoverEvent(event));
                logger.warn(t2);
                args[i2] = component;
                exceptionsToPrint.add(t2);
            }
            ++i2;
        }
        WDLMessages.chatMessage(type, new ChatComponentTranslation(translationKey, args));
        i2 = 0;
        while (i2 < exceptionsToPrint.size()) {
            logger.warn("Exception #" + (i2 + 1) + ": ", (Throwable)exceptionsToPrint.get(i2));
            ++i2;
        }
    }

    public static void chatMessage(IWDLMessageType type, IChatComponent message) {
        String tooltipText = I18n.format("wdl.messages.tooltip", type.getDisplayName()).replace("\r", "");
        ChatComponentText tooltip = new ChatComponentText(tooltipText);
        ChatComponentText text = new ChatComponentText("");
        ChatComponentText header = new ChatComponentText("[WorldDL]");
        header.getChatStyle().setColor(type.getTitleColor());
        header.getChatStyle().setChatHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, tooltip));
        ChatComponentText messageFormat = new ChatComponentText(" ");
        messageFormat.getChatStyle().setColor(type.getTextColor());
        messageFormat.appendSibling(message);
        text.appendSibling(header);
        text.appendSibling(messageFormat);
        if (WDLMessages.isEnabled(type)) {
            Minecraft.getMinecraft().ingameGUI.getChatGUI().printChatMessage(text);
        } else {
            logger.info(text.getUnformattedText());
        }
    }

    private static class MessageRegistration {
        public final String name;
        public final IWDLMessageType type;
        public final MessageTypeCategory category;

        public MessageRegistration(String name, IWDLMessageType type, MessageTypeCategory category) {
            this.name = name;
            this.type = type;
            this.category = category;
        }

        public String toString() {
            return "MessageRegistration [name=" + this.name + ", type=" + this.type + ", category=" + this.category + "]";
        }

        public int hashCode() {
            int prime = 31;
            int result = 1;
            result = 31 * result + (this.name == null ? 0 : this.name.hashCode());
            result = 31 * result + (this.category == null ? 0 : this.category.hashCode());
            result = 31 * result + (this.type == null ? 0 : this.type.hashCode());
            return result;
        }

        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null) {
                return false;
            }
            if (!(obj instanceof MessageRegistration)) {
                return false;
            }
            MessageRegistration other = (MessageRegistration)obj;
            if (this.name == null ? other.name != null : !this.name.equals(other.name)) {
                return false;
            }
            if (this.category == null ? other.category != null : !this.category.equals(other.category)) {
                return false;
            }
            return !(this.type == null ? other.type != null : !this.type.equals(other.type));
        }
    }
}

