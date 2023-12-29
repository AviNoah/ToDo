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

### View
The view is handles using XML under res/layout
- activity_add_account shows the user the **'sign in'** account screen, which allows you to sign in to an account already registered in the database and load his data and connect it to your current device id.
- activity_category_details shows the user the **'to-do list edit'** screen where you change a list's color and name.
- activity_login_screen is the **'main activity'** will be launched first, shows the user the 'select user' screen and the go to 'sign in' screen button or the go to 'manage accounts' button, if there are no accounts signed in, it will move the user directly to the 'sign in' screen **automatically**.
- activity_manage_accounts shows the user the **'manage accounts'** screen, will allows you to remove and add users currently registered to the device id of the device you are currently using.
- activity_register shows the user the **'register account'** screen, which allows you to register an account to the database, and also to your current device id.
- activity_task_details shows the **'task's details screen'** which allows you to modify tasks details like note, title, is important, is in my day, due date.
- activity_tasks_screen shows the **'selected list screen'** which shows the tasks inside a list you've selected to view, it contains a hamburger menu on the top to export import or print a list, add a task, or click to view tasks.
- custom_layout_user_profile is a generated view that inflates with user profile list info, it will show the user's generated profile picture which is the user's name initials on a random color background and his Email address and name.

#### Inflator classes
Classes that inflate data into views
- AdapterUserProfileList handles inflating the custom_layout_user_profile xml view with user's email address and creating a profile picture based on the name initials.
- NotificationAdapter handles inflating data onto notifications to notify users when their tasks are due outside of the app.
- PDFDocumentAdapter extends android printDocumentAdapter to handle sending files to the device's default print view.
- PrintHelper handles requesting storage write permission to store the generated PDF file of the list you wish to print, it then uses PDFDocumentAdapter to move you to device's default print view. 