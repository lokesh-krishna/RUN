package com.example.tobias.run.editorscreen;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.DatePicker;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.tobias.run.R;
import com.example.tobias.run.data.DatabaseHandler;
import com.example.tobias.run.helpers.DateManager;
import com.example.tobias.run.data.TrackedRun;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.util.Calendar;
import java.util.HashMap;

import es.dmoral.toasty.Toasty;

/**
 * Activity that allows user to complete distance, time, rating and date fields when adding /
 * editing a run. This class shows different dialogs to input the data, and implements the onClickListener
 * for the positive button to retrieve the data
 */
public class EditorActivity extends AppCompatActivity {

    Activity activity;
    private final int DATE_DIALOG_ID = 999;
    private SharedPreferences sharedPref;
    private static final String TAG = "EditorActivity";
    private TrackedRun trackedRun;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor);
        this.activity = this;
        this.sharedPref = getSharedPreferences(getString(R.string.preference_key), Context.MODE_PRIVATE);

        Intent intent = getIntent();
        trackedRun = (TrackedRun) intent.getParcelableExtra("TrackedRun");

        initToolbar();

        //If tracked run has been passed via intent, init edit mode.
        if (trackedRun != null){
            setEditMode();
        } else {
            trackedRun = new TrackedRun();
        }

        initDistanceField();
        initTimeField();
        initDateField();
        initRatingField();

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                //If home button pressed close activity with no result.
                finish();
                break;
            case R.id.editor_save:
                if (addRecord()) {
                    Toasty.success(activity, "Successfully Added", Toast.LENGTH_SHORT).show();
                    Log.v(TAG, "Added record to database successfully.");
                    finish();
                } else {
                    Toasty.warning(activity, "Fill in all the fields", Toast.LENGTH_SHORT).show();
                    MediaPlayer mediaPlayer = MediaPlayer.create(this, R.raw.error);
                    mediaPlayer.start();
                }
        }
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.editor_toolbar_menu, menu);
        return true;
    }

    /**
     * Gets data inserted into views and adds it into database. If one of the fields hasn't been
     * set, it returns false and exits without adding it.
     */
    private boolean addRecord() {
        DatabaseHandler databaseHandler = new DatabaseHandler(this);

        HashMap<String, String> values = retrieveDataFromViews();
        if (!isDataComplete(values)){
            return false;
        }

        trackedRun.setDistance(values.get("distance"));
        trackedRun.setDate(values.get("date"));
        trackedRun.setRating(values.get("rating"));
        trackedRun.setUnit(values.get("unit"));
        trackedRun.setTime(values.get("time"));

        //If run hasn't been assigned an ID by entering into database, add it o database as new record
        if (trackedRun.getId() == null){
            databaseHandler.addRun(trackedRun);
        } else {
            databaseHandler.updateRun(trackedRun);
        }

        return true;
    }

    //Retrieves text from distance, time, ... TextViews
    private HashMap<String, String> retrieveDataFromViews(){
        HashMap<String, String> data = new HashMap<>();
        data.put("distance", ((TextView) findViewById(R.id.editor_distance_text)).getText().toString());
        data.put("time", ((TextView) findViewById(R.id.editor_time_text)).getText().toString());
        data.put("rating", ((TextView) findViewById(R.id.editor_rating_text)).getText().toString());
        data.put("date", ((TextView) findViewById(R.id.editor_date_text)).getText().toString());
        data.put("unit", sharedPref.getString(getString(R.string.preference_distance_unit_key), null));
        return data;
    }

    /**
     * Checks if data retrieved is complete or is missing fields
     * @param data
     * @return
     */
    private boolean isDataComplete(HashMap<String, String> data){
        for(String value : data.values()){
            if (value.equals("None")){
                return false;
            }
        }
        return true;
    }

    public void initToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.editor_toolbar);
        setSupportActionBar(toolbar);
        changeStatusBarColor();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_close_white_24dp);
    }

    private void setEditMode(){
        getSupportActionBar().setTitle("Edit Run");

        String unit = sharedPref.getString(getString(R.string.preference_distance_unit_key), null);
        String distanceText = DateManager.distanceToString(trackedRun.getDistance(), unit);
        ((TextView) findViewById(R.id.editor_distance_text)).setText(distanceText);

        String dateText = DateManager.dateToString(trackedRun.getDate());
        ((TextView) findViewById(R.id.editor_date_text)).setText(dateText);

        String timeText = DateManager.timeToString(trackedRun.getTime());
        ((TextView) findViewById(R.id.editor_time_text)).setText(timeText);

        ((TextView) findViewById(R.id.editor_rating_text)).setText(String.valueOf(trackedRun.getRating()));
    }

    /**
     * AppTheme status bar color attr is set to transparent for the drawerLayout in main activity.
     * this activity uses the primary dark color as status bar color. This method sets it during runtime.
     */
    private void changeStatusBarColor() {
        Window window = getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(getResources().getColor(R.color.colorPrimaryDark));
    }


    private void initDateField() {
        RelativeLayout field = (RelativeLayout) findViewById(R.id.editor_date_view);
        field.setOnClickListener(new View.OnClickListener() {
            @Override
            @SuppressWarnings("deprecation")
            public void onClick(View v) {
                showDialog(DATE_DIALOG_ID);
            }
        });
    }

    @Override
    @SuppressWarnings("deprecation")
    /**
     * When showDialog is called, this method is called, which checks the id of the new dialog,
     * and if it corresponds with DATE_DIALOG_ID, it opens a new Date Picker dialog.
     */
    protected Dialog onCreateDialog(int id) {
        if (id == DATE_DIALOG_ID) {
            //Get current year, month and day to pass it to date picker dialog.
            Calendar calendar = Calendar.getInstance();
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH);
            int day = calendar.get(Calendar.DAY_OF_MONTH);

            return new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
                @Override
                public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                    DateTimeFormatter formatter = DateTimeFormat.forPattern("E, d/M/y");
                    DateTime dateText = new DateTime(year, month + 1, dayOfMonth, 0, 0);

                    TextView dateTextView = (TextView) findViewById(R.id.editor_date_text);
                    dateTextView.setText(formatter.print(dateText));
                }
            }, year, month, day);
        }
        return null;
    }

    private void initDistanceField() {
        RelativeLayout field = (RelativeLayout) findViewById(R.id.editor_distance_view);
        field.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DistanceDialog dialog = new DistanceDialog(activity, new DistanceDialog.onPositiveButtonListener() {
                    @Override
                    public void onClick(String distanceValue) {
                        TextView distanceTextView = (TextView) findViewById(R.id.editor_distance_text);
                        distanceTextView.setText(distanceValue);
                    }
                });
                dialog.makeDialog(activity).show();
            }
        });
        }

    private void initTimeField() {
        RelativeLayout field = (RelativeLayout) findViewById(R.id.editor_time_view);
        field.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TimeDialog timeDialog = new TimeDialog(activity, new TimeDialog.onPositiveButtonListener() {
                    @Override
                    public void onClick(String timeValue) {
                        TextView timeTextView = (TextView) findViewById(R.id.editor_time_text);
                        timeTextView.setText(timeValue);
                    }
                });
                timeDialog.makeDialog().show();
            }

        });
    }

    private void initRatingField() {
        RelativeLayout field = (RelativeLayout) findViewById(R.id.editor_rating_view);
        field.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RatingDialog ratingDialog = new RatingDialog(activity, new RatingDialog.onPositiveButtonListener() {
                    @Override
                    public void onClick(String ratingValue) {
                        TextView ratingTextView = (TextView) findViewById(R.id.editor_rating_text);
                        ratingTextView.setText(ratingValue);
                    }
                });
                ratingDialog.makeDialog().show();
            }
        });
    }


}
