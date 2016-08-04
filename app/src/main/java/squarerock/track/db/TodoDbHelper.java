package squarerock.track.db;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import nl.qbusict.cupboard.QueryResultIterable;
import squarerock.track.model.Todo;

import static nl.qbusict.cupboard.CupboardFactory.cupboard;

/**
 * Created by pranavkonduru on 7/28/16.
 */

public class TodoDbHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "todo.db";
    private static final int DATABASE_VERSION = 1;
    private static final String TAG = "TodoDbHelper";

    public TodoDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    static {
        cupboard().register(Todo.class);
    }
    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        cupboard().withDatabase(sqLiteDatabase).createTables();
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        cupboard().withDatabase(sqLiteDatabase).upgradeTables();
    }

    public static ArrayList<Todo> getAllTodos(SQLiteDatabase sqLiteDatabase){
        ArrayList<Todo> listOfTodos = new ArrayList<>();
        Cursor todoCursor = cupboard().withDatabase(sqLiteDatabase).query(Todo.class).getCursor();
        try{
            QueryResultIterable<Todo> todos = cupboard().withCursor(todoCursor).iterate(Todo.class);
            for(Todo todo : todos){
                listOfTodos.add(todo);
            }
            todos.close();
        } finally {
            todoCursor.close();
        }

        Log.d(TAG, "getAllTodos: todos now: "+listOfTodos.size());
        // Put done Todos at the end of the stack
        Collections.sort(listOfTodos, new Comparator<Todo>() {
            @Override
            public int compare(Todo todo, Todo t1) {
                return todo.status.compareTo(t1.status);
            }
        });
        Log.d(TAG, "getAllTodos: todos after: "+listOfTodos.size());
        return listOfTodos;
    }

    public static Todo getTodoForName(SQLiteDatabase db, String name){
        Todo todoToBeReturned = cupboard().withDatabase(db).query(Todo.class).withSelection( "name = ?", name).get();
        Log.d(TAG, "getTodoForName: todoToBeReturned "+((todoToBeReturned == null)? "not found" : "found"));
        return todoToBeReturned;
    }

    public static boolean delete(SQLiteDatabase db, Todo todo){
        return cupboard().withDatabase(db).delete(todo);
    }

    public static long put(SQLiteDatabase db, Todo todo){
        return cupboard().withDatabase(db).put(todo);
    }
}
