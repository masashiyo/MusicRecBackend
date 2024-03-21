package com.SpotifyWebAPI.WebAPI;

import com.neovisionaries.i18n.CountryCode;

public class TrackRecommendationRequest {
    private int limit;
    private CountryCode market = CountryCode.SE;
    private String tracks;

    public int getLimit() {
        return limit;
    }
    public CountryCode getMarket() { return market; }
    public String getTracks() { return tracks; }

}
