package com.google;

import java.util.*;

public class VideoPlayer {

  private final VideoLibrary videoLibrary;
  private Video currentVideo;
  private Boolean isPlaying;
  private HashMap<String, VideoPlaylist> playlists;

  public VideoPlayer() {
    this.videoLibrary = new VideoLibrary();
    this.currentVideo = null;
    this.isPlaying = false;
    this.playlists = new HashMap<>();
  }

  public void numberOfVideos() {
    System.out.printf("%s videos in the library%n", videoLibrary.getVideos().size());
  }

  public void showAllVideos() {
    System.out.println("Here's a list of all available videos:");
    List<Video> videos = videoLibrary.getVideos();
    Collections.sort(videos, new SortVideosByTitle());
    String flagMessage;
    String videoInfo;
    String videoId;
    Boolean isFlagged;
    for (int i = 0; i < videos.size(); i++) {
      videoInfo = videos.get(i).getTitle() + " (" + videos.get(i).getVideoId() + ") [" + String.join(" ", videos.get(i).getTags()) + "]";
      videoId = videos.get(i).getVideoId();
      isFlagged = videoLibrary.isVideoFlagged(videos.get(i).getVideoId());
      flagMessage = "";
      if (isFlagged) {
        flagMessage = " - FLAGGED (reason: " + videoLibrary.getFlagReason(videoId) + ")";
      }
      System.out.println(videoInfo + flagMessage);
    }
  }

  public void playVideo(String videoId) {
    if (videoLibrary.getVideo(videoId) == null) {
      System.out.println("Cannot play video: Video does not exist");
    }
    else if (videoLibrary.isVideoFlagged(videoId)) {
       System.out.println("Cannot play video: Video is currently flagged (reason: " + videoLibrary.getFlagReason(videoId) + ")");
    }
    else { 
      if (currentVideo != null) {
        System.out.println("Stopping video: " + currentVideo.getTitle());
      }
      currentVideo = videoLibrary.getVideo(videoId);
      isPlaying = true;
      System.out.println("Playing video: " + currentVideo.getTitle());
    }
  }

  public void stopVideo() {
    if (this.currentVideo == null) {
      System.out.println("Cannot stop video: No video is currently playing");
    }
    else {
      System.out.println("Stopping video: " + currentVideo.getTitle());
      isPlaying = false;
      currentVideo = null;
    }
  }

  public void playRandomVideo() {
    List<Video> videos = videoLibrary.getUnflaggedVideos();
    if (videos.size() == 0) {
      System.out.println("No videos available");
      return;
    }
    if (this.currentVideo != null) {
      stopVideo();
    }
    Random r = new Random();
    int randomIndex = r.nextInt(videos.size());
    String randomId = videos.get(randomIndex).getVideoId();
    playVideo(randomId);
  }

  public void pauseVideo() {
    if (this.currentVideo == null) {
      System.out.println("Cannot pause video: No video is currently playing");
    }
    else if (!isPlaying) {
      System.out.println("Video already paused: " + currentVideo.getTitle());
    }
    else {
      isPlaying = false;
      System.out.println("Pausing video: " + currentVideo.getTitle());
    }
  }

  public void continueVideo() {
    if (currentVideo == null) {
      System.out.println("Cannot continue video: No video is currently playing");
    }
    else if (isPlaying) {
      System.out.println("Cannot continue video: Video is not paused");
    }
    
    else {
      isPlaying = true;
      System.out.println("Continuing video: " + currentVideo.getTitle());
    }
  }

  public void showPlaying() {
    if (currentVideo == null) {
      System.out.println("No video is currently playing");
    }
    else {
      String message = "";
      if (!isPlaying) {
        message = " - PAUSED";
      }
      System.out.println("Currently playing: " + currentVideo.getTitle() + " (" + currentVideo.getVideoId() + ") [" + String.join(" ", currentVideo.getTags()) + "]" + message);
    }
  }

  public void createPlaylist(String playlistName) {
    if (playlists.containsKey(playlistName.toLowerCase())) {
      System.out.println("Cannot create playlist: A playlist with the same name already exists");
    }
    else {
      playlists.put(playlistName.toLowerCase(), new VideoPlaylist(playlistName));
      System.out.println("Successfully created new playlist: " + playlistName);
    }
  }

