package com.example.todo;

public class UserProfileList {
    //A class that contains user information
    private String name, surname, password, email;

    public UserProfileList(String name, String surname, String email, String password) {
        this.name = name;
        this.surname = surname;
        this.email = email;
        this.password = password;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFullName() {
        //Returns the full name of the user
        return this.name + " " + this.surname;
    }

    public String getInitials() {
        //Returns the initials of the user.
        char a = this.name.charAt(0);
        char b = this.surname.charAt(0);
        String temp = "";
        temp += a;
        temp += b;
        return temp.toUpperCase();
    }

    @Override
    public String toString() {
        String str = this.getFullName();
        str += "\n" + this.email;
        str += "\n" + this.password;
        return str;
    }
}
