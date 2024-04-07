package com.SpotifyWebAPI.WebAPI.Controllers;

import com.SpotifyWebAPI.WebAPI.Configs.AudioFeaturesObject;
import com.SpotifyWebAPI.WebAPI.Configs.SpotifyConfig;
import com.SpotifyWebAPI.WebAPI.Requests.TrackRecommendationRequest;
import com.SpotifyWebAPI.WebAPI.UserDetails.UserInformation;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import se.michaelthelin.spotify.SpotifyApi;
import se.michaelthelin.spotify.model_objects.specification.AudioFeatures;
import se.michaelthelin.spotify.model_objects.specification.Paging;
import se.michaelthelin.spotify.model_objects.specification.Recommendations;
import se.michaelthelin.spotify.model_objects.specification.Track;
import se.michaelthelin.spotify.requests.data.browse.GetRecommendationsRequest;
import se.michaelthelin.spotify.requests.data.search.simplified.SearchTracksRequest;
import se.michaelthelin.spotify.requests.data.tracks.GetAudioFeaturesForSeveralTracksRequest;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

@RestController
@RequestMapping("/api")
public class SongRecommendationController {
    @Autowired
    private SpotifyConfig spotifyConfig;

    @Autowired
    private UserInformation userInformation;
    @Autowired SongFeatureService songFeatureService;
    @GetMapping("songSearch")
    @ResponseBody
    public Track[] searchSpotify(@RequestParam String query, HttpServletRequest httpRequest) {
        Cookie[] cookies = httpRequest.getCookies();
        SpotifyApi spotifyAPI = spotifyConfig.getSpotifyObject();
        spotifyAPI.setAccessToken(userInformation.getUserCode(cookies, "authToken"));
        spotifyAPI.setRefreshToken(userInformation.getUserCode(cookies, "refreshToken"));
        final SearchTracksRequest searchTracksRequest = spotifyAPI.searchTracks(query)
                .limit(10)
                .offset(0)
                .includeExternal("audio")
                .build();
        try {
            final Paging<Track> trackPaging = searchTracksRequest.execute();
            return trackPaging.getItems();
        } catch (Exception e) {
            System.out.println("Something went wrong!\n" + e.getMessage());
        }
        return new Track[0];
    }

    @PostMapping("songRecs")
    @ResponseBody
    public ArrayList<AudioFeaturesObject> trackRecs(@RequestBody TrackRecommendationRequest request, HttpServletRequest httpRequest) {
        Cookie[] cookies = httpRequest.getCookies();
        SpotifyApi spotifyAPI = spotifyConfig.getSpotifyObject();
        spotifyAPI.setAccessToken(userInformation.getUserCode(cookies, "authToken"));
        spotifyAPI.setRefreshToken(userInformation.getUserCode(cookies, "refreshToken"));

        final GetAudioFeaturesForSeveralTracksRequest getAudioFeaturesForSeveralTracksRequest = spotifyAPI
                .getAudioFeaturesForSeveralTracks(request.getTracks())
                .build();
        try {
            final AudioFeatures[] audioFeatures = getAudioFeaturesForSeveralTracksRequest.execute();
            return songFeatureService.getSimilarSongFeatures(audioFeatures);
        } catch (Exception e) {
            System.out.println("Something went wrong!\n" + e.getMessage());
        }
        return new ArrayList<AudioFeaturesObject>();
    }

//    @PostMapping("songRecs")
//    @ResponseBody
//    public HashMap<String, String> trackRecs(@RequestBody TrackRecommendationRequest request, HttpServletRequest httpRequest) {
//        Cookie[] cookies = httpRequest.getCookies();
//        SpotifyApi spotifyAPI = spotifyConfig.getSpotifyObject();
//        spotifyAPI.setAccessToken(userInformation.getUserCode(cookies, "authToken"));
//        spotifyAPI.setRefreshToken(userInformation.getUserCode(cookies, "refreshToken"));
//        String[] songProperties = new String[]{};
//        final GetRecommendationsRequest getRecommendationsRequest = spotifyAPI.getRecommendations()
//                .market(request.getMarket())
//                .limit(request.getLimit())
//                .seed_tracks(request.getTracks())
//                .build();
//        try {
//            final AudioFeatures[] audioFeatures = getAudioFeaturesForSeveralTracksRequest.execute();
//            return songFeatureService.getSimilarSongFeatures(audioFeatures);
//        } catch (Exception e) {
//            System.out.println("Something went wrong!\n" + e.getMessage());
//        }
//    }
}
