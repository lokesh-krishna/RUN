package com.tobipristupin.simplerun.ui.stats.personalrecords;

import com.github.mikephil.charting.data.Entry;
import com.tobipristupin.simplerun.data.RunPredicates;
import com.tobipristupin.simplerun.data.repository.PreferencesRepository;
import com.tobipristupin.simplerun.data.model.DistanceUnit;
import com.tobipristupin.simplerun.data.model.Run;
import com.tobipristupin.simplerun.data.repository.Repository;
import com.tobipristupin.simplerun.utils.RunUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import io.reactivex.disposables.Disposable;
import io.reactivex.observers.DisposableObserver;

public class StatsPrsPresenter {

    private StatsPrsView view;
    private Repository<Run> runRepository;
    private Disposable repositorySubscription;
    private List<Run> runList;
    private PreferencesRepository preferencesRepository;

    public StatsPrsPresenter(StatsPrsView view, Repository<Run> runRepository, PreferencesRepository repository) {
        this.view = view;
        this.runRepository = runRepository;
        this.preferencesRepository = repository;

        subscribeToData();
    }

    private void subscribeToData(){
        repositorySubscription = runRepository.fetch().subscribeWith(new DisposableObserver<List<Run>>(){

            @Override
            public void onNext(List<Run> runs) {
                onNewData(runs);
            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onComplete() {

            }
        });
    }

    private void onNewData(List<Run> data){
        runList = data;
        updateChartData();
    }

    public void onDetachView(){
        if (repositorySubscription != null){
            repositorySubscription.dispose();
        }
    }

    private void updateChartData(){
        update400mChartData();
        updateMileChartData();
        update5kChartData();
        update10kChartData();
        update21kChartData();
        update42kChartData();
        updatePersonalBests();
    }

    private void updatePersonalBests(){
        Run farthest = RunUtils.getLongestRun(runList);

        if (farthest != null){
            String distance = RunUtils.distanceToString(farthest.getDistance(getDistanceUnit()), getDistanceUnit());
            String date = RunUtils.dateToString(farthest.getDate());
            view.setFarthestDistanceText(distance, date);
        }


        Run fastestPace = RunUtils.getFastestPace(runList);
        if (fastestPace != null){
            String pace = RunUtils.paceToString(fastestPace.getPace(getDistanceUnit()), getDistanceUnit());
            String date = RunUtils.dateToString(fastestPace.getDate());
            view.setFastestPaceText(pace, date);
        }

        Run longest = RunUtils.getLongestDuration(runList);
        if (longest != null){
            String time = RunUtils.timeToString(longest.getTime(), true);
            String date = RunUtils.dateToString(longest.getDate());
            view.setLongestDurationText(time, date);
        }
    }

    private void update400mChartData(){
        List<Run> runs400m = RunUtils.filterList(runList, RunPredicates.isRunFromDistance(.4, .25));

        List<Entry> scatterData = calculateScatterData(runs400m);
        List<Entry> bestRunData = calculateBestRunData(runs400m, scatterData.size());
        List<Entry> averageRunData = calculateAverageRunData(runs400m, scatterData.size());

        view.setGraph400mData(scatterData, bestRunData, averageRunData);
    }

    private void updateMileChartData(){
        List<Run> runsMile = RunUtils.filterList(runList, RunPredicates.isRunFromDistance(1.6, 1));

        List<Entry> scatterData = calculateScatterData(runsMile);
        int scatterDataSize = scatterData == null ? 0  : scatterData.size();
        List<Entry> bestRunData = calculateBestRunData(runsMile, scatterDataSize);
        List<Entry> averageRunData = calculateAverageRunData(runsMile, scatterDataSize);

        view.setGraphMileData(scatterData, bestRunData, averageRunData);
    }

    private void update5kChartData(){
        List<Run> runs5k = RunUtils.filterList(runList, RunPredicates.isRunFromDistance(5, 3.1));

        List<Entry> scatterData = calculateScatterData(runs5k);
        List<Entry> bestRunData = calculateBestRunData(runs5k, scatterData.size());
        List<Entry> averageRunData = calculateAverageRunData(runs5k, scatterData.size());

        view.setGraph5kData(scatterData, bestRunData, averageRunData);
    }

    private void update10kChartData(){
        List<Run> runs10k = RunUtils.filterList(runList, RunPredicates.isRunFromDistance(10, 6.2));

        List<Entry> scatterData = calculateScatterData(runs10k);
        List<Entry> bestRunData = calculateBestRunData(runs10k, scatterData.size());
        List<Entry> averageRunData = calculateAverageRunData(runs10k, scatterData.size());

        view.setGraph10kData(scatterData, bestRunData, averageRunData);
    }

    private void update21kChartData(){
        List<Run> runs21k = RunUtils.filterList(runList, RunPredicates.isRunFromDistance(21, 13.1));

        List<Entry> scatterData = calculateScatterData(runs21k);
        List<Entry> bestRunData = calculateBestRunData(runs21k, scatterData.size());
        List<Entry> averageRunData = calculateAverageRunData(runs21k, scatterData.size());

        view.setGraph21kData(scatterData, bestRunData, averageRunData);
    }

    private void update42kChartData() {
        List<Run> runs42k = RunUtils.filterList(runList, RunPredicates.isRunFromDistance(42, 26.2));

        List<Entry> scatterData = calculateScatterData(runs42k);
        List<Entry> bestRunData = calculateBestRunData(runs42k, scatterData.size());
        List<Entry> averageRunData = calculateAverageRunData(runs42k, scatterData.size());

        view.setGraph42kData(scatterData, bestRunData, averageRunData);
    }

    private List<Entry> calculateScatterData(List<Run> runs){
        if (runs.size() < 1){
            return Collections.emptyList();
        }

        List<Entry> scatterData = new ArrayList<>();
        Run fastestRun = RunUtils.getFastestRun(runs);

        //Fastest run will always be added
        scatterData.add(new Entry(1, fastestRun.getTime()));

        //Start at 2 because index 0 is outside of the graph and index 1 is already occupied by fastestRun
        int counter = 2;
        for (int i = 1; i < runs.size(); i++){
            Run r = runs.get(i);

            scatterData.add(new Entry(counter, r.getTime()));
            counter++;
        }

        return scatterData;
    }

    /**
     *
     * @param runs
     * @param scatterDataSize bestRunData is represented as a line in the graph , and the x-value of
     *                       the endpoint of the line has to be scatterDataSize + 1, to add the necessary
     *                       padding to the graph and avoid the scatter data dots going outside of the graph.
     * @return
     */
    private List<Entry> calculateBestRunData(List<Run> runs, int scatterDataSize){
        if (runs.size() < 1){
            return Collections.emptyList();
        }

        Run fastestRun = RunUtils.getFastestRun(runs);
        List<Entry> bestRunData = new ArrayList<>();

        bestRunData.add(new Entry(0, fastestRun.getTime()));
        bestRunData.add(new Entry(scatterDataSize + 1, fastestRun.getTime()));

        return bestRunData;
    }

    /**
     *
     * @param runs
     * @param scatterDataSize averageRunData is represented as a line in the graph , and the x-value of
     *                       the endpoint of the line has to be scatterDataSize + 1, to add the necessary
     *                       padding to the graph and avoid the scatter data dots going outside of the graph.
     * @return
     */
    private List<Entry> calculateAverageRunData(List<Run> runs, int scatterDataSize){
        if (runs.size() < 1){
            return Collections.emptyList();
        }

        List<Entry> averageRunData = new ArrayList<>();

        double averageTime = RunUtils.getAverageTime(runs);
        averageRunData.add(new Entry(0, (float) averageTime));
        averageRunData.add(new Entry(scatterDataSize + 1, (float) averageTime));

        return averageRunData;
    }

    private DistanceUnit getDistanceUnit(){
        return preferencesRepository.getDistanceUnit();
    }

}
