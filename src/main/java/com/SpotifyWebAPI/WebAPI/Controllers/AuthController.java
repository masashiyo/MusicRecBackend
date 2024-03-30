package com.SpotifyWebAPI.WebAPI.Controllers;
import com.SpotifyWebAPI.WebAPI.Configs.SpotifyConfig;
import com.SpotifyWebAPI.WebAPI.Requests.SearchStringRequest;
import com.SpotifyWebAPI.WebAPI.Requests.TopSongOrArtistRequest;
import com.SpotifyWebAPI.WebAPI.Requests.TrackRecommendationRequest;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import se.michaelthelin.spotify.SpotifyApi;
import se.michaelthelin.spotify.SpotifyHttpManager;
import se.michaelthelin.spotify.exceptions.SpotifyWebApiException;
import se.michaelthelin.spotify.model_objects.credentials.AuthorizationCodeCredentials;
import se.michaelthelin.spotify.model_objects.specification.Artist;
import se.michaelthelin.spotify.model_objects.specification.Paging;
import se.michaelthelin.spotify.model_objects.specification.Recommendations;
import se.michaelthelin.spotify.model_objects.specification.Track;
import se.michaelthelin.spotify.requests.authorization.authorization_code.AuthorizationCodeRequest;
import se.michaelthelin.spotify.requests.authorization.authorization_code.AuthorizationCodeUriRequest;
import se.michaelthelin.spotify.requests.data.browse.GetRecommendationsRequest;
import se.michaelthelin.spotify.requests.data.personalization.simplified.GetUsersTopArtistsRequest;
import se.michaelthelin.spotify.requests.data.personalization.simplified.GetUsersTopTracksRequest;
import se.michaelthelin.spotify.requests.data.search.simplified.SearchTracksRequest;

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
        Cookie authCookie = new Cookie("authToken", authToken);
//        authCookie.setSecure(true);
//        authCookie.setHttpOnly(true);
        authCookie.setMaxAge(60 * 60); //1 hr for now
        authCookie.setPath("http://localhost:8080"); //sets to global. need to look more into this

        Cookie refreshCookie = new Cookie("refreshToken", refreshToken);
//        refreshCookie.setSecure(true);
//        refreshCookie.setHttpOnly(true);
        refreshCookie.setMaxAge(60 * 60); // 1 hr for now
        refreshCookie.setPath("http://localhost:8080"); //sets to global. need to look more into this

        response.addCookie(authCookie);
        response.addCookie(refreshCookie);

        response.sendRedirect("http://localhost:5173/songRecommendation");
        return spotifyAPI.getAccessToken();
    }



    @PostMapping(value = "user-top-artists")
    public Artist[] getUserTopArtists(@RequestBody TopSongOrArtistRequest request) {
        SpotifyApi spotifyAPI = spotifyConfig.getSpotifyObject();
        spotifyAPI.setAccessToken(authToken);
        spotifyAPI.setRefreshToken(refreshToken);
        final GetUsersTopArtistsRequest getUsersTopArtistsRequest = spotifyAPI.getUsersTopArtists()
                .time_range(request.getTimeRange())
                .limit(request.getLimit())
                .offset(request.getOffset())
                .build();
        try {
            final Paging<Artist> artistPaging = getUsersTopArtistsRequest.execute();
            return artistPaging.getItems();
        } catch (Exception e) {
            System.out.println("Something went wrong!\n" + e.getMessage());
        }
        return new Artist[0];
    }

    @PostMapping(value = "user-top-tracks")
    public Track[] getUserTopTracks(@RequestBody TopSongOrArtistRequest request) {
        SpotifyApi spotifyAPI = spotifyConfig.getSpotifyObject();
        spotifyAPI.setAccessToken(authToken);
        spotifyAPI.setRefreshToken(refreshToken);
        final GetUsersTopTracksRequest getUsersTopTracksRequest = spotifyAPI.getUsersTopTracks()
                .time_range(request.getTimeRange())
                .limit(request.getLimit())
                .offset(request.getOffset())
                .build();
        try {
            final Paging<Track> trackPaging = getUsersTopTracksRequest.execute();
            return trackPaging.getItems();
        } catch (Exception e) {
            System.out.println("Something went wrong!\n" + e.getMessage());
        }
        return new Track[0];
    }

    @PostMapping("songSearch")
    @ResponseBody
    public Track[] searchSpotify(@RequestBody SearchStringRequest query, HttpServletRequest request) {
        SpotifyApi spotifyAPI = spotifyConfig.getSpotifyObject();
        Cookie[] cookies = request.getCookies();
        if(cookies != null) {
            for (Cookie cookie : cookies) {
                if(cookie.getName().equals("authToken")) {
                     spotifyAPI.setAccessToken(cookie.getValue());
                }
            }
            return null;
        }
        spotifyAPI.setRefreshToken(refreshToken);
        String searchQuery = query.getQuery();
        final SearchTracksRequest searchTracksRequest = spotifyAPI.searchTracks(searchQuery)
                .limit(10)
                .offset(0)
                .includeExternal("audio")
                .build();
        try{
            final Paging<Track> trackPaging = searchTracksRequest.execute();
            return trackPaging.getItems();
        } catch (Exception e) {
            System.out.println("Something went wrong!\n" + e.getMessage());
        }
        return new Track[0];
    }

    @PostMapping("songRecs")
    @ResponseBody
    public Track[] trackRecs(@RequestBody TrackRecommendationRequest request) {
        SpotifyApi spotifyAPI = spotifyConfig.getSpotifyObject();
        spotifyAPI.setAccessToken(authToken);
        spotifyAPI.setRefreshToken(refreshToken);
        final GetRecommendationsRequest getRecommendationsRequest = spotifyAPI.getRecommendations()
                .market(request.getMarket())
                .limit(request.getLimit())
                .seed_tracks(request.getTracks())
                .build();
        try {
            final Recommendations trackRecommendations = getRecommendationsRequest.execute();
            return trackRecommendations.getTracks();
        } catch (Exception e) {
            System.out.println("Something went wrong!\n" + e.getMessage());
        }
        return new Track[0];
    }
}


