package com.android.popularmovies.popularmovies.entity;

import com.google.gson.annotations.SerializedName;

/**
 * Created by lei on 6/27/17.
 */

public class Review {
    /**
     * id : 58d04679c3a3682dcd0002c6
     * author : Salt-and-Limes
     * content : **Spoilers**

     The live action remake of "Beauty and the Beast" was good, but it failed to capture the magic of the cartoon version. There were somethings that they got right, and others that dragged on.

     I thought "Be Our Guest" was done beautifully. The 3d made it even more enchanting. The main characters' backstories also added some depth to them. However, there were some scenes that I felt added nothing to the story. Such as the search for Belle by Gaston and her father. The "No one is like Gaston" scene didn't have the bravado or arrogance of the original.

     I also felt that Luke Evans was miscast. He wasn't the handsomest guy in town, nor was he the strongest. Which is why it was hard for me to accept him as the character. Emma Watson was serviceable. Her voice was fine, but it wasn't strong enough to carry Belle's songs. Dan Stevens was the best part of the film. I felt that he should have had more songs, because he has a beautiful baritone. Although his beast costume should have been more frightening.

     Overall, it's a fun film to watch. Though, I wouldn't call it a classic.
     * url : https://www.themoviedb.org/review/58d04679c3a3682dcd0002c6
     */

    @SerializedName("id")
    private String id;
    @SerializedName("author")
    private String author;
    @SerializedName("content")
    private String content;
    @SerializedName("url")
    private String url;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}