  public void addVideoToPlaylist(String playlistName, String videoId) {
    Video video = videoLibrary.getVideo(videoId);    
    if (!playlists.containsKey(playlistName.toLowerCase())) {
      System.out.print("Cannot add video to " + playlistName + ": Playlist does not exist. ");
      //asking for input causes error with Part2Test
      /*System.out.print("Do you want to create a playlist called " + playlistName + "? ");
      System.out.println("If your answer is not yes, we will assume it's a no.");
      var scanner = new Scanner(System.in);
      String input = scanner.next();
      if (input.toLowerCase().equals("yes")) {
        createPlaylist(playlistName);
      }*/
    } 
    else if (video == null) {
      System.out.println("Cannot add video to " + playlistName + ": Video does not exist");
    }
    else if (videoLibrary.isVideoFlagged(videoId)) {
      System.out.println("Cannot add video to " + playlistName + ": Video is currently flagged (reason: " + videoLibrary.getFlagReason(videoId) + ")");
    }
    else if (playlists.get(playlistName.toLowerCase()).containsVideo(videoId)) {
      System.out.println("Cannot add video to " + playlistName + ": Video already added"); 
    }
    else {
      System.out.println("Added video to " + playlistName + ": " + video.getTitle());
      playlists.get(playlistName.toLowerCase()).addVideo(videoId);
    }
  }

  public void showAllPlaylists() {
    if (playlists.size() == 0) {
      System.out.println("No playlists exist yet");
    }
    else {
      System.out.println("Showing all playlists:");
      Set<String> keys = playlists.keySet();
      List<String> keysList = new ArrayList<String>(keys);
      Collections.sort(keysList, new SortPlaylistsByTitle());
      String videoDescriptor;
     
      for (int i = 0; i < keysList.size(); i++) {
        if (playlists.get(keysList.get(i)).getSize() == 1) {
          videoDescriptor = "video";
        }
        else {
          videoDescriptor = "videos";
        }
        System.out.println(keysList.get(i) + " (" + playlists.get(keysList.get(i)).getSize() + " " + videoDescriptor + ")");
      }
    }
  }

  public void showPlaylist(String playlistName) {
    if (!playlists.containsKey(playlistName.toLowerCase())) {
      System.out.println("Cannot show playlist " + playlistName + ": Playlist does not exist");
      //asking for input causes error with Part2Test
      /*System.out.println("Do you want to create a playlist called " + playlistName + "?");
      System.out.println("If your answer is not yes, we will assume it's a no.");
      var scanner = new Scanner(System.in);
      var input = scanner.next();
      if (input.toLowerCase().equals("yes")) {
        createPlaylist(playlistName);
      }*/
    }
    else {
      System.out.println("Showing playlist: " + playlistName);
      VideoPlaylist playlist = playlists.get(playlistName.toLowerCase());
      if (playlist.getSize() == 0) {
        System.out.println("No videos here yet");
      }
      else {
        String videoId;
        Video video;
        String flagMessage;
        String videoInfo;
        Boolean isFlagged;
        for (int i = 0; i < playlist.getSize(); i++) {
          videoId = playlist.getVideoId(i);
          video = videoLibrary.getVideo(videoId);
          videoInfo = video.getTitle() + " (" + videoId + ") [" + String.join(" ", video.getTags()) + "]";
          isFlagged = videoLibrary.isVideoFlagged(videoId);
          flagMessage = "";
          if (isFlagged) {
            flagMessage = " - FLAGGED (reason: " + videoLibrary.getFlagReason(videoId) + ")";
          }
          System.out.println(videoInfo + flagMessage);
        }
      }
    }
  }

  public void removeFromPlaylist(String playlistName, String videoId) {
    Video video = videoLibrary.getVideo(videoId);    
    if (!playlists.containsKey(playlistName.toLowerCase())) {
      System.out.println("Cannot remove video from " + playlistName + ": Playlist does not exist");
      //asking for input causes error with Part2Test
      /*System.out.println("Do you want to create a playlist called " + playlistName + "?");
      System.out.println("If your answer is not yes, we will assume it's a no.");
      var scanner = new Scanner(System.in);
      var input = scanner.next();
      if (input.toLowerCase().equals("yes")) {
        createPlaylist(playlistName);
      }*/
    } 
    else if (video == null) {
      System.out.println("Cannot remove video from " + playlistName + ": Video does not exist");
    }
    else if (!playlists.get(playlistName.toLowerCase()).containsVideo(videoId)){
      System.out.println("Cannot remove video from " + playlistName + ": Video is not in playlist");
    }
    else {
      playlists.get(playlistName.toLowerCase()).removeVideo(videoId);
      System.out.println("Removed video from " + playlistName + ": " + video.getTitle());
    }
  }

  public void clearPlaylist(String playlistName) {
    if (!playlists.containsKey(playlistName.toLowerCase())) {
      System.out.println("Cannot clear playlist " + playlistName + ": Playlist does not exist");
      //asking for input causes error with Part2Test
      /*System.out.println("Do you want to create a playlist called " + playlistName + "?");
      System.out.println("If your answer is not yes, we will assume it's a no.");
      var scanner = new Scanner(System.in);
      var input = scanner.next();
      if (input.toLowerCase().equals("yes")) {
        createPlaylist(playlistName);
      }*/
    }
    else {
      playlists.get(playlistName.toLowerCase()).clear();
      System.out.println("Successfully removed all videos from " + playlistName);
    }
  }

