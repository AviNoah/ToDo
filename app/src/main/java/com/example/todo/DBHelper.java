package com.example.todo;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;
import android.util.Log;

import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.Locale;

public class DBHelper extends SQLiteOpenHelper {

    public static class USERS implements BaseColumns {
        public static final String TABLE_NAME = "USERTBL";
        public static final String NAME = "USERNAME"; //String representing user's real first name.
        public static final String SURNAME = "USERSURNAME"; //String representing user's real surname.
        public static final String EMAIL = "USEREMAIL"; //String representing user's email address.
        public static final String PASSWORD = "USERPASSWORD"; //String representing user's password.
        public static final String ID = "USER_ID"; //String representing user's unique generated ID.
    }

    public static class LISTS implements BaseColumns {
        public static final String TABLE_NAME = "LISTTBL";
        public static final String ID = "LIST_ID"; //Array of users the list is related to.
        public static final String TITLE = "TITLE"; //String representing list's title.
        public static final String COLOR = "COLOR"; //String representing list's color.
    }

    public static class TASKS implements BaseColumns {
        //Tasks table
        public static final String TABLE_NAME = "TASKTBL";
        public static final String TITLE = "TITLE"; //String representing task's title.
        public static final String NOTE = "NOTE"; //String representing task's side note.
        public static final String IS_IMPORTANT = "ISIMPORTANT"; //Boolean representing if task is marked important.
        public static final String IS_IN_MY_DAY = "ISINMYDAY"; //Boolean representing if task is marked in my day.

        public static class DUE_DATE implements BaseColumns {
            public static final String DAY = "DAY"; //String representing day of month.
            public static final String MONTH = "MONTH"; //String representing month, from  1 to 12 inclusive.
            public static final String YEAR = "YEAR"; //String representing year, in YYYY format.
        }

        public static final String ID = "TASK_ID"; //String representing task's unique generated ID.
    }

    public static class LISTS_TASKS implements BaseColumns {
        public static final String TABLE_NAME = "TASKSLISTSTBL";
        public static final String LIST_ID = LISTS.ID;
        public static final String TASK_ID = TASKS.ID;
    }

    public static class USERS_LISTS implements BaseColumns {
        public static final String TABLE_NAME = "LISTSUSERSTBL";
        public static final String LIST_ID = LISTS.ID;
        public static final String USER_ID = USERS.ID;
    }

    public static class DEVICES_USERS implements BaseColumns {
        //USERS-DEVICES table - creates relations
        // between activated users on devices.
        public static final String TABLE_NAME = "USERSDEVICESTBL";
        public static final String DEVICE_ID = "DEVICE_ID";
        public static final String USER_ID = USERS.ID;
    }

    public static class CREATE_STATEMENTS implements BaseColumns {
        public static final String SQLQueryUSERS =
                "CREATE TABLE " + USERS.TABLE_NAME + "(" +
                        USERS.ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                        USERS.NAME + " TEXT," +
                        USERS.SURNAME + " TEXT," +
                        USERS.EMAIL + " TEXT," +
                        USERS.PASSWORD + " TEXT)";

        public static final String SQLQueryLISTS =
                "CREATE TABLE " + LISTS.TABLE_NAME + "(" +
                        LISTS.ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                        LISTS.TITLE + " TEXT," +
                        LISTS.COLOR + " TEXT)";

        public static final String SQLQueryTASKS =
                "CREATE TABLE " + TASKS.TABLE_NAME + "(" +
                        TASKS.ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                        TASKS.TITLE + " TEXT," +
                        TASKS.NOTE + " TEXT," +
                        TASKS.DUE_DATE.DAY + " INTEGER," +
                        TASKS.DUE_DATE.MONTH + " INTEGER," +
                        TASKS.DUE_DATE.YEAR + " INTEGER," +
                        TASKS.IS_IMPORTANT + " INTEGER," +
                        TASKS.IS_IN_MY_DAY + " INTEGER)";

