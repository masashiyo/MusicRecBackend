package com.SpotifyWebAPI.WebAPI.Controllers;

import com.SpotifyWebAPI.WebAPI.Configs.AudioFeaturesObject;
import org.springframework.stereotype.Service;
import se.michaelthelin.spotify.model_objects.specification.AudioFeatures;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;

@Service
public class SongFeatureService {

    private void loadListWithCommonAudioFeaturesObject(Float[] values, String lowValue, String middleValue, String highValue, String category, ArrayList<AudioFeaturesObject> audioFeaturesList) {
        int low = 0;
        int middle = 0;
        int high = 0;
        float average = 0;
        for(int i = 0; i < values.length; i++) {
            average += values[i];
            if(values[i] < 0.4) {
                low++;
            } else if (values[i] <= 0.6) {
                middle++;
            } else {
                high++;
            }
        }
        average = average/values.length;
        if(low == values.length)
            audioFeaturesList.add(new AudioFeaturesObject(average, category, lowValue));
        else if(middle == values.length)
            audioFeaturesList.add(new AudioFeaturesObject(average, category, middleValue));
        else if (high == values.length)
            audioFeaturesList.add(new AudioFeaturesObject(average, category, highValue));
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
        loadListWithCommonAudioFeaturesObject(danceability, "Low Danceability", "Neutal Danceability", "High Dancebility", "danceability", audioFeatureList);
        loadListWithCommonAudioFeaturesObject(valence, "Sad", "Happy and Sad Mix", "Happy", "valence", audioFeatureList);
        loadListWithCommonAudioFeaturesObject(energy, "Low Energy", "Neutral Energy", "High Energy", "energy", audioFeatureList);
        loadListWithCommonAudioFeaturesObject(vocal, "High Vocal Mix", "Vocal and Instrumental Mix", "High Instrumental Mix", "instrumentalness", audioFeatureList);
        return audioFeatureList;
    }
}
