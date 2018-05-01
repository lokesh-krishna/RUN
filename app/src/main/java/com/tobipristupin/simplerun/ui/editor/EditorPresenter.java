package com.tobipristupin.simplerun.ui.editor;

import com.tobipristupin.simplerun.data.interfaces.PreferencesRepository;
import com.tobipristupin.simplerun.data.interfaces.Repository;
import com.tobipristupin.simplerun.data.model.DistanceUnit;
import com.tobipristupin.simplerun.data.model.Run;
import com.tobipristupin.simplerun.utils.DateUtils;
import com.tobipristupin.simplerun.utils.RunUtils;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.text.DecimalFormat;
import java.util.Locale;

/**
 * Presenter used in EditorActivityView.
 */

public class EditorPresenter {

    private EditorView view;
    private Run runToEdit;
    private boolean editMode;
    private Repository<Run> repository;
    private PreferencesRepository preferencesRepository;

    public EditorPresenter(EditorView view, Repository<Run> repository, PreferencesRepository preferencesRepository) {
        this.view = view;
        this.repository = repository;
        this.preferencesRepository = preferencesRepository;
    }

    /**
     * If Run was passed, that means intent for activity was sent with a Run
     * that should be edited. If runToEdit is null, a new Run should be created
     * instead of modifying an existing one.
     * @param runToEdit
     */
    public void onCreateView(Run runToEdit){
        if (runToEdit != null) {
            this.runToEdit = runToEdit;
            editMode = true;
            setViewEditMode();
        }
    }

    private void setViewEditMode(){
        DistanceUnit unit = preferencesRepository.getDistanceUnit();

        String distanceText = RunUtils.distanceToString(runToEdit.getDistance(unit), unit);
        view.setDistanceText(distanceText);

        String dateText = RunUtils.dateToString(runToEdit.getDate());
        view.setDateText(dateText);

        String timeText = RunUtils.timeToString(runToEdit.getTime(), true);
        view.setTimeText(timeText);

        view.setRatingText(String.valueOf(runToEdit.getRating()));

        view.setActionBarEditTitle();
    }

    public void onSaveButtonPressed(){
        String distanceText = view.getDistanceText();
        String timeText = view.getTimeText();
        String ratingText = view.getRatingText();
        String dateText = view.getDateText();

        if (!isValid(distanceText, timeText, ratingText, dateText)) {
            view.showInvalidFieldsToast();
            view.vibrate();
            return;
        }

        Run run;

        if (editMode){
            run = putDataIntoExistingRun(distanceText, ratingText, timeText, dateText);
        } else {
            run = createRunFromData(distanceText, ratingText, timeText, dateText);
        }

        addRunToDatabase(run);
        view.showAddedRunSuccessfullyToast();
        view.finishView();
    }

    public void onDistanceDialogPositiveButton(int wholeNum, int fractionalNum){
        String distance = "" + wholeNum + "." + fractionalNum + " " +
                preferencesRepository.getDistanceUnit().toString();

        view.setDistanceText(distance);
    }

    public void onTimeDialogPositiveButton(int hours, int minutes, int seconds){
        DecimalFormat df = new DecimalFormat("00");
        String time = df.format(hours) + ":"
                + df.format(minutes) + ":"
                + df.format(seconds);
        view.setTimeText(time);
    }

    public void onRatingDialogPositiveButton(int rating){
        view.setRatingText(String.valueOf(rating));
    }

    public void onDateDialogPositiveButton(int year, int month, int dayOfMonth){
        DateTimeFormatter formatter;

        if (Locale.getDefault().equals(Locale.US)) {
            formatter = DateTimeFormat.forPattern("E, M/d/y");
        } else {
            formatter = DateTimeFormat.forPattern("E, d/M/y");
        }

        DateTime dateText = new DateTime(year, month + 1, dayOfMonth, 0, 0);

        view.setDateText(DateUtils.formatDateString(formatter.print(dateText)));
    }

    private boolean isValid(String... values){
        for(String value : values){
            if (value == null || value.equals(view.getEmptyFieldText())){
                return false;
            }
        }
        return true;
    }

    private Run createRunFromData(String distanceText, String ratingText, String timeText, String dateText){
        Run run;
        if (distanceText.contains(DistanceUnit.KM.toString())){
            run = Run.fromKilometers(distanceText, timeText, dateText, ratingText);
        } else {
            run = Run.fromMiles(distanceText, timeText, dateText, ratingText);
        }

        return run;
    }

    private Run putDataIntoExistingRun(String distanceText, String ratingText, String timeText, String dateText){
        runToEdit.setDistance(distanceText);
        runToEdit.setRating(ratingText);
        runToEdit.setTime(timeText);
        runToEdit.setDate(dateText);
        return runToEdit;
    }


    private void addRunToDatabase(Run run){
        if (editMode){
            repository.update(run);
        } else {
            repository.add(run);
        }
    }

}
