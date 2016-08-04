package squarerock.track;

import android.app.Application;
import android.database.sqlite.SQLiteDatabase;

import squarerock.track.db.TodoDbHelper;

/**
 * Created by pranavkonduru on 7/28/16.
 */

public class Track extends Application {

    protected SQLiteDatabase db;

    @Override
    public void onCreate() {
        super.onCreate();

        TodoDbHelper dbHelper = new TodoDbHelper(this);
        db = dbHelper.getWritableDatabase();
    }

    public SQLiteDatabase getDb() {
        return db;
    }
}
