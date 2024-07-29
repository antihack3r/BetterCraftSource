/*
 * Decompiled with CFR 0.152.
 */
package net.labymod.account;

import com.google.gson.JsonObject;
import com.mojang.authlib.Agent;
import com.mojang.authlib.UserAuthentication;
import com.mojang.authlib.yggdrasil.YggdrasilAuthenticationService;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.HttpURLConnection;
import java.net.Proxy;
import java.net.URL;
import java.util.List;
import java.util.UUID;
import net.labymod.account.AccountManager;
import net.labymod.account.AuthError;
import net.labymod.account.LauncherProfile;
import net.labymod.core.LabyModCore;
import net.labymod.main.LabyMod;
import net.labymod.support.util.Debug;
import net.labymod.utils.Consumer;
import net.labymod.utils.JsonParse;
import net.labymod.utils.UUIDFetcher;
import net.minecraft.client.Minecraft;
import net.minecraft.util.Session;
import org.apache.commons.io.Charsets;
import org.apache.commons.io.IOUtils;

public class AccountLoginHandler {
    private AccountManager accountManager;

    public AccountLoginHandler(AccountManager accountManager) {
        this.accountManager = accountManager;
    }

    public void loginWithPassword(String username, String password, final Consumer<Boolean> response) {
        this.authenticatePassword(username, password, new Consumer<Session>(){

            @Override
            public void accept(Session session) {
                if (session == null) {
                    response.accept(false);
                } else {
                    AccountLoginHandler.this.login(session, response);
                }
            }
        });
    }

    public void loginWithToken(UUID uuid, String username, String accessToken, Consumer<Boolean> response) {
        Session session = new Session(username, uuid.toString(), accessToken, Session.Type.MOJANG.toString());
        this.login(session, response);
    }

    public void login(Session session, Consumer<Boolean> response) {
        try {
            this.setSession(session, response);
        }
        catch (Exception e2) {
            response.accept(false);
            e2.printStackTrace();
            this.accountManager.throwError(AuthError.EXCEPTION, e2.getMessage());
        }
    }

    public void refreshToken(String accessToken, Consumer<String> refreshedToken, Consumer<Boolean> response) {
        try {
            String payload = "{ \"clientToken\": \"" + this.accountManager.getClientToken() + "\", \"accessToken\": \"" + accessToken + "\" }";
            String result = this.performPostRequest(new URL("https://authserver.mojang.com/refresh"), payload);
            JsonObject resultObject = JsonParse.parse(result).getAsJsonObject();
            if (resultObject.has("accessToken")) {
                String newAccessToken = resultObject.get("accessToken").getAsString();
                refreshedToken.accept(JsonParse.parse(newAccessToken).getAsString());
                response.accept(true);
            } else if (resultObject.has("errorMessage")) {
                response.accept(false);
                this.accountManager.throwError(AuthError.FAILED_TO_REFRESH, resultObject.get("errorMessage").getAsString());
            } else {
                response.accept(false);
                this.accountManager.throwError(AuthError.FAILED_TO_REFRESH, "Unknown error");
            }
        }
        catch (Exception error) {
            error.printStackTrace();
            response.accept(false);
            this.accountManager.throwError(AuthError.EXCEPTION, new String[0]);
        }
    }

    public void setSession(Session newSession, Consumer<Boolean> response) {
        try {
            LabyMod.getInstance().getLabyConnect().getClientConnection().disconnect(false);
            Minecraft mc2 = Minecraft.getMinecraft();
            Field field = ReflectionHelper.findField(Minecraft.class, LabyModCore.getMappingAdapter().getSessionMappings());
            field.setAccessible(true);
            field.set(mc2, newSession);
            LabyMod.getInstance().getLabyConnect().getClientConnection().connect();
            Debug.log(Debug.EnumDebugMode.ACCOUNT_MANAGER, "AccountManager: You are now playing with " + newSession.getProfile().getName() + ".");
            response.accept(true);
        }
        catch (Exception error) {
            error.printStackTrace();
            response.accept(false);
            this.accountManager.throwError(AuthError.EXCEPTION, new String[0]);
        }
    }

    private void removeFinal(Field field) throws Exception {
        field.setAccessible(true);
        int modData = field.getModifiers();
        Field fieldModification = field.getClass().getDeclaredField("modifiers");
        fieldModification.setAccessible(true);
        fieldModification.setInt(field, modData &= 0xFFFFFFEF);
    }