        public static final String SQLQueryLISTS_USERS =
                "CREATE TABLE " + USERS_LISTS.TABLE_NAME + "(" +
                        USERS_LISTS.LIST_ID + " INTEGER," +
                        USERS_LISTS.USER_ID + " INTEGER," +
                        "PRIMARY KEY (" + USERS_LISTS.LIST_ID + "," + USERS_LISTS.USER_ID + "))";

        public static final String SQLQueryTASKS_LISTS =
                "CREATE TABLE " + LISTS_TASKS.TABLE_NAME + "(" +
                        LISTS_TASKS.LIST_ID + " INTEGER," +
                        LISTS_TASKS.TASK_ID + " INTEGER," +
                        "PRIMARY KEY (" + LISTS_TASKS.LIST_ID + "," + LISTS_TASKS.TASK_ID + "))";

        public static final String SQLQueryUSERS_DEVICES =
                "CREATE TABLE " + DEVICES_USERS.TABLE_NAME + "(" +
                        DEVICES_USERS.USER_ID + " INTEGER," +
                        DEVICES_USERS.DEVICE_ID + " TEXT," +
                        "PRIMARY KEY (" + DEVICES_USERS.USER_ID + "," + DEVICES_USERS.DEVICE_ID + "))";

    }

    public static class databaseValues implements BaseColumns {
        public final static String[] Tables =
                {TASKS.TABLE_NAME, USERS.TABLE_NAME, LISTS.TABLE_NAME,
                        LISTS_TASKS.TABLE_NAME, USERS_LISTS.TABLE_NAME, DEVICES_USERS.TABLE_NAME};

        public static final String NAME = "AviDatabaseV8"; //Database's name.
        public static final int VER = 1; //Database's version
    }

    private static final String APOS = "'"; //String representing the TEXT APOSTROPHE, used to represent a string in an SQL statement.

    private SQLiteDatabase database; //Reference to database.

