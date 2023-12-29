## A simple local task management app for android devices from android 11+.

- The app allows you to create and manage users on the app using a local sqlite database
- For each user, you may create lists with unique names and colors, in addition to your personal day, important and planned lists.
- You may export and import or share lists of other users, which will statically copy or dynamically update respective to your choice.
- Inside each list you may add tasks and specify due date, importance, text, notes which will add them to your personal day, important or planned lists depending on your changes
- Support for printing lists

This project uses a MVC model design pattern and was made for my highschool project in 2022
it was made using Android studio Arctic Fox v2020.3.1 in Java

# Documentation

### Model
- UserProfileList class stores personal data related to the user
- UserModel connects the UserProfileList data to a unique identifier in the database and its linked todo lists
- TasksCategory handles data queried from the database related to to-do lists, it stores the unique identifier related to it in the database and the Task objects of the list 
- Task handles data queries from the database related to to-do tasks, it stores the unique identifier related to the task and its contents