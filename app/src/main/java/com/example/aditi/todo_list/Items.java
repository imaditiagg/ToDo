package com.example.aditi.todo_list;

public class Items {
    private long id;
    private String title;
    private String description;
    private String date;
    private String time;
    private String category;
    private int  important=0;
    private int completed=0;


    public void setImportant(int important) {
        this.important = important;
    }

    public void setCompleted(int completed) {
        this.completed = completed;
    }

    public int isImportant() {
        return important;
    }

    public int isCompleted() {
        return completed;
    }




    public Items(String title, String description, String date, String time, String category) {
        this.title = title;
        this.description = description;
        this.date=date;
        this.time=time;
        this.category=category;

    }
    public void setCategory(String category) {
        this.category = category;
    }

    public String getCategory() {

        return category;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getDate() {

        return date;
    }

    public String getTime() {
        return time;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
