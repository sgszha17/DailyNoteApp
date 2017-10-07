package com.example.dailynote;


import java.io.Serializable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by David on 2017/10/5.
 * to be finished
 */

public class Note implements Comparable<Note>, Serializable{
    String date;
    String title;
    String content;
    Date d;
    DateFormat df = new SimpleDateFormat("yyyy.MMM.dd");

    public Note(){
    }

    public Note(String title, String content, Date d) {
        this.title = title;
        this.content = content;
        this.d = d;
        date = df.format(d);
    }


    @Override
    public int compareTo(Note o) {
        if (d == null || o.d == null)
            return 0;
        return d.compareTo(o.d);
    }
}
