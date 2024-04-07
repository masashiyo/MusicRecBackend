package com.SpotifyWebAPI.WebAPI.Configs;

public class AudioFeaturesObject {
    private float average;
    private String category;
    private String categoryDisplayName;

    public AudioFeaturesObject(float average, String category, String categoryDisplayName) {
        this.average = average;
        this.category = category;
        this.categoryDisplayName = categoryDisplayName;
    }
    public float getAverage() {
        return average;
    }

    public void setAverage(float average) {
        this.average = average;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getCategoryDisplayName() {
        return categoryDisplayName;
    }

    public void setCategoryDisplayName(String categoryDisplayName) {
        this.categoryDisplayName = categoryDisplayName;
    }
}
