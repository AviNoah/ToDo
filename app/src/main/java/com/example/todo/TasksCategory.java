package com.example.todo;

import android.content.Context;

import java.util.ArrayList;

public class TasksCategory {
    //A class that stores a set of tasks.
    private long id; //ID received from database.
    private ArrayList<Task> tasks;
    private String title;
    private int colorRID;

    //Some constructors that require the Context object will create a new ID and be added to lists table.
    public TasksCategory(ArrayList<Task> tasks, String title, int colorRID, long id) {
        this.tasks = tasks;
        this.title = title;
        this.colorRID = colorRID;
        this.id = id;
    }

    public TasksCategory(String title, int colorRID, long id) {
        //creates an empty new category.
        this(new ArrayList<Task>(), title, colorRID, id);
    }

    public TasksCategory(ArrayList<Task> tasks, String title, int colorRID) {
        //Create tasks category object. without adding to database, with a list.
        this(title, colorRID, -1);
        this.tasks = tasks;
    }

    public TasksCategory(String title, int colorRID) {
        //Create tasks category object. without adding to database, with an empty list.
        this(title, colorRID, -1);
        this.tasks = new ArrayList<Task>();
    }

    public ArrayList<Task> getTasks() {
        return tasks;
    }

    public void setTasks(ArrayList<Task> tasks) {
        this.tasks = tasks;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getColorRID() {
        return colorRID;
    }

    public void setColorRID(int colorRID) {
        this.colorRID = colorRID;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    //Task management
    public void addTask(Task t, Context context) {
        //Adds a task to the end of the list.
        this.tasks.add(t);
        DBHelper helper = new DBHelper(context);
        helper.insertIntoTblTasksLists(this.id, t.getId()); //Link between user and list.
    }

    public void removeTask(Task t, Context context) {
        //Removes a task and unlinks from list.
        while (this.tasks.contains(t))
            this.tasks.remove(t);
        DBHelper helper = new DBHelper(context);
        helper.removeFromTblTasksLists(this.id, t.getId()); //Link between user and list.
    }
}
