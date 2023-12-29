## A simple local task management app for android devices from android 11+.

- The app allows you to create and manage users on the app using a local sqlite database
- For each user, you may create lists with unique names and colors, in addition to your personal day, important and planned lists.
- You may export and import or share lists of other users, which will statically copy or dynamically update respective to your choice.
- Inside each list you may add tasks and specify due date, importance, text, notes which will add them to your personal day, important or planned lists depending on your changes
- Support for printing lists

This project uses a MVC model design pattern and was made for my highschool project in 2022
it was made using Android studio Arctic Fox v2020.3.1 in Java

Please note that this project was made as a proof of concept, which is why user data is completely local and is stored unencrypted in the database

# Documentation

### Database
- The database has 3 Tables and 3 Relational tables
#### Data tables
- Tasks has entities containing Title, Note, as well as Day, Month Year integers for due date and is_important and is_in_my_day which are both booleans, as well as a unique auto incrementing identifier integer.
- Lists has entities containing Title and Color which are strings and a unique auto incrementing identifier integer.
- Users has entities containing Name, Surname, Email, Password and a unique auto incrementing identifier integer.

#### Relational tables
- Lists-Tasks has entities linking the unique task identifier to a unique list identifier, allowing the same task to exist in multiple lists of multiple users and be updated dynamically across them.
- Users-Lists has entities linking the unique list identifier to a unique user identifier, allowing the same list to exist in multiple users and be updated dynamically across all of them.
- Devices-Users handles linking all users linked to a unique device ID fetches from the phone itself, so if the database is copied to another phone of a different device ID it will have different users in it, this is more of a proof of concept to handle a network of phones using the app with the database being stored on a server.

### Model
- UserProfileList class handles data queried from the database related top personal user data, it also stores the unique identifier related to the user
- UserModel connects the UserProfileList data to a unique identifier in the database and its linked todo lists
- TasksCategory handles data queried from the database related to to-do lists, it stores the unique identifier related to it in the database and the Task objects of the list 
- Task handles data queried from the database related to to-do tasks, it stores the unique identifier related to the task and its contents