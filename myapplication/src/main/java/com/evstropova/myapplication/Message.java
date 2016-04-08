package com.evstropova.myapplication;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by evstropova on 07.04.2016.
 */
public class Message {
    private long id;
    private String name;
    private List<String> genres;
    private long tracks;
    private long albums;
    private String link;
    private String description;
    private Cover cover;

    public Message(long id, String name, ArrayList<String> genres, long tracks,
                   long albums, String link, String description, Cover cover) {
        this.id = id;
        this.name = name;
        this.genres = genres;
        this.tracks = tracks;
        this.albums = albums;
        this.link = link;
        this.description = description;
        this.cover = cover;
    }

    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public List getGenres() {
        return genres;
    }

    public long getTracks() {
        return tracks;
    }

    public long getAlbums() {
        return albums;
    }

    public String getLink() {
        return link;
    }

    public String getDescription() {
        return description;
    }

    public Cover getCover() {
        return cover;
    }

    public String getGenresString() {
        StringBuilder stringBuilder = new StringBuilder();

        for (String genre: genres) {
            stringBuilder.append(genre);
        }

        return stringBuilder.toString();
    }
}
