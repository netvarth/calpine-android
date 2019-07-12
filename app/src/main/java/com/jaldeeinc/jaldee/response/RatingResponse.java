package com.jaldeeinc.jaldee.response;

import java.util.ArrayList;

/**
 * Created by sharmila on 7/11/18.
 */

public class RatingResponse {

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
