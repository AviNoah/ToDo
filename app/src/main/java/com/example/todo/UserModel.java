package com.example.todo;

import android.content.Context;

import java.util.ArrayList;

public class UserModel extends UserProfileList {
    //User security

    private long id; //ID received from database.
    private ArrayList<TasksCategory> lists;


    public UserModel(String name, String surname, String email, String password, Context context) {
        //CREATES NEW RECORD IN DATABASE
        super(name, surname, email, password);
        this.id = fetchID(context);
        this.lists = new ArrayList<TasksCategory>();
    }

    public UserModel(String name, String surname, String email, String password, long id) {
        super(name, surname, email, password);
        this.id = id;
        this.lists = new ArrayList<TasksCategory>();
    }

    private long fetchID(Context context)
    {
        //Adds record to table and returns generated ID.
        DBHelper helper = new DBHelper(context);
        long id = helper.insertIntoTblUser(this);
        return id;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public ArrayList<TasksCategory> getLists() {
        return lists;
    }

    public void setLists(ArrayList<TasksCategory> lists) {
        this.lists = lists;
    }

    public void addLists(ArrayList<TasksCategory> lists, Context context) {
        for(TasksCategory list: lists)
        {
            this.addList(list, context);
        }
    }

    public void addList(TasksCategory l, Context context)
    {
        //Links a list to the account.
        DBHelper helper = new DBHelper(context);
        if(helper.checkIfListIsConnectedToUser(l.getId(), this.id)) return; // if list is already connected, return.
        this.lists.add(l);
        helper.insertIntoTblListsUsers(this.id, l.getId()); //Link between user and list.
    }

    public void removeList(TasksCategory l, Context context)
    {
        //Unlinks a list from the account.
        while(this.lists.contains(l))
            this.lists.remove(l);
        DBHelper helper = new DBHelper(context);
        helper.removeFromTblListsUsers(this.id, l.getId()); //Unlinks the connection between user and list.
    }

    public UserProfileList getUserProfileList()
    {
        UserProfileList upl = new UserProfileList(getName(), getSurname(), getEmail(), getPassword());
        return upl;
    }
}
