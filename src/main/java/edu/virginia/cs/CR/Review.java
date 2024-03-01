package edu.virginia.cs.CR;

import java.lang.Math.*;

public class Review {
    private int id;
    private int studentId;
    private int courseId;
    private String text;
    private int rating;

    public Review(int studentId, int courseId, String text, int rating) {
        this.studentId = studentId;
        this.courseId = courseId;
        this.text = text;
        this.rating = rating;
    }

    public int getStudentId(){
        return studentId;
    }

    public void setStudentId(int id){
        this.studentId = id;
    }

    public int getCourseId(){
        return courseId;
    }

    public void setCourseId(int id){
        this.courseId = id;
    }
    public String getMessage(){
        return text;
    }

    public void setMessage(String text){
        this.text = text;
    }

    public int getRating(){
        return rating;
    }

    public void setRating(int rating){
        if(isValidRating(rating)){
            this.rating = rating;
        }
        else{
            throw new IllegalArgumentException();
        }
    }

    public boolean isValidRating(int rating){
        if (rating < 1 || rating > 5 || (rating - Math.floor(rating)) != 0){
            return false;
        }
        else{
            return true;
        }
    }

}