    public DBHelper(@Nullable Context context, @Nullable String name, @Nullable SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    public DBHelper(@Nullable Context context) {
        //Simple constructor that uses only context.
        super(context, databaseValues.NAME, null, databaseValues.VER);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        //Method executes only if getWritableDatabase() is called and tables don't exist. executes only once.
        try {
            db.execSQL(CREATE_STATEMENTS.SQLQueryTASKS);
            db.execSQL(CREATE_STATEMENTS.SQLQueryLISTS);
            db.execSQL(CREATE_STATEMENTS.SQLQueryUSERS);
            db.execSQL(CREATE_STATEMENTS.SQLQueryLISTS_USERS);
            db.execSQL(CREATE_STATEMENTS.SQLQueryTASKS_LISTS);
            db.execSQL(CREATE_STATEMENTS.SQLQueryUSERS_DEVICES);
        } catch (SQLException e) {
            Log.e("Db Error", e.toString());
            e.printStackTrace();
        }
    }

    public Cursor readStatement(String statement) {
        database = getReadableDatabase();
        try {
            //todo fix raw query issue, returns null instead of cursor.
            Cursor cursor = database.rawQuery(statement, null);
            if (cursor == null) return null;
            cursor.moveToFirst();
            return cursor;
        } catch (SQLException e) {
            Log.e(" Db error", e.toString());
            e.printStackTrace();
            return null;
        }
    }

    public void writeStatement(String statement) {
        database = getWritableDatabase();
        try {
            database.execSQL(statement);
            //database.rawQuery(statement, null);
        } catch (SQLException e) {
            Log.e(" Db error", e.toString());
            e.printStackTrace();
        }
    }

    public void updateStatement(String TABLE_NAME, ContentValues cv, String idColumn, String[] ids) {
        String whereStatement = idColumn + " = ?";
        database = getWritableDatabase();
        try {
            database.update(TABLE_NAME, cv, whereStatement, ids);
        } catch (SQLException e) {
            Log.e(" Db error", e.toString());
            e.printStackTrace();
        }
    }

    //Insert method; almost all of them return the generated id.
    public long insertStatement(String table_name, ContentValues cv) {
        //Inserts a record into a table, returns ID.
        database = getWritableDatabase();
        try {
            return database.insert(table_name, null, cv);
        } catch (SQLException e) {
            Log.d("Db error", e.toString());
            e.printStackTrace();
            return -1; //Unsuccessful
        }
    }

    public long insertIntoTblUser(UserModel um) {
        //An alternative method that gets a user model object and inserts it into the table.
        return this.insertIntoTblUser(um.getName(), um.getSurname(), um.getEmail(), um.getPassword());
    }

    public long insertIntoTblUser(String name, String surname, String email, String password) {
        //A method that inserts user record data into table, returns generated row id.
        ContentValues cv = new ContentValues();
        cv.put(USERS.NAME, name);
        cv.put(USERS.SURNAME, surname);
        cv.put(USERS.EMAIL, email);
        cv.put(USERS.PASSWORD, password);

        return insertStatement(USERS.TABLE_NAME, cv);
    }

    public long insertIntoTblLists(TasksCategory tc) {
        //Inserts a new list record from a TasksCategory object.
        String title = tc.getTitle();
        String color = tc.getColorRID() + "";

        return insertIntoTblLists(title, color);
    }

    public long insertIntoTblLists(String title, String color) {
        //Inserts a new list record into lists table.
        ContentValues cv = new ContentValues();
        cv.put(LISTS.TITLE, title);
        cv.put(LISTS.COLOR, color);

        return insertStatement(LISTS.TABLE_NAME, cv);
    }

    public long insertIntoTblTask(Task t) {
        String title = t.getTitle();
        String note = t.getContent();
        int d, m, y;
        d = t.getDay();
        m = t.getMonth();
        y = t.getYear();

        boolean is_important = t.isImportant();
        boolean is_in_my_day = t.isInMyDay();

        return insertIntoTblTask(title, note, d, m, y, is_important, is_in_my_day);
    }

    public long insertIntoTblTask(String title, String note, int d, int m, int y, boolean is_important, boolean is_in_my_day) {
        //Insert a task record into tasks table.
        ContentValues cv = new ContentValues();
        cv.put(TASKS.TITLE, title);
        cv.put(TASKS.NOTE, note);
        cv.put(TASKS.DUE_DATE.DAY, d);
        cv.put(TASKS.DUE_DATE.MONTH, m);
        cv.put(TASKS.DUE_DATE.YEAR, y);
        cv.put(TASKS.IS_IMPORTANT, is_important);
        cv.put(TASKS.IS_IN_MY_DAY, is_in_my_day);

        return insertStatement(TASKS.TABLE_NAME, cv);
    }

    public long insertIntoTblListsUsers(long user_id, long list_id) {
        //Creates a link between the user id and the list id, returns generated row id.
        ContentValues cv = new ContentValues();
        cv.put(USERS_LISTS.USER_ID, user_id);
        cv.put(USERS_LISTS.LIST_ID, list_id);

        return this.insertStatement(USERS_LISTS.TABLE_NAME, cv);
    }

    public long insertIntoTblTasksLists(long list_id, long task_id) {
        ContentValues cv = new ContentValues();
        cv.put(LISTS_TASKS.LIST_ID, list_id);
        cv.put(LISTS_TASKS.TASK_ID, task_id);

        return this.insertStatement(LISTS_TASKS.TABLE_NAME, cv);
    }

    public long insertIntoTblUsersDevices(long user_id, String device_id) {
        ContentValues cv = new ContentValues();
        cv.put(DEVICES_USERS.USER_ID, user_id);
        cv.put(DEVICES_USERS.DEVICE_ID, device_id);

        return this.insertStatement(DEVICES_USERS.TABLE_NAME, cv);
    }

    //Select methods
    public Cursor selectMostUpdatedBy(String Table_name, String column_value, String value, boolean ignoreCase) {
        //Returns the most updated result set entry for the input.
        //Input is Table name, column name, value.
        String selectQuery;
        selectQuery = "SELECT * FROM " + Table_name + " WHERE " + column_value + " = " +
                APOS + value + APOS + " LIMIT 1;";
        if(ignoreCase)
        {
            selectQuery = "SELECT * FROM " + Table_name + " WHERE lower(" + column_value + ") = " +
                    APOS + value.toLowerCase(Locale.ROOT) + APOS + " LIMIT 1;";
        }
        if (value.isEmpty()) {
            selectQuery = "SELECT FROM " + Table_name + " LIMIT 1;";
        }

        return readStatement(selectQuery);
    }



    public Cursor selectFieldFromAndWhere(String Field, String tblName, String whereField, String val) {
        String selectQuery = " SELECT " + Field + " FROM " + tblName + " WHERE " + whereField + " = " +
                APOS + val + APOS;
        database = getReadableDatabase();
        return readStatement(selectQuery);
    }

    public Cursor selectAllActiveUsersForDevice(String id) {
        //returns the active users signed in to a device.
        return selectFieldFromAndWhere(DEVICES_USERS.USER_ID, DEVICES_USERS.TABLE_NAME, DEVICES_USERS.DEVICE_ID, id);
    }

    public Cursor selectByEmailTheMostUpdated(String email) {
        //Returns the most updated result set entry for the input.
        return selectMostUpdatedBy(USERS.TABLE_NAME, USERS.EMAIL, email, true);
    }

    @SuppressLint("Range")
    public boolean checkPasswordForEmail(String email, String password) {
        //Returns true if password is correct for email.
        Cursor c = selectByEmailTheMostUpdated(email);
        return checkPasswordForEmail(c, password);
    }

    @SuppressLint("Range")
    public boolean checkPasswordForEmail(Cursor c, String password) {
        //Returns true if password is correct for email.
        //Cursor is user cursor
        if (c == null) return false; //Email doesn't exist
        database = getReadableDatabase();
        return password.equals(c.getString(c.getColumnIndex(USERS.PASSWORD)));
    }

    public boolean checkIfListIsConnectedToUser(long list_id, long user_id) {
        String query = "SELECT * FROM " + USERS_LISTS.TABLE_NAME + " WHERE "
                + USERS_LISTS.USER_ID + " = " + user_id + " AND "
                + USERS_LISTS.LIST_ID + " = " + list_id;

        return (readStatement(query).getCount() > 0); // if cursor count is greater than 0, it is connected.
    }

    public Cursor selectAllListsOfUserID(long id) {
        //Returns the cursor of all lists' ids linked to a user.
        return selectFieldFromAndWhere(USERS_LISTS.LIST_ID, USERS_LISTS.TABLE_NAME, USERS_LISTS.USER_ID, id + "");
    }

    public Cursor selectAllTasksOfListID(long id) {
        return selectFieldFromAndWhere(LISTS_TASKS.TASK_ID, LISTS_TASKS.TABLE_NAME, LISTS_TASKS.LIST_ID, id + "");
    }

    public Task selectTaskByID(long id) {
        String val = id + "";
        Cursor c = selectMostUpdatedBy(TASKS.TABLE_NAME, TASKS.ID, val, false);
        return this.toTask(c);
    }

    public TasksCategory selectListByID(long id) {
        String val = id + "";
        Cursor c = selectMostUpdatedBy(LISTS.TABLE_NAME, LISTS.ID, val, false);
        return this.toTasksCategory(c);
    }

    public UserModel selectUserByID(long id) {
        String val = id + "";
        Cursor c = selectMostUpdatedBy(USERS.TABLE_NAME, USERS.ID, val, false);
        return this.toUserModel(c);
    }

    public ArrayList<Task> selectAllTasksOfUser(long id) {
        Cursor c = selectAllListsOfUserID(id);
        //Cursor contains all lists related to user.
        ArrayList<Task> tasks = new ArrayList<Task>();

        if (c == null || c.getCount() == 0)
            return tasks;

        c.moveToFirst();

        //Run through all list id's.
        while (!c.isAfterLast()) {
            @SuppressLint("Range") long _id = c.getLong(0);
            TasksCategory t = this.selectListByID(_id);
            tasks.addAll(t.getTasks());
            c.moveToNext();
        }
        return tasks;
    }

    @SuppressLint("Range")
    public TasksCategory selectAllTasksDueTodayForUser(long id) {
        ArrayList<Task> tempTasks = this.selectAllTasksOfUser(id), tasks = new ArrayList<Task>();

        for (Task i : tempTasks)
            if (i.isInMyDay())
                tasks.add(i);

        //Package all into tasks category object.
        TasksCategory tc = new TasksCategory(tasks, "My day", R.color.light_blue); //ID-less object
        return tc;
    }

    @SuppressLint("Range")
    public TasksCategory selectAllTasksPlannedForUser(long id) {
        ArrayList<Task> tempTasks = this.selectAllTasksOfUser(id), tasks = new ArrayList<Task>();

        for (Task i : tempTasks)
            if (i.isDue())
                tasks.add(i);

        //Package all into tasks category object.
        TasksCategory tc = new TasksCategory(tasks, "Planned", R.color.light_green); //ID-less object
        return tc;
    }

    @SuppressLint("Range")
    public TasksCategory selectAllTasksMarkedImportantForUser(long id) {
        ArrayList<Task> tempTasks = this.selectAllTasksOfUser(id), tasks = new ArrayList<Task>();

        for (Task i : tempTasks)
            if (i.isImportant())
                tasks.add(i);

        //Package all into tasks category object.

        TasksCategory tc = new TasksCategory(tasks, "Important", R.color.light_red); //ID-less object
        return tc;
    }

    @SuppressLint("Range")
    public TasksCategory selectAllTasksForUser(long id) {
        ArrayList<Task> tempTasks = this.selectAllTasksOfUser(id);

        //Package all into tasks category object.
        TasksCategory tc = new TasksCategory(tempTasks, "All tasks", R.color.light_gray); //ID-less object
        return tc;
    }

    //Table/Record deletion methods
    public void removeRecord(String Table_name, String Column_ID, long id) {
        //Removes the record with the corresponding id.
        String deleteQuery = "DELETE FROM " + Table_name + " WHERE " + Column_ID + " = " + id;
        writeStatement(deleteQuery);
    }

    public void removeTask(long id) {
        //todo unlink all lists tasks relations
        removeRecord(TASKS.TABLE_NAME, TASKS.ID, id);
    }

    public void removeFromTblUsersDevices(long user_id, String device_id) {
        //Deletes all matching records.
        String removeQuery = "DELETE FROM " + DEVICES_USERS.TABLE_NAME +
                " WHERE " + DEVICES_USERS.USER_ID + " = " + user_id + " AND " + DEVICES_USERS.DEVICE_ID + " = " + device_id;
        writeStatement(removeQuery);
    }

    public void removeFromTblListsUsers(long user_id, long list_id) {
        //Deletes all matching records.
        String removeQuery = "DELETE FROM " + USERS_LISTS.TABLE_NAME +
                " WHERE " + USERS_LISTS.USER_ID + " = " + user_id + " AND " + USERS_LISTS.LIST_ID + " = " + list_id;
        writeStatement(removeQuery);
    }

    public void removeFromTblTasksLists(long list_id, long task_id) {
        //Deletes all matching records.
        String removeQuery = "DELETE FROM " + LISTS_TASKS.TABLE_NAME +
                " WHERE " + LISTS_TASKS.LIST_ID + " = " + list_id + " AND " + LISTS_TASKS.TASK_ID + " = " + task_id;

        writeStatement(removeQuery);
    }

    public void deleteTable(String TABLE_NAME) {
        //Deletes table.
        String deleteQuery = "DROP TABLE IF EXISTS " + TABLE_NAME;

        writeStatement(deleteQuery);
    }

    public void deleteAllTables() {
        //Deletes table.
        for (String i : databaseValues.Tables) {
            this.deleteTable(i);
        }
    }

    //Convert cursors to object, do not run unless sure of what the cursor contains.
    @SuppressLint("Range")
    public UserModel toUserModel(Cursor c) {
        String name, surname, email, password;
        long id;
        c.moveToFirst();
        name = c.getString(c.getColumnIndex(USERS.NAME));
        id = c.getInt(c.getColumnIndex(USERS.ID));
        surname = c.getString(c.getColumnIndex(USERS.SURNAME));
        email = c.getString(c.getColumnIndex(USERS.EMAIL));
        password = c.getString(c.getColumnIndex(USERS.PASSWORD));

        UserModel um = new UserModel(name, surname, email, password, id);
        return um;
    }

    @SuppressLint("Range")
    public TasksCategory toTasksCategory(Cursor c) {
        String title;
        int colorID, id;
        ArrayList<Task> tasks = new ArrayList<Task>();

        title = c.getString(c.getColumnIndex(LISTS.TITLE));
        id = c.getInt(c.getColumnIndex(LISTS.ID));
        colorID = c.getInt(c.getColumnIndex(LISTS.COLOR));

        Cursor taskIDsCursor = this.selectAllTasksOfListID(id);

        if (taskIDsCursor != null && taskIDsCursor.getCount() > 0) {
            taskIDsCursor.moveToFirst();
            while (!taskIDsCursor.isAfterLast()) {
                //todo important, creates a NEW task object for every new list, there won't be a connection between lists - must fix.
                @SuppressLint("Range") long _id = taskIDsCursor.getLong(taskIDsCursor.getColumnIndex(TASKS.ID));
                Task t = this.selectTaskByID(_id);
                tasks.add(t);
                taskIDsCursor.moveToNext();
            }
        }

        TasksCategory tc = new TasksCategory(tasks, title, colorID, id);
        return tc;
    }

    @SuppressLint("Range")
    public Task toTask(Cursor c) {
        //todo convert date from object to string type in rest of code.
        String name, content;
        boolean isImportant, isInMyDay;
        int d, m, y;
        long id;

        name = c.getString(c.getColumnIndex(TASKS.TITLE));
        content = c.getString(c.getColumnIndex(TASKS.NOTE));
        isImportant = c.getInt(c.getColumnIndex(TASKS.IS_IMPORTANT)) > 0;
        isInMyDay = c.getInt(c.getColumnIndex(TASKS.IS_IN_MY_DAY)) > 0;
        d = c.getInt(c.getColumnIndex(TASKS.DUE_DATE.DAY));
        m = c.getInt(c.getColumnIndex(TASKS.DUE_DATE.MONTH));
        y = c.getInt(c.getColumnIndex(TASKS.DUE_DATE.YEAR));
        id = c.getInt(c.getColumnIndex(TASKS.ID));

        Task t;
        t = new Task(name, content, isImportant, isInMyDay, d, m, y, id);
        return t;
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }

    //update methods, use object to update.
    public void updateList(long id, TasksCategory t) {
        ContentValues cv = new ContentValues();
        cv.put(LISTS.TITLE, t.getTitle());
        cv.put(LISTS.COLOR, t.getColorRID());
        updateStatement(LISTS.TABLE_NAME, cv, LISTS.ID, new String[]{id + ""});

    }

    public void updateUser(long id, UserModel t) {
        ContentValues cv = new ContentValues();
        cv.put(USERS.NAME, t.getName());
        cv.put(USERS.SURNAME, t.getSurname());
        cv.put(USERS.EMAIL, t.getEmail());
        cv.put(USERS.PASSWORD, t.getPassword());
        updateStatement(USERS.TABLE_NAME, cv, USERS.ID, new String[]{id + ""});
    }

    public void updateTask(long id, Task t) {
        int x1 = 0, x2 = 0;
        if (t.isInMyDay())
            x1 = 1;
        if (t.isImportant())
            x2 = 1;

        ContentValues cv = new ContentValues();
        cv.put(TASKS.TITLE, t.getTitle());
        cv.put(TASKS.NOTE, t.getContent());
        cv.put(TASKS.DUE_DATE.DAY, t.getDay());
        cv.put(TASKS.DUE_DATE.MONTH, t.getMonth());
        cv.put(TASKS.DUE_DATE.YEAR, t.getYear());
        cv.put(TASKS.IS_IN_MY_DAY, x1);
        cv.put(TASKS.IS_IMPORTANT, x2);

        updateStatement(TASKS.TABLE_NAME, cv, TASKS.ID, new String[]{id + ""});
    }
}
