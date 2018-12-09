package com.example.potter.nct;

public class Song {
    private String songName, songPath, songArtist, songAlbum;

    public Song(String songName, String songPath, String songArtist, String songAlbum) {
        this.songName = songName;
        this.songPath = songPath;
        this.songArtist = songArtist;
        this.songAlbum = songAlbum;
    }

    public String getSongName() {
        return songName;
    }

    public void setSongName(String songName) {
        this.songName = songName;
    }

    public String getSongPath() {
        return songPath;
    }

    public void setSongPath(String songPath) {
        this.songPath = songPath;
    }

    public String getSongArtist() {
        return songArtist;
    }

    public void setSongArtist(String songArtist) {
        this.songArtist = songArtist;
    }

    public String getSongAlbum() {
        return songAlbum;
    }

    public void setSongAlbum(String songAlbum) {
        this.songAlbum = songAlbum;
    }
}
