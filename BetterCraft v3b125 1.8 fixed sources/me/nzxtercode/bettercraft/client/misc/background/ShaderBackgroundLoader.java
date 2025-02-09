/*
 * Decompiled with CFR 0.152.
 */
package me.nzxtercode.bettercraft.client.misc.background;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.google.gson.JsonPrimitive;
import java.util.AbstractMap;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import me.nzxtercode.bettercraft.client.Config;
import me.nzxtercode.bettercraft.client.misc.background.ShaderList1;
import me.nzxtercode.bettercraft.client.misc.background.ShaderList2;
import me.nzxtercode.bettercraft.client.misc.background.ShaderUniform;
import net.minecraft.client.renderer.texture.DynamicTexture;

public class ShaderBackgroundLoader {
    private static ShaderBackgroundLoader loader = new ShaderBackgroundLoader();
    private ShaderUniform shaderUniform;
    private boolean isEnabledShader;
    private int currentShaderId;
    private final List<Map.Entry<String, String>> shaderList;
    private final String Custom = Config.getInstance().getBackground("Shader").get("custom").getAsString();
    private boolean isPEnabledBackground;
    private boolean isCEnabledBackground;
    private final Map<String, DynamicTexture> imageCache = new ConcurrentHashMap<String, DynamicTexture>();

    public static ShaderBackgroundLoader getLoader() {
        return loader;
    }

    public ShaderBackgroundLoader() {
        this.shaderList = Lists.newCopyOnWriteArrayList();
        Stream.of(Arrays.asList(this.getClass().getDeclaredFields()), Arrays.asList(ShaderList1.class.getDeclaredFields()), Arrays.asList(ShaderList2.class.getDeclaredFields())).flatMap(list -> list.stream()).collect(Collectors.toList()).forEach(field -> {
            try {
                String shaderContext = (String)field.get(this);
                if (!Strings.isNullOrEmpty(shaderContext)) {
                    this.shaderList.add(new AbstractMap.SimpleEntry<String, String>(field.getName(), shaderContext));
                }
            }
            catch (Throwable throwable) {
                // empty catch block
            }
        });
        this.setEnabledShader(Config.getInstance().getBackground("Shader").get("enabled").getAsBoolean());
        this.setCurrentShaderId(Config.getInstance().getBackground("Shader").get("id").getAsInt());
    }

    public void renderShader() {
        this.shaderUniform.renderFirst();
        this.shaderUniform.addDefaultUniforms(true);
        this.shaderUniform.renderSecond();
    }

    private void updateShaderUniform() {
        this.shaderUniform = new ShaderUniform(this.shaderList.get(this.currentShaderId).getValue());
        Config.getInstance().editBackground("Shader", json -> json.add("id", new JsonPrimitive(this.currentShaderId)));
    }

    public void setCurrentShaderId(int currentShaderId) {
        this.currentShaderId = currentShaderId;
        this.updateShaderUniform();
    }

    public int getCurrentShaderId() {
        return this.currentShaderId;
    }

    public void setEnabledShader(boolean isEnabledShader) {
        this.isEnabledShader = isEnabledShader;
        Config.getInstance().editBackground("Shader", json -> json.add("enabled", new JsonPrimitive(this.isEnabledShader)));
    }

    public final boolean isEnabledShader() {
        return this.isEnabledShader;
    }

    public final List<Map.Entry<String, String>> getShaderList() {
        return this.shaderList;
    }

    public final boolean isPEnabledBackground() {
        return this.isPEnabledBackground;
    }

    public void setPEnabledBackground(boolean isPEnabledBackground) {
        this.isPEnabledBackground = isPEnabledBackground;
        Config.getInstance().editBackground("Background", json -> json.addProperty("penabled", this.isPEnabledBackground));
    }

    public final boolean isCEnabledBackground() {
        return this.isCEnabledBackground;
    }

    public void setCEnabledBackground(boolean isCEnabledBackground) {
        this.isCEnabledBackground = isCEnabledBackground;
        Config.getInstance().editBackground("Background", json -> json.addProperty("cenabled", this.isCEnabledBackground));
    }

    public final Map<String, DynamicTexture> getImageCache() {
        return this.imageCache;
    }

    public static final List<String> getBackgrounds() {
        return Arrays.asList("mojang", "wood", "town", "block");
    }
}

