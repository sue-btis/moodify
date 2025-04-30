package com.haswe.moodify.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data //getters and setters
@AllArgsConstructor //final constructor
@NoArgsConstructor //default constructor
public class Song {

    private String id;
    private String title;
    private String artist;
    private String imageUrl;
    private String uri;

}
