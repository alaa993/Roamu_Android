package com.alaan.roamu.pojo;

import java.io.Serializable;
import java.lang.Long;
import java.sql.Timestamp;
import java.util.Date;

public class Post implements Serializable{
    public String id;
    public UserProfile author;
    public String text;
    public String type;
    public String privacy;

    public Long timestamp;

    public Post()
    {
    }

    public Post(String id, UserProfile author, String Text, Long timestamp,String type, String privacy)
    {
        this.id = id;
        this.author = author;
        this.text = text;

        this.timestamp = timestamp;
        this.type = type;
        this.privacy = privacy;
    }
}
