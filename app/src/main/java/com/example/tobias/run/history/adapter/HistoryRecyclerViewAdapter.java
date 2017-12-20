package com.example.tobias.run.history.adapter;


import android.content.Context;
import android.support.constraint.ConstraintLayout;
import android.support.v7.util.DiffUtil;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;
import android.widget.TextView;

import com.example.tobias.run.R;
import com.example.tobias.run.data.DiffCallback;
import com.example.tobias.run.data.Distance;
import com.example.tobias.run.data.Run;
import com.example.tobias.run.data.SharedPreferenceManager;
import com.example.tobias.run.data.SharedPreferenceRepository;
import com.example.tobias.run.utils.RunUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Adapter for custom RecyclerView in History Fragment.
 */
public class HistoryRecyclerViewAdapter extends SelectableAdapter<HistoryRecyclerViewAdapter.HistoryViewHolder> {

    public interface OnItemClicked {
        void onClick(int position);
        boolean onLongClick(int position);
    }

    private ArrayList<Run> runs = new ArrayList<>();
    private Context context;
    private OnItemClicked clickListener;
    private SharedPreferenceRepository preferenceManager;

    public HistoryRecyclerViewAdapter(Context context, OnItemClicked clickListener){
        this.context = context;
        this.clickListener = clickListener;
        preferenceManager = new SharedPreferenceManager(context);
    }

    @Override
    public HistoryViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.history_list_item, parent, false);
        return new HistoryViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final HistoryViewHolder holder, int position) {
        final Run run = runs.get(holder.getAdapterPosition());
        holder.ratingBar.setRating(run.getRating());
        holder.date.setText(RunUtils.dateToString(run.getDate()));
        holder.time.setText(RunUtils.timeToString(run.getTime()));
        holder.distance.setText(RunUtils.getDistanceText(run, getDistanceUnit()));
        holder.pace.setText(RunUtils.getPaceText(run, getDistanceUnit()));

        if (isSelected(holder.getAdapterPosition())){
            holder.layout.setBackgroundColor(context.getResources().getColor(R.color.selectedColor));
        } else {
            holder.layout.setBackgroundColor(context.getResources().getColor(android.R.color.white));
        }
    }

    /**
     * Called from view to update data set. Uses diffUtil to calculate view updates
     * @param newList
     */
    public void updateItems(List<Run> newList){
        final DiffCallback diff = new DiffCallback(newList, this.runs);
        final DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(diff);

        this.runs.clear();
        this.runs.addAll(newList);
        diffResult.dispatchUpdatesTo(this);
    }

    private Distance.Unit getDistanceUnit(){
        return preferenceManager.get(SharedPreferenceRepository.DISTANCE_UNIT_KEY);
    }

    @Override
    public int getItemCount() {
        return this.runs.size();
    }

    public class HistoryViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {

        public RatingBar ratingBar;
        public TextView distance;
        public TextView time;
        public TextView date;
        public TextView pace;
        public ConstraintLayout layout;

        public HistoryViewHolder(View view){
            super(view);
            ratingBar = (RatingBar) view.findViewById(R.id.history_list_item_rating_bar);
            distance = (TextView) view.findViewById(R.id.history_list_item_distance_text);
            time = (TextView) view.findViewById(R.id.history_list_item_time_text);
            date = (TextView) view.findViewById(R.id.history_list_item_date_text);
            pace = (TextView) view.findViewById(R.id.history_list_item_pace_text);
            layout = view.findViewById(R.id.history_list_item_layout);

            view.setOnClickListener(this);
            view.setOnLongClickListener(this);
        }

        @Override
        public void onClick(View view) {
            clickListener.onClick(getAdapterPosition());
        }

        @Override
        public boolean onLongClick(View view) {
            return clickListener.onLongClick(getAdapterPosition());
        }


    }


}

