package com.jaldeeinc.jaldee.response;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by sharmila on 7/11/18.
 */

public class RatingResponse implements Serializable {

    int stars;

    ArrayList<RatingResponse>feedback;
    String comments;

    public int getStars() {
        return stars;
    }

    public ArrayList<RatingResponse> getFeedback() {
        return feedback;
    }

    public String getComments() {
        return comments;
    }
}
