package com.google;

import java.util.ArrayList;

/** A class used to represent a Playlist */
class VideoPlaylist {


  private String name;
  private ArrayList<String> playlistVideos;


  public VideoPlaylist(String playlistName) {
    this.name = playlistName;
    this.playlistVideos = new ArrayList<String>();
  }

  public Boolean containsVideo(String videoId) {
    return playlistVideos.contains(videoId);
  }

  public void addVideo(String videoId) {
    playlistVideos.add(videoId);
  }

  public int getSize() {
    return playlistVideos.size();
  }

  public String getName() {
    return name;
  }

  public void clear() {
    playlistVideos.clear();
  }

  public void delete() {
  }
  
  public void removeVideo(String videoId) {
    playlistVideos.remove(videoId);
  }

  public String getVideoId(int index) {
    return playlistVideos.get(index);
  }

}
