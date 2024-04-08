package com.SpotifyWebAPI.WebAPI.Requests;

import com.SpotifyWebAPI.WebAPI.Configs.AudioFeaturesObject;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.neovisionaries.i18n.CountryCode;

import java.util.ArrayList;

public class TrackRecommendationRequest {
    private int limit;
    private CountryCode market = CountryCode.US;
    private String tracks;
    @JsonProperty("audioFeaturesList")
    private AudioFeaturesObject[] audioFeaturesList;

    public int getLimit() {
        return limit;
    }
    public CountryCode getMarket() { return market; }
    public String getTracks() { return tracks; }
    public AudioFeaturesObject[] getAudioFeatureList() { return audioFeaturesList; }

}
