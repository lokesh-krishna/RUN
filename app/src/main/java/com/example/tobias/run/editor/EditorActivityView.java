package com.example.tobias.run.editor;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.tobias.run.R;
import com.example.tobias.run.data.FirebaseDatabaseManager;
import com.example.tobias.run.data.Run;
import com.example.tobias.run.data.SharedPreferenceManager;
import com.example.tobias.run.data.SharedPreferenceRepository;
import com.example.tobias.run.editor.dialog.DistanceDialog;
import com.example.tobias.run.editor.dialog.RatingDialog;
import com.example.tobias.run.editor.dialog.TimeDialog;

import java.util.Calendar;

import es.dmoral.toasty.Toasty;

/**
 * Activity that allows user to complete distance, time, rating and date fields when adding /
 * editing a run. This class shows different dialogs to input the data, and implements the onClickListener
 * for the positive button to retrieve the data
 */
public class EditorActivityView extends AppCompatActivity implements EditorView {

    private final int DATE_DIALOG_ID = 999;
    private SharedPreferenceRepository preferenceManager;
    private EditorPresenter presenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor);

        initToolbar();

        SharedPreferenceRepository preferenceRepository = new SharedPreferenceManager(EditorActivityView.this);
        presenter = new EditorPresenter(this, preferenceRepository, FirebaseDatabaseManager.getInstance());
        preferenceManager = new SharedPreferenceManager(EditorActivityView.this);

        Intent intent = getIntent();
        Run run = intent.getParcelableExtra(getString(R.string.run_intent_key));

        presenter.onCreateView(run);

        initDistanceField();
        initTimeField();
        initDateField();
        initRatingField();
        animateViewsEntrance();
    }

    @Override
    public void showAddedRunSuccessfullyToast() {
        Toasty.success(EditorActivityView.this, "Successfully Added", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void showInvalidFieldsToast() {
        Toasty.warning(EditorActivityView.this, "Fill in all the fields", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void finishView() {
        supportFinishAfterTransition();
    }

    @Override
    public void vibrate() {
        Vibrator vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
        vibrator.vibrate(100);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.editor_toolbar_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                //If exit  button pressed close activity with no result.
                supportFinishAfterTransition();
                break;
            case R.id.editor_save:
                presenter.onSaveButtonPressed();
        }

        return true;

    }    @Override
    public void setDistanceText(String text) {
        ((TextView) findViewById(R.id.editor_distance_text)).setText(text);
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
                    presenter.onDateDialogPositiveButton(year, month, dayOfMonth);
                }
            }, year, month, day);
        }
        return null;
    }    @Override
    public void setDateText(String text) {
        ((TextView) findViewById(R.id.editor_date_text)).setText(text);
    }

    public void initToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.editor_toolbar);
        setSupportActionBar(toolbar);
        changeStatusBarColor();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_close_white_24dp);
    }    @Override
    public void setTimeText(String text) {
        ((TextView) findViewById(R.id.editor_time_text)).setText(text);
    }

    /**
     * AppTheme status bar color attr is set to transparent for the drawerLayout in main activity.
     * this activity uses the primary dark color as status bar color. This method sets it during runtime.
     */
    private void changeStatusBarColor() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
            Window window = getWindow();
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(getResources().getColor(R.color.colorPrimaryDark));
        }
    }    @Override
    public void setRatingText(String text) {
        ((TextView) findViewById(R.id.editor_rating_text)).setText(text);
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
    }    @Override
    public void setSupportActionBarTitle(String text) {
        getSupportActionBar().setTitle(text);
    }

    private void initDistanceField() {
        RelativeLayout field = (RelativeLayout) findViewById(R.id.editor_distance_view);
        field.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DistanceDialog dialog = new DistanceDialog(EditorActivityView.this, new DistanceDialog.onPositiveButtonListener() {
                    @Override
                    public void onClick(String distanceValue) {
                        presenter.onDistanceDialogPositiveButton(distanceValue);
                    }
                });
                dialog.makeDialog(EditorActivityView.this).show();
            }
        });
        }    @Override
    public String getDistanceText() {
        return ((TextView) findViewById(R.id.editor_distance_text)).getText().toString();
    }

    private void initTimeField() {
        RelativeLayout field = (RelativeLayout) findViewById(R.id.editor_time_view);
        field.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TimeDialog timeDialog = new TimeDialog(EditorActivityView.this, new TimeDialog.onPositiveButtonListener() {
                    @Override
                    public void onClick(String timeValue) {
                        presenter.onTimeDialogPositiveButton(timeValue);
                    }
                });
                timeDialog.makeDialog().show();
            }

        });
    }    @Override
    public String getDateText() {
        return ((TextView) findViewById(R.id.editor_date_text)).getText().toString();
    }

    private void initRatingField() {
        RelativeLayout field = (RelativeLayout) findViewById(R.id.editor_rating_view);
        field.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RatingDialog ratingDialog = new RatingDialog(EditorActivityView.this, new RatingDialog.onPositiveButtonListener() {
                    @Override
                    public void onClick(String ratingValue) {
                        presenter.onRatingDialogPositiveButton(ratingValue);
                    }
                });
                ratingDialog.makeDialog().show();
            }
        });
    }    @Override
    public String getTimeText() {
        return ((TextView) findViewById(R.id.editor_time_text)).getText().toString();
    }

    private void animateViewsEntrance() {
        LinearLayout linearLayout = (LinearLayout) findViewById(R.id.content_main);
        for (int i = 0; i < linearLayout.getChildCount(); i++) {
            View child = linearLayout.getChildAt(i);
            child.animate()
                    .setDuration(750)
                    .alpha(1.0f);

        }
    }    @Override
    public String getRatingText() {
        return ((TextView) findViewById(R.id.editor_rating_text)).getText().toString();
    }




















}