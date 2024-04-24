package com.SpotifyWebAPI.WebAPI.Controllers;

import com.SpotifyWebAPI.WebAPI.Configs.AudioFeaturesObject;
import com.SpotifyWebAPI.WebAPI.Requests.TrackRecommendationRequest;
import org.springframework.stereotype.Service;
import se.michaelthelin.spotify.SpotifyApi;
import se.michaelthelin.spotify.model_objects.specification.AudioFeatures;
import se.michaelthelin.spotify.requests.data.browse.GetRecommendationsRequest;

import java.lang.reflect.Method;
import java.util.ArrayList;

@Service
public class SongFeatureService {
    private void loadListWithCommonAudioFeaturesObject(Float[] values, String lowValue, String middleValue, String highValue, String category, ArrayList<AudioFeaturesObject> audioFeaturesList) {
        float average = 0f;
        float range = 0f;
        float max = 0f;
        float min = 1f;
        float total = 0;
        for(int i = 0; i < values.length; i++) {
            total += values[i];
            if(values[i] > max)
                max = values[i];
            if(values[i] < min)
                min = values[i];
        }
        average = total/values.length;
        range = max-min;
        if(range <=.1) {
            if(average < 0.4) {
                audioFeaturesList.add(new AudioFeaturesObject(average, category, lowValue));
            } else if (average <= 0.6) {
                audioFeaturesList.add(new AudioFeaturesObject(average, category, middleValue));
            } else {
                audioFeaturesList.add(new AudioFeaturesObject(average, category, highValue));
            }
        }
    }
    public ArrayList<AudioFeaturesObject> getSimilarSongFeatures(AudioFeatures[] audioFeatures) {
        ArrayList<AudioFeaturesObject> audioFeatureList = new ArrayList<AudioFeaturesObject>();
        Float[] acousticness = new Float[audioFeatures.length];
        Float[] danceability = new Float[audioFeatures.length];
        Float[] energy = new Float[audioFeatures.length];
        Float[] valence = new Float[audioFeatures.length];
        Float[] vocal = new Float[audioFeatures.length];

        for(int i=0; i < audioFeatures.length; i++) {
            acousticness[i] = audioFeatures[i].getAcousticness();
            danceability[i] = audioFeatures[i].getDanceability();
            valence[i] = audioFeatures[i].getValence();
            energy[i] = audioFeatures[i].getEnergy();
            vocal[i] = audioFeatures[i].getInstrumentalness();
        }
        loadListWithCommonAudioFeaturesObject(acousticness, "High Electronic Mix", "Electronic and Acoustic Mix", "High Acoustic Mix", "acousticness", audioFeatureList);
        loadListWithCommonAudioFeaturesObject(danceability, "Low Danceability", "Medium Danceability", "High Dancebility", "danceability", audioFeatureList);
        loadListWithCommonAudioFeaturesObject(valence, "Sad", "Happy and Sad Mix", "Happy", "valence", audioFeatureList);
        loadListWithCommonAudioFeaturesObject(energy, "Low Energy", "Neutral Energy", "High Energy", "energy", audioFeatureList);
        loadListWithCommonAudioFeaturesObject(vocal, "High Vocal Mix", "Vocal and Instrumental Mix", "High Instrumental Mix", "instrumentalness", audioFeatureList);
        return audioFeatureList;
    }
    private static void setTargetProperty(GetRecommendationsRequest.Builder builder, String propertyName, float value) {
        try {
            String methodName = "target_" + propertyName;
            Method method = GetRecommendationsRequest.Builder.class.getMethod(methodName, Float.class);
            method.invoke(builder, value);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
        }
    }
    public static GetRecommendationsRequest getRecommendationsRequest(TrackRecommendationRequest request, SpotifyApi spotifyAPI) {
            GetRecommendationsRequest.Builder builder = spotifyAPI.getRecommendations()
                    .seed_tracks(request.getTracks())
                    .limit(request.getLimit())
                    .market(request.getMarket());

            for(int i=0; i<request.getAudioFeatureList().length; i++) {
                    setTargetProperty(builder, request.getAudioFeatureList()[i].getCategory(), request.getAudioFeatureList()[i].getAverage());
                }
            return builder.build();
    }

}
