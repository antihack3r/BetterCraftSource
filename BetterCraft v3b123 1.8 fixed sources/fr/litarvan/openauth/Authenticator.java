// 
// Decompiled by Procyon v0.6.0
// 

package fr.litarvan.openauth;

import java.io.InputStream;
import java.io.Reader;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.DataOutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.io.IOException;
import fr.litarvan.openauth.model.AuthError;
import com.google.gson.Gson;
import fr.litarvan.openauth.model.request.InvalidateRequest;
import fr.litarvan.openauth.model.request.SignoutRequest;
import fr.litarvan.openauth.model.request.ValidateRequest;
import fr.litarvan.openauth.model.request.RefreshRequest;
import fr.litarvan.openauth.model.response.RefreshResponse;
import fr.litarvan.openauth.model.request.AuthRequest;
import fr.litarvan.openauth.model.response.AuthResponse;
import fr.litarvan.openauth.model.AuthAgent;

public class Authenticator
{
    public static final String MOJANG_AUTH_URL = "https://authserver.mojang.com/";
    private String authURL;
    private AuthPoints authPoints;
    
    public Authenticator(final String authURL, final AuthPoints authPoints) {
        this.authURL = authURL;
        this.authPoints = authPoints;
    }
    
    public AuthResponse authenticate(final AuthAgent agent, final String username, final String password, final String clientToken) throws AuthenticationException {
        final AuthRequest request = new AuthRequest(agent, username, password, clientToken);
        return (AuthResponse)this.sendRequest(request, AuthResponse.class, this.authPoints.getAuthenticatePoint());
    }
    
    public RefreshResponse refresh(final String accessToken, final String clientToken) throws AuthenticationException {
        final RefreshRequest request = new RefreshRequest(accessToken, clientToken);
        return (RefreshResponse)this.sendRequest(request, RefreshResponse.class, this.authPoints.getRefreshPoint());
    }
    
    public void validate(final String accessToken) throws AuthenticationException {
        final ValidateRequest request = new ValidateRequest(accessToken);
        this.sendRequest(request, null, this.authPoints.getValidatePoint());
    }
    
    public void signout(final String username, final String password) throws AuthenticationException {
        final SignoutRequest request = new SignoutRequest(username, password);
        this.sendRequest(request, null, this.authPoints.getSignoutPoint());
    }
    
    public void invalidate(final String accessToken, final String clientToken) throws AuthenticationException {
        final InvalidateRequest request = new InvalidateRequest(accessToken, clientToken);
        this.sendRequest(request, null, this.authPoints.getInvalidatePoint());
    }
    
    private Object sendRequest(final Object request, final Class<?> model, final String authPoint) throws AuthenticationException {
        final Gson gson = new Gson();
        String response;
        try {
            response = this.sendPostRequest(this.authURL + authPoint, gson.toJson(request));
        }
        catch (final IOException e) {
            throw new AuthenticationException(new AuthError("Can't send the request : " + e.getClass().getName(), e.getMessage(), "Unknown"));
        }
        if (model != null) {
            return gson.fromJson(response, model);
        }
        return null;
    }
    
    private String sendPostRequest(final String url, final String json) throws AuthenticationException, IOException {
        final byte[] jsonBytes = json.getBytes(StandardCharsets.UTF_8);
        final URL serverURL = new URL(url);
        final HttpURLConnection connection = (HttpURLConnection)serverURL.openConnection();
        connection.setRequestMethod("POST");
        connection.setDoOutput(true);
        connection.setRequestProperty("Accept-Charset", "UTF-8");
        connection.setRequestProperty("Content-Type", "application/json;charset=utf-8");
        connection.setRequestProperty("Content-Length", String.valueOf(jsonBytes.length));
        final DataOutputStream wr = new DataOutputStream(connection.getOutputStream());
        wr.write(jsonBytes, 0, jsonBytes.length);
        wr.flush();
        wr.close();
        connection.connect();
        final int responseCode = connection.getResponseCode();
        if (responseCode == 204) {
            connection.disconnect();
            return null;
        }
        InputStream is;
        if (responseCode == 200) {
            is = connection.getInputStream();
        }
        else {
            is = connection.getErrorStream();
        }
        final BufferedReader br = new BufferedReader(new InputStreamReader(is));
        String response = br.readLine();
        try {
            br.close();
        }
        catch (final IOException e) {
            e.printStackTrace();
        }
        connection.disconnect();
        while (response != null && response.startsWith("\ufeff")) {
            response = response.substring(1);
        }
        if (responseCode == 200) {
            return response;
        }
        final Gson gson = new Gson();
        if (response != null && !response.startsWith("{")) {
            throw new AuthenticationException(new AuthError("Internal server error", response, "Remote"));
        }
        throw new AuthenticationException(gson.fromJson(response, AuthError.class));
    }
}
