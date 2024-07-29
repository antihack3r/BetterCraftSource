/*
 * Decompiled with CFR 0.152.
 */
package net.labymod.user.group;

import com.google.gson.annotations.SerializedName;
import java.awt.Color;
import java.beans.ConstructorProperties;
import net.labymod.main.LabyMod;
import net.labymod.main.ModTextures;
import net.labymod.user.group.EnumGroupDisplayType;
import net.labymod.utils.ModColor;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;

public class LabyGroup {
    private int id;
    private String name;
    @SerializedName(value="nice_name")
    private String displayName;
    @SerializedName(value="color_hex")
    private String colorHex;
    @SerializedName(value="color_minecraft")
    private char colorMinecraft;
    @SerializedName(value="tag_name")
    private String tagName;
    @SerializedName(value="display_type")
    private String displayTypeString;
    private Color color;
    private EnumGroupDisplayType displayType;

    protected LabyGroup init() {
        try {
            EnumGroupDisplayType type = EnumGroupDisplayType.valueOf(this.displayTypeString);
            this.displayType = type == null ? EnumGroupDisplayType.NONE : type;
        }
        catch (Exception error) {
            error.printStackTrace();
            this.displayType = EnumGroupDisplayType.NONE;
        }
        try {
            if (this.colorHex != null && !this.colorHex.isEmpty()) {
                this.color = Color.decode("#" + this.colorHex);
            }
        }
        catch (Exception error) {
            error.printStackTrace();
        }
        return this;
    }

    public String getDisplayTag() {
        return String.valueOf(ModColor.cl("f")) + ModColor.cl("l") + "LABYMOD " + '\u00a7' + this.colorMinecraft + this.tagName;
    }

    public void renderBadge(double x2, double y2, double width, double height, boolean small) {
        boolean familiar;
        boolean bl2 = familiar = this.color == null;
        if (!familiar) {
            GlStateManager.color((float)this.color.getRed() / 255.0f, (float)this.color.getGreen() / 255.0f, (float)this.color.getBlue() / 255.0f);
        }
        ResourceLocation texture = familiar ? (small ? ModTextures.BADGE_FAMILIAR_SMALL : ModTextures.BADGE_FAMILIAR) : (small ? ModTextures.BADGE_GROUP_SMALL : ModTextures.BADGE_GROUP);
        GlStateManager.enableBlend();
        Minecraft.getMinecraft().getTextureManager().bindTexture(texture);
        LabyMod.getInstance().getDrawUtils().drawTexture(x2, y2, 255.0, 255.0, 8.0, 8.0, 1.1f);
        GlStateManager.color(1.0f, 1.0f, 1.0f);
    }

    public LabyGroup() {
    }

    @ConstructorProperties(value={"id", "name", "displayName", "colorHex", "colorMinecraft", "tagName", "displayTypeString", "color", "displayType"})
    public LabyGroup(int id2, String name, String displayName, String colorHex, char colorMinecraft, String tagName, String displayTypeString, Color color, EnumGroupDisplayType displayType) {
        this.id = id2;
        this.name = name;
        this.displayName = displayName;
        this.colorHex = colorHex;
        this.colorMinecraft = colorMinecraft;
        this.tagName = tagName;
        this.displayTypeString = displayTypeString;
        this.color = color;
        this.displayType = displayType;
    }

    public int getId() {
        return this.id;
    }

    public String getName() {
        return this.name;
    }

    public String getDisplayName() {
        return this.displayName;
    }

    public char getColorMinecraft() {
        return this.colorMinecraft;
    }

    public Color getColor() {
        return this.color;
    }

    public EnumGroupDisplayType getDisplayType() {
        return this.displayType;
    }
}

