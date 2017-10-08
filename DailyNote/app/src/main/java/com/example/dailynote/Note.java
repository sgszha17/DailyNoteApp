/**
 * @(Note).java     1.0 08/10/2017
 *
 * Copyright 2017 University of Melbourne. All rights reserved.
 *
 * @author Dawei Wang
 * @email daweiw@student.unimelb.edu.au
 *
 * @author Siyu Zhang
 * @email siyuz6@student.unimelb.edu.au
 *
 * @author Tong Zou
 * @email tzou2@student.unimelb.edu.au
 *
 **/
package com.example.dailynote;


import java.io.Serializable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

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
