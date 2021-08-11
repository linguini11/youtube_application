package com.google;

import java.util.Comparator;

public class SortVideosByTitle implements Comparator<Video> {

  @Override
  public int compare(Video video1, Video video2) {
    return video1.getTitle().compareTo(video2.getTitle());
  }

}