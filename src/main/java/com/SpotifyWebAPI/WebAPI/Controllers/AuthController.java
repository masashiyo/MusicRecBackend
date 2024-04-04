package com.SpotifyWebAPI.WebAPI.Controllers;
import com.SpotifyWebAPI.WebAPI.Configs.SpotifyConfig;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import se.michaelthelin.spotify.SpotifyApi;
import se.michaelthelin.spotify.exceptions.SpotifyWebApiException;
import se.michaelthelin.spotify.model_objects.credentials.AuthorizationCodeCredentials;
import se.michaelthelin.spotify.requests.authorization.authorization_code.AuthorizationCodeRequest;
import se.michaelthelin.spotify.requests.authorization.authorization_code.AuthorizationCodeUriRequest;

import java.io.IOException;
import java.net.URI;

@RestController
@RequestMapping("/auth")
public class AuthController {
    public String authToken = "";
    public String refreshToken = "";

    @Autowired
    private SpotifyConfig spotifyConfig;

    @GetMapping("login")
    @ResponseBody
    public String spotifyLogin() {
        SpotifyApi spotifyAPI = spotifyConfig.getSpotifyObject();
        AuthorizationCodeUriRequest authorizationCodeUriRequest = spotifyAPI.authorizationCodeUri()
                .scope("user-read-private, user-read-email, user-top-read")
                .show_dialog(true)
                .build();
        final URI uri = authorizationCodeUriRequest.execute();
        return uri.toString();
    }

    @GetMapping(value = "get-user-code")
    public String getSpotifyUserCode(@RequestParam(value = "code", required = false) String userCode, @RequestParam(value = "error", required = false) String error, HttpServletResponse response) throws IOException {

        if(error != null && error.equals("access_denied") || userCode == null) {
            System.out.println("Error occurred while trying to sign in.");
            response.sendRedirect("http://localhost:5173/");
            return null;
        }

        SpotifyApi spotifyAPI = spotifyConfig.getSpotifyObject();
        AuthorizationCodeRequest authorizationCodeRequest = spotifyAPI.authorizationCode(userCode)
                .build();

        try {
            final AuthorizationCodeCredentials authorizationCodeCredentials = authorizationCodeRequest.execute();


            String accessToken = authorizationCodeCredentials.getAccessToken();
            String refreshToken = authorizationCodeCredentials.getRefreshToken();

            spotifyAPI.setAccessToken(accessToken);
            spotifyAPI.setRefreshToken(refreshToken);

            System.out.println("Expires in: " + authorizationCodeCredentials.getExpiresIn());
        } catch (IOException | SpotifyWebApiException | org.apache.hc.core5.http.ParseException e) {
            System.out.println("Error: " + e.getMessage());
        }
        authToken = spotifyAPI.getAccessToken();
        refreshToken = spotifyAPI.getRefreshToken();
        Cookie authCookie = new Cookie("authToken", authToken){{
            setSecure(true);
            setHttpOnly(true);
            setMaxAge(60 * 60); //1 hr for now
            setPath("/"); //sets to global. need to look more into this
        }};

        Cookie refreshCookie = new Cookie("refreshToken", refreshToken) {{
            setSecure(true);
            setHttpOnly(true);
            setMaxAge(60 * 60); // 1 hr for now
            setPath("/"); //sets to global. need to look more into this
        }};


        response.addCookie(authCookie);
        response.addCookie(refreshCookie);

        response.sendRedirect("http://localhost:5173/songRecommendation");
        return spotifyAPI.getAccessToken();
    }
}


