/*
 * Decompiled with CFR 0.152.
 */
package fr.litarvan.openauth.microsoft.model.response;

public class MinecraftProfile {
    private final String id;
    private final String name;
    private final MinecraftSkin[] skins;

    public MinecraftProfile(String id2, String name, MinecraftSkin[] skins) {
        this.id = id2;
        this.name = name;
        this.skins = skins;
    }

    public String getId() {
        return this.id;
    }

    public String getName() {
        return this.name;
    }

    public MinecraftSkin[] getSkins() {
        return this.skins;
    }

    public static class MinecraftSkin {
        private final String id;
        private final String state;
        private final String url;
        private final String variant;
        private final String alias;

        public MinecraftSkin(String id2, String state, String url, String variant, String alias) {
            this.id = id2;
            this.state = state;
            this.url = url;
            this.variant = variant;
            this.alias = alias;
        }

        public String getId() {
            return this.id;
        }

        public String getState() {
            return this.state;
        }

        public String getUrl() {
            return this.url;
        }

        public String getVariant() {
            return this.variant;
        }

        public String getAlias() {
            return this.alias;
        }
    }
}

