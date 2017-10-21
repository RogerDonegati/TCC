package com.tcc.zeugmaproject.zeugmaproject.Model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Video {
    @SerializedName("results")
    private List<LinkVideo> links_video;


    public List<LinkVideo> getLinks_video() {
        return links_video;
    }

    public void setLinks_video(List<LinkVideo> links_video) {
        this.links_video = links_video;
    }
}