package com.SpotifyWebAPI.WebAPI.Controllers;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import se.michaelthelin.spotify.SpotifyApi;
import se.michaelthelin.spotify.model_objects.credentials.AuthorizationCodeCredentials;
import se.michaelthelin.spotify.requests.authorization.authorization_code.pkce.AuthorizationCodePKCERefreshRequest;

import java.io.IOException;

public class AuthService {
    public String getRefreshedSpotifyAuthToken(SpotifyApi spotifyAPI) throws IOException {
        SpotifyApi tempSpotifyAPI = spotifyAPI;
        final AuthorizationCodePKCERefreshRequest refreshRequest = tempSpotifyAPI.authorizationCodePKCERefresh().build();
        try {
            final AuthorizationCodeCredentials authorizationCodeCredentials = refreshRequest.execute();
            return authorizationCodeCredentials.getRefreshToken();
        } catch (Exception error) {
            System.out.println("Error with refreshing your auth token: " + error.getMessage());
        }
        return null;
    }
    public String getRefreshedSpotifyRefreshToken(SpotifyApi spotifyAPI) throws IOException {
        SpotifyApi tempSpotifyAPI = spotifyAPI;
        final AuthorizationCodePKCERefreshRequest refreshRequest = tempSpotifyAPI.authorizationCodePKCERefresh().build();
        try {
            final AuthorizationCodeCredentials authorizationCodeCredentials = refreshRequest.execute();
            return authorizationCodeCredentials.getRefreshToken();
        } catch (Exception error) {
            System.out.println("Error with refreshing your refresh token: " + error.getMessage());
        }
        return null;
    }
}
