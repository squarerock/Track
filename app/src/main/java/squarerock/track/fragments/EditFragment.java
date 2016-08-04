package squarerock.track.fragments;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TimePicker;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import info.hoang8f.android.segmented.SegmentedGroup;
import squarerock.track.Constants;
import squarerock.track.R;
import squarerock.track.Track;
import squarerock.track.db.TodoDbHelper;
import squarerock.track.model.Todo;

/**
 * Created by pranavkonduru on 7/29/16.
 */

public class EditFragment extends android.support.v4.app.DialogFragment implements Toolbar.OnMenuItemClickListener, View.OnClickListener {

    private EditText todoTitle, todoDescription;
    private EditText dateTextView, timeTextView;

    private Switch statusSwitch;
    private SegmentedGroup segmentedGroup;
    private Toolbar toolbar;

    private DatePickerDialog datePickerDialog;
    private TimePickerDialog timePickerDialog;

    private SimpleDateFormat dateFormatter;

    private EditFragmentCallback callback;
    private Todo todo;
    private SQLiteDatabase db;

    private static final String TAG = "EditFragment";
    public EditFragment(){}

    /**
     * Talking activity needs to implement this to update views on dismissal
     */
    public interface EditFragmentCallback{
        void updateViewOnDialogDismiss();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        todo = (Todo) getArguments().getSerializable("todo");
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if(context instanceof EditFragmentCallback){
            callback = (EditFragmentCallback) context;
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        Dialog dialog = getDialog();
        if (dialog != null) {
            dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.WHITE));
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView: ");
        getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);
        View view = inflater.inflate(R.layout.cardview_editfragment, container, false);

        initViews(view);
        initDb();
        customizeToolbar();
        populateView();