    public void authenticatePassword(String username, String password, Consumer<Session> callback) {
        try {
            YggdrasilAuthenticationService auth = new YggdrasilAuthenticationService(Proxy.NO_PROXY, this.accountManager.getClientToken());
            UserAuthentication authentification = auth.createUserAuthentication(Agent.MINECRAFT);
            authentification.setUsername(username);
            authentification.setPassword(password);
            authentification.logIn();
            if (authentification.canPlayOnline()) {
                Session session = new Session(authentification.getSelectedProfile().getName(), authentification.getSelectedProfile().getId().toString(), authentification.getAuthenticatedToken(), Session.Type.MOJANG.toString());
                callback.accept(session);
            } else {
                callback.accept(null);
                this.accountManager.throwError(AuthError.NOT_READY_TO_PLAY, new String[0]);
            }
        }
        catch (Exception error) {
            error.printStackTrace();
            callback.accept(null);
            this.accountManager.throwError(AuthError.EXCEPTION, error.getMessage());
        }
    }

    public void handleDirectLogin(final String username, String password, final boolean saveAccount, final Consumer<Boolean> result) {
        this.authenticatePassword(username, password, new Consumer<Session>(){

            @Override
            public void accept(final Session session) {
                if (session == null) {
                    result.accept(false);
                } else {
                    List<LauncherProfile> launcherProfiles = AccountLoginHandler.this.accountManager.getLauncherProfiles();
                    LauncherProfile targetProfile = null;
                    for (LauncherProfile launcherProfile : launcherProfiles) {
                        if (!session.getProfile().getId().equals(launcherProfile.getGameProfile().getId())) continue;
                        targetProfile = launcherProfile;
                        break;
                    }
                    if (targetProfile == null) {
                        if (saveAccount) {
                            AccountLoginHandler.this.accountManager.addAccount(username, session);
                        }
                    } else {
                        targetProfile.setAccessToken(session.getToken());
                    }
                    AccountLoginHandler.this.accountManager.saveLauncherProfiles(new Consumer<Boolean>(){

                        @Override
                        public void accept(Boolean accepted) {
                            if (accepted.booleanValue()) {
                                AccountLoginHandler.this.setSession(session, result);
                            } else {
                                result.accept(false);
                            }
                        }
                    });
                }
            }
        });
    }

    public void handleOfflineLogin(final String username, final boolean saveAccount, final Consumer<Boolean> result) {
        UUIDFetcher.getUUID(username, new Consumer<UUID>(){

            @Override
            public void accept(UUID uuid) {
                if (uuid == null) {
                    AccountLoginHandler.this.accountManager.throwError(AuthError.EXCEPTION, "Username not found");
                    result.accept(false);
                } else {
                    final Session session = new Session(username, uuid.toString(), "", Session.Type.MOJANG.toString());
                    List<LauncherProfile> launcherProfiles = AccountLoginHandler.this.accountManager.getLauncherProfiles();
                    LauncherProfile targetProfile = null;
                    for (LauncherProfile launcherProfile : launcherProfiles) {
                        if (!session.getProfile().getId().equals(launcherProfile.getGameProfile().getId())) continue;
                        targetProfile = launcherProfile;
                        break;
                    }
                    if (targetProfile == null && saveAccount) {
                        AccountLoginHandler.this.accountManager.addAccount(username, session);
                    }
                    AccountLoginHandler.this.accountManager.saveLauncherProfiles(new Consumer<Boolean>(){

                        @Override
                        public void accept(Boolean accepted) {
                            if (accepted.booleanValue()) {
                                AccountLoginHandler.this.setSession(session, result);
                            } else {
                                result.accept(false);
                            }
                        }
                    });
                }
            }
        });
    }

    private String performPostRequest(URL url, String post) throws Exception {
        HttpURLConnection connection = (HttpURLConnection)url.openConnection();
        connection.setConnectTimeout(15000);
        connection.setReadTimeout(15000);
        connection.setUseCaches(false);
        byte[] postAsBytes = post.getBytes(Charsets.UTF_8);
        connection.setRequestProperty("Content-Type", "application/json; charset=utf-8");
        connection.setRequestProperty("Content-Length", "" + postAsBytes.length);
        connection.setDoOutput(true);
        OutputStream outputStream = null;
        try {
            outputStream = connection.getOutputStream();
            IOUtils.write(postAsBytes, outputStream);
        }
        finally {
            IOUtils.closeQuietly(outputStream);
        }
        InputStream inputStream = null;
        try {
            String result;
            inputStream = connection.getInputStream();
            String string = result = IOUtils.toString(inputStream, Charsets.UTF_8);
            return string;
        }
        catch (IOException e2) {
            IOUtils.closeQuietly(inputStream);
            inputStream = connection.getErrorStream();
            if (inputStream != null) {
                String result2;
                String string = result2 = IOUtils.toString(inputStream, Charsets.UTF_8);
                return string;
            }
            throw e2;
        }
        finally {
            IOUtils.closeQuietly(inputStream);
        }
    }

