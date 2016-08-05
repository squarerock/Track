package squarerock.track.activities;

import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import java.util.ArrayList;

import squarerock.track.R;
import squarerock.track.Track;
import squarerock.track.adapter.DividerItemDecoration;
import squarerock.track.adapter.ItemClickListener;
import squarerock.track.adapter.TodoAdapter;
import squarerock.track.db.TodoDbHelper;
import squarerock.track.fragments.EditFragment;
import squarerock.track.model.Todo;

/**
 * Created by pranavkonduru on 7/28/16.
 */
public class TodoActivity extends AppCompatActivity implements
        View.OnClickListener, EditFragment.EditFragmentCallback {

    private SQLiteDatabase db;
    private RecyclerView recyclerView;
    private TodoAdapter adapter;
    private static final String TAG = "TodoActivity";
    private ArrayList<Todo> listOfTodos = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate: ");
        setContentView(R.layout.activity_todo);
        initViews();
        initDB();
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "onResume: ");
        updateListOfTodos();
    }

    /**
     * Initialize all the views on the layout
     */
    private void initViews() {
        Log.d(TAG, "initViews: ");
        initRecyclerView();
        initFab();
        initToolbar();
    }

    /**
     * Initialize Recycler view. Sets up layout manager, adapter and touch listeners
     */
    private void initRecyclerView() {
        Log.d(TAG, "initRecyclerView: ");
        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);

        // Layout Manager
        LinearLayoutManager llm = new LinearLayoutManager(this);
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(llm);

        // Adapter
        adapter = new TodoAdapter(listOfTodos);
        recyclerView.setAdapter(adapter);
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        // Decoration
        recyclerView.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));

        //Touch listeners
        initTouchListeners();
    }

    /**
     * Initialize Floating Action Button. Sets up click listener
     */
    private void initFab() {
        Log.d(TAG, "initFab: ");
        FloatingActionButton addTodo = (FloatingActionButton) findViewById(R.id.floatingActionButton);
        addTodo.setOnClickListener(this);
        addTodo.setBackgroundColor(getResources().getColor(R.color.colorPrimaryDark));
    }

    /**
     * Initialize toolbar. Sets up title and colors status bar
     */
    private void initToolbar() {
        Log.d(TAG, "initToolbar: ");
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_activity);
        toolbar.setTitle("Track");
        toolbar.setTitleTextColor(getResources().getColor(R.color.white));
        toolbar.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
        setSupportActionBar(toolbar);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(getResources().getColor(R.color.colorPrimaryDark));
        }
    }

    /**
     * Initializes Database
     */
    private void initDB() {
        Log.d(TAG, "initDB: ");
        db = ((Track)getApplication()).getDb();
    }

    /**
     * Updates the recycler view adapter with new data from database
     */
    private void updateListOfTodos() {
        Log.d(TAG, "updateListOfTodos: ");
        adapter.updateData(TodoDbHelper.getAllTodos(db));
        adapter.notifyDataSetChanged();
    }

    /**
     * Handles single click and long click of items in recycler view
     */
    private void initTouchListeners() {
        Log.d(TAG, "initTouchListeners: ");
        recyclerView.addOnItemTouchListener(new ItemClickListener(this, recyclerView, new ItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Log.d(TAG, "onItemClick: position is: "+position);
                if(position != RecyclerView.NO_POSITION) {
                    Todo clickedTodo = listOfTodos.get(position);
                    Log.d(TAG, "onItemClick: clicked item is: " + clickedTodo.name);
                    openEditTodoDialog(clickedTodo);
                }
            }

            @Override
            public void onItemLongClick(View view, int position) {
                Log.d(TAG, "onItemLongClick: position is: "+position);
                if(position != RecyclerView.NO_POSITION) {
                    TodoDbHelper.delete(db, listOfTodos.get(position));
                    adapter.removeItem(position);
                    adapter.notifyDataSetChanged();
                }
            }
        }));
    }

    /**
     * click listener for floating action button. Opens a fragment to edit/enter a Todo
     * @param view view that is clicked
     */
    @Override
    public void onClick(View view) {
        Log.d(TAG, "onClick: view is "+view);
        switch (view.getId()){
            case R.id.floatingActionButton:
                Log.d(TAG, "onClick: floatingActionButton");
                openEditTodoDialog(new Todo());
        }
    }

    /**
     * Opens fragment to edit/create a Todo
     * @param todo passing a new todo creates one and passing an existing one edits it
     */
    private void openEditTodoDialog(Todo todo){
        Log.d(TAG, "openEditTodoDialog: ");

        Bundle todoData = new Bundle();
        todoData.putSerializable("todo", todo);

        FragmentManager fm = getSupportFragmentManager();
        EditFragment editFragment = new EditFragment();
        editFragment.setArguments(todoData);
        editFragment.show(fm, "edit_fragment");
    }

    /**
     * Callback from dialog fragment to update the view
     */
    @Override
    public void updateViewOnDialogDismiss() {
        Log.d(TAG, "updateViewOnDialogDismiss: ");
        adapter.updateData(TodoDbHelper.getAllTodos(db));
        adapter.notifyDataSetChanged();
    }
}
