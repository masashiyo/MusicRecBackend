package com.SpotifyWebAPI.WebAPI.Controllers;

import com.SpotifyWebAPI.WebAPI.Configs.SpotifyConfig;
import com.SpotifyWebAPI.WebAPI.UserDetails.UserInformation;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import se.michaelthelin.spotify.SpotifyApi;
import se.michaelthelin.spotify.model_objects.specification.Artist;
import se.michaelthelin.spotify.model_objects.specification.Paging;
import se.michaelthelin.spotify.model_objects.specification.Track;
import se.michaelthelin.spotify.requests.data.personalization.simplified.GetUsersTopArtistsRequest;
import se.michaelthelin.spotify.requests.data.personalization.simplified.GetUsersTopTracksRequest;
@RestController
@RequestMapping("/api")

public class TopArtistsTracksController {
    @Autowired
    private SpotifyConfig spotifyConfig;

    @Autowired
    private UserInformation userInformation;

    @GetMapping(value = "user-top-artists")
    public Artist[] getUserTopArtists(
            @RequestParam String timeRange,
            @RequestParam int limit,
            @RequestParam int offset,
            HttpServletRequest httpRequest)
    {
        Cookie[] cookies = httpRequest.getCookies();
        SpotifyApi spotifyAPI = spotifyConfig.getSpotifyObject();
        spotifyAPI.setAccessToken(userInformation.getUserCode(cookies, "authToken"));
        spotifyAPI.setRefreshToken(userInformation.getUserCode(cookies, "refreshToken"));
        final GetUsersTopArtistsRequest getUsersTopArtistsRequest = spotifyAPI.getUsersTopArtists()
                .time_range(timeRange)
                .limit(limit)
                .offset(offset)
                .build();
        try {
            final Paging<Artist> artistPaging = getUsersTopArtistsRequest.execute();
            return artistPaging.getItems();
        } catch (Exception e) {
            System.out.println("Something went wrong!\n" + e.getMessage());
        }
        return new Artist[0];
    }

    @GetMapping(value = "user-top-tracks")
    public Track[] getUserTopTracks(
            @RequestParam String timeRange,
            @RequestParam int limit,
            @RequestParam int offset,
            HttpServletRequest httpRequest)
    {
        Cookie[] cookies = httpRequest.getCookies();
        SpotifyApi spotifyAPI = spotifyConfig.getSpotifyObject();
        spotifyAPI.setAccessToken(userInformation.getUserCode(cookies, "authToken"));
        spotifyAPI.setRefreshToken(userInformation.getUserCode(cookies, "refreshToken"));
        final GetUsersTopTracksRequest getUsersTopTracksRequest = spotifyAPI.getUsersTopTracks()
                .time_range(timeRange)
                .limit(limit)
                .offset(offset)
                .build();
        try {
            final Paging<Track> trackPaging = getUsersTopTracksRequest.execute();
            return trackPaging.getItems();
        } catch (Exception e) {
            System.out.println("Something went wrong!\n" + e.getMessage());
        }
        return new Track[0];
    }
}