package com.SpotifyWebAPI.WebAPI.Controllers;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import se.michaelthelin.spotify.SpotifyApi;
import se.michaelthelin.spotify.model_objects.credentials.AuthorizationCodeCredentials;
import se.michaelthelin.spotify.requests.authorization.authorization_code.pkce.AuthorizationCodePKCERefreshRequest;

import java.io.IOException;

public class AuthService {
    public String[] getRefreshedTokens(SpotifyApi spotifyAPI) {
        final AuthorizationCodePKCERefreshRequest refreshRequest = spotifyAPI.authorizationCodePKCERefresh().build();
        try {
            final AuthorizationCodeCredentials authorizationCodeCredentials = refreshRequest.execute();
            String[] tokens = new String[2];
            tokens[0] = authorizationCodeCredentials.getAccessToken();
            tokens[1] = authorizationCodeCredentials.getRefreshToken();
            return tokens;
        } catch (Exception error) {
            System.out.println("Error with refreshing tokens: " + error.getMessage());
        }
        return null;
    }
}
