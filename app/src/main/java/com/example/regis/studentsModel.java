package com.example.regis;

public class studentsModel {

    String name , course , duration , rollno , profileImage;

    public studentsModel(String name, String course, String duration, String rollno, String profileImage) {
        this.name = name;
        this.course = course;
        this.duration = duration;
        this.rollno = rollno;
        this.profileImage = profileImage;
    }

    public studentsModel() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCourse() {
        return course;
    }

    public void setCourse(String course) {
        this.course = course;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public String getRollno() {
        return rollno;
    }

    public void setRollno(String rollno) {
        this.rollno = rollno;
    }

    public String getProfileImage() {
        return profileImage;
    }

    public void setProfileImage(String profileImage) {
        this.profileImage = profileImage;
    }
}
