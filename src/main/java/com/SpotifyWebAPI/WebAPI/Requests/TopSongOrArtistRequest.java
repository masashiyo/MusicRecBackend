package com.SpotifyWebAPI.WebAPI.Requests;

public class TopSongOrArtistRequest {
    private int limit;
    private int offset;
    private String timeRange;

    public int getLimit() {
        return limit;
    }
    public int getOffset(){
        return offset;
    }

    public String getTimeRange(){
        return timeRange;
    }
}
