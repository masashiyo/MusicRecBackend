package com.SpotifyWebAPI.WebAPI;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.bind.annotation.*;
import se.michaelthelin.spotify.SpotifyApi;
import se.michaelthelin.spotify.SpotifyHttpManager;
import se.michaelthelin.spotify.exceptions.SpotifyWebApiException;
import se.michaelthelin.spotify.model_objects.credentials.AuthorizationCodeCredentials;
import se.michaelthelin.spotify.model_objects.specification.Artist;
import se.michaelthelin.spotify.model_objects.specification.Paging;
import se.michaelthelin.spotify.model_objects.specification.Track;
import se.michaelthelin.spotify.requests.authorization.authorization_code.AuthorizationCodeRequest;
import se.michaelthelin.spotify.requests.authorization.authorization_code.AuthorizationCodeUriRequest;
import se.michaelthelin.spotify.requests.data.personalization.simplified.GetUsersTopArtistsRequest;
import se.michaelthelin.spotify.requests.data.personalization.simplified.GetUsersTopTracksRequest;
import se.michaelthelin.spotify.requests.data.search.simplified.SearchTracksRequest;

import java.io.IOException;
import java.net.URI;

@RestController
@RequestMapping("/auth")
public class AuthController {
    private static final java.net.URI redirectUri = SpotifyHttpManager.makeUri("http://localhost:8080/auth/get-user-code");
    private String code = "";

    private static final SpotifyApi spotifyAPI = new SpotifyApi.Builder()
            //.setClientId(System.getenv("SPOTIFY_CLIENT_ID")) set SPOTIFY_CLIENT_ID=your-client-id
            //.setClientSecret(System.getenv("SPOTIFY_CLIENT_SECRET")) set SPOTIFY_CLIENT_SECRET=your-client-secret
            .setClientId("5f49d819d085489a833b8f0ad63886e5")
            .setClientSecret("776f056f1efd4f45988f3bd9c8f42de1")
            .setRedirectUri(redirectUri)
            .build();

    @GetMapping("login")
    @ResponseBody
    public String spotifyLogin() {
        AuthorizationCodeUriRequest authorizationCodeUriRequest = spotifyAPI.authorizationCodeUri()
                .scope("user-read-private, user-read-email, user-top-read")
                .show_dialog(true)
                .build();
        final URI uri = authorizationCodeUriRequest.execute();
        return uri.toString();
    }

    @GetMapping(value = "get-user-code")
    public String getSpotifyUserCode(@RequestParam("code") String userCode, HttpServletResponse response) throws IOException {
        code = userCode;
        AuthorizationCodeRequest authorizationCodeRequest = spotifyAPI.authorizationCode(code)
                .build();

        try {
            final AuthorizationCodeCredentials authorizationCodeCredentials = authorizationCodeRequest.execute();

            spotifyAPI.setAccessToken((authorizationCodeCredentials).getAccessToken());
            spotifyAPI.setRefreshToken((authorizationCodeCredentials).getRefreshToken());

            System.out.println("Expires in: " + authorizationCodeCredentials.getExpiresIn());
        } catch (IOException | SpotifyWebApiException | org.apache.hc.core5.http.ParseException e) {
            System.out.println("Error: " + e.getMessage());
        }
        response.sendRedirect("http://localhost:5173/songRecommendation");
        return spotifyAPI.getAccessToken();
    }

    @PostMapping(value = "user-top-artists")
    public Artist[] getUserTopArtists(@RequestBody TopSongOrArtistRequest request) {

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
    public Track[] searchSpotify(@RequestBody SearchStringRequest query) {
        String searchQuery = query.getQuery();
        final SearchTracksRequest searchTracksRequest = spotifyAPI.searchTracks(searchQuery)
                .limit(10)
                .offset(0)
//                .includeExternal("audio") will probably use this somewhere later
                .build();
        try{
            final Paging<Track> trackPaging = searchTracksRequest.execute();
            return trackPaging.getItems();
        } catch (Exception e) {
            System.out.println("Something went wrong!\n" + e.getMessage());
        }
        return new Track[0];
    }
}


