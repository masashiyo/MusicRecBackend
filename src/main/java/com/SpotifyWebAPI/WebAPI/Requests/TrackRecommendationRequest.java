package com.SpotifyWebAPI.WebAPI.Requests;

import com.SpotifyWebAPI.WebAPI.Configs.AudioFeaturesObject;
import com.neovisionaries.i18n.CountryCode;

import java.util.ArrayList;

public class TrackRecommendationRequest {
    private int limit;
    private CountryCode market = CountryCode.US;
    private String tracks;
    private ArrayList<AudioFeaturesObject> list;

    public int getLimit() {
        return limit;
    }
    public CountryCode getMarket() { return market; }
    public String getTracks() { return tracks; }
    public ArrayList<AudioFeaturesObject> getAudioFeature() { return list; }

}
