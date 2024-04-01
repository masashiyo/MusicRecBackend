package com.SpotifyWebAPI.WebAPI.Configs;

import org.springframework.stereotype.Service;
import se.michaelthelin.spotify.SpotifyApi;
import se.michaelthelin.spotify.SpotifyHttpManager;

@Service
public class SpotifyConfig {
    private static final java.net.URI redirectUri = SpotifyHttpManager.makeUri("http://localhost:8080/auth/get-user-code");
    public SpotifyApi getSpotifyObject() {
            //.setClientId(System.getenv("SPOTIFY_CLIENT_ID")) set SPOTIFY_CLIENT_ID=your-client-id
            //.setClientSecret(System.getenv("SPOTIFY_CLIENT_SECRET")) set SPOTIFY_CLIENT_SECRET=your-client-secret
            return new SpotifyApi.Builder()
            .setClientId("5f49d819d085489a833b8f0ad63886e5")
            .setClientSecret("776f056f1efd4f45988f3bd9c8f42de1")
            .setRedirectUri(redirectUri)
            .build();
    }

}