        return view;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.editText:
                // Date
                Log.d(TAG, "onClick: Date clicked");
                datePickerDialog.show();
                break;
            case R.id.editText3:
                // Time
                Log.d(TAG, "onClick: Time clicked");
                timePickerDialog.show();
                break;
            default:
                Log.d(TAG, "onClick: "+view);
        }
    }

    /**
     * Creates a date picker dialog
     */
    private void initDatePickerDialog() {
        Calendar cal = Calendar.getInstance();
        datePickerDialog = new DatePickerDialog(this.getActivity(), new DatePickerDialog.OnDateSetListener() {
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                Calendar newDate = Calendar.getInstance();
                newDate.set(year, monthOfYear, dayOfMonth);
                dateTextView.setText(dateFormatter.format(newDate.getTime()));
            }
        },cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH));
    }

    /**
     * Creates a time picker dialog
     */
    private void initTimePickerDialog(){
        Calendar cal = Calendar.getInstance();
        timePickerDialog = new TimePickerDialog(this.getActivity(), new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int hour, int minute) {
                String meridian;
                if(hour > 12){
                    hour = hour % 12;
                    meridian = "PM";
                } else if(hour == 12){
                    meridian = "PM";
                } else {
                    meridian = "AM";
                }
                timeTextView.setText(hour+":"+minute+" "+meridian);
            }
        }, cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE), false /*shows 12hr view*/);
    }

    /**
     * Initializes Database
     */
    private void initDb() {
        db = ((Track)getActivity().getApplication()).getDb();
    }

    /**
     * If a todo is passed, sets up the fields with the values of that todo
     */
    private void populateView() {
        String title, description, date, time, status;
        int priority;

        title = todo.name;
        description = todo.notes;
        priority = todo.priority;
        status = todo.status;
        date = todo.date;
        time = todo.time;

        if(title != null ) todoTitle.setText(title);
        if(description != null) todoDescription.setText(description);
        if(priority != -1) segmentedGroup.check(getPriorityId(priority));
        if(Constants.CHECKED.equals(status)) statusSwitch.setChecked(true);
        if(date != null) dateTextView.setText(date);
        if(time != null) timeTextView.setText(time);
    }

    /**
     * customize the toolbar
     */
    private void customizeToolbar() {
        toolbar.inflateMenu(R.menu.edit_fragment);
        toolbar.setOnMenuItemClickListener(this);
        toolbar.setTitle("Todo");
    }

    /**
     * Initializes the views on the fragment
     * @param view fragment view
     */
    private void initViews(View view) {
        toolbar = (Toolbar) view.findViewById(R.id.toolbar3);
        todoTitle = (EditText) view.findViewById(R.id.tiet1_ed);
        todoDescription = (EditText) view.findViewById(R.id.tiet2_ed);
        statusSwitch = (Switch) view.findViewById(R.id.switch1);
        segmentedGroup = (SegmentedGroup) view.findViewById(R.id.segmentedGroup2);

        dateTextView = (EditText) view.findViewById(R.id.editText);
        timeTextView = (EditText) view.findViewById(R.id.editText3);

        // Simple date formatter
        dateFormatter = new SimpleDateFormat("dd-MM-yyyy", Locale.US);

        // Disable focus for date and time views
        dateTextView.setFocusable(false);
        timeTextView.setFocusable(false);
        // Disable cursor on date and time views
        dateTextView.setCursorVisible(false);
        timeTextView.setCursorVisible(false);
        // set click listeners for date and time views
        dateTextView.setOnClickListener(this);
        timeTextView.setOnClickListener(this);

        initDatePickerDialog();
        initTimePickerDialog();
    }

    /**
     * Handling dismiss of the dialog
     * @param dialog dialog that was dismissed
     */
    @Override
    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);
        Log.d(TAG, "onDismiss");
        dialog.dismiss();

        // Once the dialog is dismissed, call the method in activity to refresh views.
        if(callback != null){
            Log.d(TAG, "onDismiss: calling back");
            callback.updateViewOnDialogDismiss();
        }
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        Log.d(TAG, "onMenuItemClick: item is: "+item);
        switch (item.getItemId()){
            case R.id.menu_item_save:
                Log.d(TAG, "onMenuItemClick: save clicked");
                updateFields();
                return true;

            case R.id.menu_item_delete:
                Log.d(TAG, "onMenuItemClick: delete clicked");
                deleteItem();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /**
     * Handle delete click
     */
    private void deleteItem() {
        Log.d(TAG, "deleteItem: ");
        TodoDbHelper.delete(db, todo);

        // Put up a toast
        Toast.makeText(getActivity(), R.string.deleted, Toast.LENGTH_SHORT).show();
        dismiss();
    }

    /**
     * Handle save click
     */
    private void updateFields() {
        Log.d(TAG, "updateFields: ");

        // Close any soft keyboards
        if(getActivity().getCurrentFocus() != null) {
            Log.d(TAG, "updateFields: Closing soft keyboard");
            InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(), 0);
        }

        todo.name = todoTitle.getText().toString();
        if(todo.name.isEmpty()){
            todoTitle.setError("Cannot have empty titles");
            return;
        }
        todo.notes = todoDescription.getText().toString();
        todo.priority = getPriorityValue(segmentedGroup.getCheckedRadioButtonId());
        todo.status = statusSwitch.isChecked()? Constants.CHECKED : Constants.NOT_CHECKED;
        todo.date = dateTextView.getText().toString();
        todo.time = timeTextView.getText().toString();

        TodoDbHelper.put(db, todo);
        // Put up a toast
        Toast.makeText(getActivity(), R.string.saved, Toast.LENGTH_SHORT).show();
        dismiss();
    }

    /**
     * Helper method to get priority
     * @param id priority button id
     * @return value for that button ID
     */
    private int getPriorityValue(int id) {
        switch (id){
            case R.id.button_low:
                return 0;
            case R.id.button_medium:
                return 1;
            case R.id.button_high:
                return 2;
            default:
                return -1;
        }
    }

    /**
     * Helper method to get priority button id
     * @param value priority value
     * @return button ID for that value
     */
    private int getPriorityId(int value){
        switch (value){
            case 0:
                return R.id.button_low;
            case 1:
                return R.id.button_medium;
            case 2:
                return R.id.button_high;
            default:
                return 0;
        }
    }

}