  public void deletePlaylist(String playlistName) {
    if (!playlists.containsKey(playlistName.toLowerCase())) {
      System.out.println("Cannot delete playlist " + playlistName + ": Playlist does not exist");
      //asking for input causes error with Part2Test
      /*System.out.println("Do you want to create a playlist called " + playlistName + "?");
      System.out.println("If your answer is not yes, we will assume it's a no.");
      var scanner = new Scanner(System.in);
      var input = scanner.next();
      if (input.toLowerCase().equals("yes")) {
        createPlaylist(playlistName);
      }*/
    }
    else {
      playlists.get(playlistName.toLowerCase()).delete();
      System.out.println("Deleted playlist: " + playlistName);
    }
  }

  public void searchVideos(String searchTerm) {
    List<Video> videos = videoLibrary.getUnflaggedVideos();
    List<Video> foundVideos = new ArrayList<Video>();
    for (Video video : videos) {
      if (video.getTitle().toLowerCase().contains(searchTerm.toLowerCase())) {
        foundVideos.add(video);
      }
    }
    if (foundVideos.size() == 0) {
      System.out.println("No search results for " + searchTerm);
    }
    else {
      System.out.println("Here are the results for " + searchTerm + ":");
      Collections.sort(foundVideos, new SortVideosByTitle());
      for (int i = 0; i < foundVideos.size(); i++) {
          Video video = foundVideos.get(i);
          System.out.println((i + 1) + ") " + video.getTitle() + " (" + video.getVideoId() + ") [" + String.join(" ", video.getTags()) + "]");
       }
      System.out.println("Would you like to play any of the above? If yes, specify the number of the video.");
      System.out.println("If your answer is not a valid number, we will assume it's a no.");
      var scanner = new Scanner(System.in);
      var input = scanner.next();
      int choice;
      try {
        choice = Integer.parseInt(input);
      }
      catch (NumberFormatException e) {
        return;
      }
      if (choice >= 1 && choice <= foundVideos.size()) {
        playVideo(foundVideos.get(choice - 1).getVideoId());
      }
    }
  }
  
  public void searchVideosWithTag(String videoTag) {
    if (videoTag.charAt(0) != '#') {
      System.out.println("No search results for " + videoTag);
      return;
    }
    String searchTag = videoTag.substring(1).toLowerCase();
    List<Video> videos = videoLibrary.getUnflaggedVideos();
    List<Video> foundVideos = new ArrayList<Video>();
    for (Video video : videos) {
      List<String> tags = video.getTags();
      for (String tag : tags) {
        if (tag.substring(1).equals(searchTag)) {
          foundVideos.add(video);
        }     
      } 
    }
    if (foundVideos.size() == 0) {
      System.out.println("No search results for " + videoTag);
    }
    else {
      System.out.println("Here are the results for " + videoTag + ":");
      Collections.sort(foundVideos, new SortVideosByTitle());
      for (int i = 0; i < foundVideos.size(); i++) {
          Video video = foundVideos.get(i);
          System.out.println((i + 1) + ") " + video.getTitle() + " (" + video.getVideoId() + ") [" + String.join(" ", video.getTags()) + "]");
      }
      System.out.println("Would you like to play any of the above? If yes, specify the number of the video.");
      System.out.println("If your answer is not a valid number, we will assume it's a no.");
      var scanner = new Scanner(System.in);
      var input = scanner.next();
      int choice;
      try {
        choice = Integer.parseInt(input);
      }
      catch (NumberFormatException e) {
        return;
      }
      if (choice >= 1 && choice <= foundVideos.size()) {
        playVideo(foundVideos.get(choice - 1).getVideoId());
      }
    }
  }

  public void flagVideo(String videoId) {
    flagVideo(videoId, "Not supplied");
  }

  public void flagVideo(String videoId, String reason) {
    Video video = videoLibrary.getVideo(videoId);  
    if (video == null) {
      System.out.println("Cannot flag video: Video does not exist");
    }  
    else if (videoLibrary.isVideoFlagged(videoId)) {
      System.out.println("Cannot flag video: Video is already flagged");
    }
    else {
      if (video == currentVideo) {
        stopVideo();
      }
      videoLibrary.addFlag(videoId, reason);
      System.out.println("Successfully flagged video: " + video.getTitle() + " (reason: " + reason + ")");
    }
  }

  public void allowVideo(String videoId) {
    Video video = videoLibrary.getVideo(videoId);  
    if (video == null) {
      System.out.println("Cannot remove flag from video: Video does not exist");
    }
    else if (!videoLibrary.isVideoFlagged(videoId)) {
      System.out.println("Cannot remove flag from video: Video is not flagged");
    }
    else {
      videoLibrary.removeFlag(videoId);
      System.out.println("Successfully removed flag from video: " + video.getTitle());
    }
  }
}