    public static class ReflectionHelper {
        public static Field findField(Class<?> clazz, String ... fieldNames) {
            Exception failed = null;
            String[] stringArray = fieldNames;
            int n2 = fieldNames.length;
            int n3 = 0;
            while (n3 < n2) {
                String fieldName = stringArray[n3];
                try {
                    Field f2 = clazz.getDeclaredField(fieldName);
                    f2.setAccessible(true);
                    return f2;
                }
                catch (Exception e2) {
                    failed = e2;
                    ++n3;
                }
            }
            throw new UnableToFindFieldException(fieldNames, failed);
        }

        public static <T, E> T getPrivateValue(Class<? super E> classToAccess, E instance, int fieldIndex) {
            try {
                Field f2 = classToAccess.getDeclaredFields()[fieldIndex];
                f2.setAccessible(true);
                return (T)f2.get(instance);
            }
            catch (Exception e2) {
                throw new UnableToAccessFieldException(new String[0], e2);
            }
        }

        public static <T, E> T getPrivateValue(Class<? super E> classToAccess, E instance, String ... fieldNames) {
            try {
                return (T)ReflectionHelper.findField(classToAccess, fieldNames).get(instance);
            }
            catch (Exception e2) {
                throw new UnableToAccessFieldException(fieldNames, e2);
            }
        }

        public static <T, E> void setPrivateValue(Class<? super T> classToAccess, T instance, E value, int fieldIndex) {
            try {
                Field f2 = classToAccess.getDeclaredFields()[fieldIndex];
                f2.setAccessible(true);
                f2.set(instance, value);
            }
            catch (Exception e2) {
                throw new UnableToAccessFieldException(new String[0], e2);
            }
        }

        public static <T, E> void setPrivateValue(Class<? super T> classToAccess, T instance, E value, String ... fieldNames) {
            try {
                ReflectionHelper.findField(classToAccess, fieldNames).set(instance, value);
            }
            catch (Exception e2) {
                throw new UnableToAccessFieldException(fieldNames, e2);
            }
        }

        public static Class<? super Object> getClass(ClassLoader loader, String ... classNames) {
            Exception err = null;
            String[] stringArray = classNames;
            int n2 = classNames.length;
            int n3 = 0;
            while (n3 < n2) {
                String className = stringArray[n3];
                try {
                    return Class.forName(className, false, loader);
                }
                catch (Exception e2) {
                    err = e2;
                    ++n3;
                }
            }
            throw new UnableToFindClassException(classNames, err);
        }

        public static <E> Method findMethod(Class<? super E> clazz, E instance, String[] methodNames, Class<?> ... methodTypes) {
            Exception failed = null;
            String[] stringArray = methodNames;
            int n2 = methodNames.length;
            int n3 = 0;
            while (n3 < n2) {
                String methodName = stringArray[n3];
                try {
                    Method m2 = clazz.getDeclaredMethod(methodName, methodTypes);
                    m2.setAccessible(true);
                    return m2;
                }
                catch (Exception e2) {
                    failed = e2;
                    ++n3;
                }
            }
            throw new UnableToFindMethodException(methodNames, failed);
        }

        public static class UnableToAccessFieldException
        extends RuntimeException {
            private static final long serialVersionUID = 1L;
            private String[] fieldNameList;

            public UnableToAccessFieldException(String[] fieldNames, Exception e2) {
                super(e2);
                this.fieldNameList = fieldNames;
            }
        }

        public static class UnableToFindClassException
        extends RuntimeException {
            private static final long serialVersionUID = 1L;
            private String[] classNames;

            public UnableToFindClassException(String[] classNames, Exception err) {
                super(err);
                this.classNames = classNames;
            }
        }

        public static class UnableToFindFieldException
        extends RuntimeException {
            private static final long serialVersionUID = 1L;
            private String[] fieldNameList;

            public UnableToFindFieldException(String[] fieldNameList, Exception e2) {
                super(e2);
                this.fieldNameList = fieldNameList;
            }
        }

        public static class UnableToFindMethodException
        extends RuntimeException {
            private static final long serialVersionUID = 1L;
            private String[] methodNames;

            public UnableToFindMethodException(String[] methodNames, Exception failed) {
                super(failed);
                this.methodNames = methodNames;
            }
        }
    }
}

