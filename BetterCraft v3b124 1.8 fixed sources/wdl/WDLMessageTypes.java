/*
 * Decompiled with CFR 0.152.
 */
package wdl;

import net.minecraft.client.resources.I18n;
import net.minecraft.util.EnumChatFormatting;
import wdl.MessageTypeCategory;
import wdl.WDLMessages;
import wdl.api.IWDLMessageType;

public enum WDLMessageTypes implements IWDLMessageType
{
    INFO("wdl.messages.message.info", EnumChatFormatting.RED, EnumChatFormatting.GOLD, true, MessageTypeCategory.CORE_RECOMMENDED),
    ERROR("wdl.messages.message.error", EnumChatFormatting.DARK_GREEN, EnumChatFormatting.DARK_RED, true, MessageTypeCategory.CORE_RECOMMENDED),
    UPDATES("wdl.messages.message.updates", EnumChatFormatting.RED, EnumChatFormatting.GOLD, true, MessageTypeCategory.CORE_RECOMMENDED),
    LOAD_TILE_ENTITY("wdl.messages.message.loadingTileEntity", false),
    ON_WORLD_LOAD("wdl.messages.message.onWorldLoad", false),
    ON_BLOCK_EVENT("wdl.messages.message.blockEvent", true),
    ON_MAP_SAVED("wdl.messages.message.mapDataSaved", false),
    ON_CHUNK_NO_LONGER_NEEDED("wdl.messages.message.chunkUnloaded", false),
    ON_GUI_CLOSED_INFO("wdl.messages.message.guiClosedInfo", true),
    ON_GUI_CLOSED_WARNING("wdl.messages.message.guiClosedWarning", true),
    SAVING("wdl.messages.message.saving", true),
    REMOVE_ENTITY("wdl.messages.message.removeEntity", false),
    PLUGIN_CHANNEL_MESSAGE("wdl.messages.message.pluginChannel", false),
    UPDATE_DEBUG("wdl.messages.message.updateDebug", false);

    private final String displayTextKey;
    private final EnumChatFormatting titleColor;
    private final EnumChatFormatting textColor;
    private final String descriptionKey;
    private final boolean enabledByDefault;

    private WDLMessageTypes(String i18nKey, boolean enabledByDefault) {
        this(i18nKey, EnumChatFormatting.DARK_GREEN, EnumChatFormatting.GOLD, enabledByDefault, MessageTypeCategory.CORE_DEBUG);
    }

    private WDLMessageTypes(String i18nKey, EnumChatFormatting titleColor, EnumChatFormatting textColor, boolean enabledByDefault, MessageTypeCategory category) {
        this.displayTextKey = String.valueOf(i18nKey) + ".text";
        this.titleColor = titleColor;
        this.textColor = textColor;
        this.descriptionKey = String.valueOf(i18nKey) + ".description";
        this.enabledByDefault = enabledByDefault;
        WDLMessages.registerMessage(this.name(), this, category);
    }

    @Override
    public String getDisplayName() {
        return I18n.format(this.displayTextKey, new Object[0]);
    }

    @Override
    public EnumChatFormatting getTitleColor() {
        return this.titleColor;
    }

    @Override
    public EnumChatFormatting getTextColor() {
        return this.textColor;
    }

    @Override
    public String getDescription() {
        return I18n.format(this.descriptionKey, new Object[0]);
    }

    @Override
    public boolean isEnabledByDefault() {
        return this.enabledByDefault;
    }
}

