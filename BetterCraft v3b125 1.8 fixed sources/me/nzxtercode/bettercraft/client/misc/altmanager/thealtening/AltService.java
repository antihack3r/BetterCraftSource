/*
 * Decompiled with CFR 0.152.
 */
package me.nzxtercode.bettercraft.client.misc.altmanager.thealtening;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import me.nzxtercode.bettercraft.client.misc.altmanager.thealtening.ReflectionUtility;

public class AltService {
    private static final AltService INSTANCE = new AltService();
    private ReflectionUtility userAuthentication = new ReflectionUtility("com.mojang.authlib.yggdrasil.YggdrasilUserAuthentication");
    private ReflectionUtility minecraftSession = new ReflectionUtility("com.mojang.authlib.yggdrasil.YggdrasilMinecraftSessionService");
    private EnumAltService currentService;

    public static AltService getInstance() {
        return INSTANCE;
    }

    public void switchService(EnumAltService enumAltService) throws NoSuchFieldException, IllegalAccessException {
        if (this.currentService == enumAltService) {
            return;
        }
        this.reflectionFields(enumAltService.hostname);
        this.currentService = enumAltService;
    }

    private void reflectionFields(String authServer) throws NoSuchFieldException, IllegalAccessException {
        HashMap<String, URL> userAuthenticationModifies = new HashMap<String, URL>();
        String useSecureStart = authServer.contains("thealtening") ? "http" : "https";
        userAuthenticationModifies.put("ROUTE_AUTHENTICATE", this.constantURL(String.valueOf(useSecureStart) + "://authserver." + authServer + ".com/authenticate"));
        userAuthenticationModifies.put("ROUTE_INVALIDATE", this.constantURL(String.valueOf(useSecureStart) + "://authserver" + authServer + "com/invalidate"));
        userAuthenticationModifies.put("ROUTE_REFRESH", this.constantURL(String.valueOf(useSecureStart) + "://authserver." + authServer + ".com/refresh"));
        userAuthenticationModifies.put("ROUTE_VALIDATE", this.constantURL(String.valueOf(useSecureStart) + "://authserver." + authServer + ".com/validate"));
        userAuthenticationModifies.put("ROUTE_SIGNOUT", this.constantURL(String.valueOf(useSecureStart) + "://authserver." + authServer + ".com/signout"));
        userAuthenticationModifies.forEach((key, value) -> {
            try {
                this.userAuthentication.setStaticField((String)key, value);
            }
            catch (Exception e2) {
                e2.printStackTrace();
            }
        });
        this.userAuthentication.setStaticField("BASE_URL", String.valueOf(useSecureStart) + "://authserver." + authServer + ".com/");
        this.minecraftSession.setStaticField("BASE_URL", String.valueOf(useSecureStart) + "://sessionserver." + authServer + ".com/session/minecraft/");
        this.minecraftSession.setStaticField("JOIN_URL", this.constantURL(String.valueOf(useSecureStart) + "://sessionserver." + authServer + ".com/session/minecraft/join"));
        this.minecraftSession.setStaticField("CHECK_URL", this.constantURL(String.valueOf(useSecureStart) + "://sessionserver." + authServer + ".com/session/minecraft/hasJoined"));
        this.minecraftSession.setStaticField("WHITELISTED_DOMAINS", new String[]{".minecraft.net", ".mojang.com", ".thealtening.com"});
    }

    private URL constantURL(String url) {
        try {
            return new URL(url);
        }
        catch (MalformedURLException ex2) {
            throw new Error("Couldn't create constant for " + url, ex2);
        }
    }

    public EnumAltService getCurrentService() {
        if (this.currentService == null) {
            this.currentService = EnumAltService.MOJANG;
        }
        return this.currentService;
    }

    public void switchToMojang() {
        try {
            this.switchService(EnumAltService.MOJANG);
        }
        catch (NoSuchFieldException e2) {
            System.out.println("Couldn't switch to modank altservice");
        }
        catch (IllegalAccessException e3) {
            System.out.println("Couldn't switch to modank altservice -2");
        }
    }

    public void switchToTheAltening() {
        try {
            this.switchService(EnumAltService.THEALTENING);
        }
        catch (NoSuchFieldException e2) {
            System.out.println("Couldn't switch to altening altservice");
        }
        catch (IllegalAccessException e3) {
            System.out.println("Couldn't switch to altening altservice -2");
        }
    }

    public static enum EnumAltService {
        MOJANG("mojang"),
        THEALTENING("thealtening");

        String hostname;

        private EnumAltService(String hostname) {
            this.hostname = hostname;
        }
    }
}

