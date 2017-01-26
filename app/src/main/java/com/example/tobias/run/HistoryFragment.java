package com.example.tobias.run;


import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Fragment that displays activities tracked, and can sort them by different criteria. Accesed via the DrawerLayout
 * in MainActivity as History.
 */
public class HistoryFragment extends Fragment {

    private View rootView;
    private HistoryListItemAdapter adapter;
    private ArrayList<TrackedRun> trackedRuns;

    public HistoryFragment(){
        //Required empty constructor.
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_history, container, false);
        trackedRuns = new DatabaseHandler(getContext()).getAllTrackedRuns();
        initDateSpinner();
        initListView();
        initFab();

        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        loadRecords();
    }

    /**
     * Populates date spinner, implements spinner callbacks and small runtime UI tweaks on spinner.
     */
    private void initDateSpinner(){
        final Spinner spinner = (Spinner) rootView.findViewById(R.id.date_spinner);
        //Spinner dropdown elements
        String[] categories = new String[]{"All", "Week", "Month", "Year"};
        //Create adapter for Spinner
        final ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<String>(getContext(), R.layout.support_simple_spinner_dropdown_item,
                categories);

        spinner.setAdapter(spinnerAdapter);
        /*Set color of spinner dropdown icon to white. Because of added complexity when designing a custom
        layout file for the Spinner, decided to instead change the color on runtime*/
        spinner.getBackground().setColorFilter(Color.parseColor("#FFFFFF"), PorterDuff.Mode.SRC_ATOP);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                /*Get currentlySelected TextView and set its color to white.Because of added complexity
                when designing a custom layout file for the Spinner, decided to instead change the color on runtime*/
                TextView selectedTextView = (TextView) spinner.getSelectedView();
                if(selectedTextView != null){
                    selectedTextView.setTextColor(Color.parseColor("#FFFFFF"));
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }


    private void initListView(){
        ListView listView = (ListView) rootView.findViewById(R.id.history_listview);
        adapter = new HistoryListItemAdapter(getContext(), trackedRuns);
        listView.setAdapter(adapter);
        listView.setEmptyView(rootView.findViewById(R.id.empty_view));
    }


    /**
     * Sets Floating action Button callback
     */
    private void initFab(){
        FloatingActionButton fab = (FloatingActionButton) rootView.findViewById(R.id.fab_button);
        fab.setTransitionName("reveal");

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(),EditorActivity.class);
                startActivity(intent);
            }
        });
    }

    /**
     * Reloads records into list view. Because of countless bugs trying to add items dinamically,
     * resorted to clearing the adapter and adding all the items again, which is not great for performance,
     * and should eventually be refactored to a more efficient solution.
     */
    private void loadRecords() {
        //TODO: Reformat for performance
        adapter.clear();
        for(TrackedRun tr : new DatabaseHandler(getContext()).getAllTrackedRuns()){
            adapter.add(tr);
        }
        adapter.notifyDataSetChanged();
    }

}
