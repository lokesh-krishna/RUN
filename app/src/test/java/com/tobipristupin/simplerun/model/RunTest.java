package com.tobipristupin.simplerun.model;

import com.tobipristupin.simplerun.data.model.DistanceUnit;
import com.tobipristupin.simplerun.data.model.Run;

import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by Tobi on 10/20/2017.
 */

public class RunTest {

    Run similarRun = Run.fromKilometers(1, 2, 3, 3);
    Run similarRun2 = Run.fromKilometers(1, 2, 3, 3);
    Run differentRun = Run.fromMiles(45, 2, 567, 3);

    @Test public void testEquals(){
        Assert.assertEquals(true, similarRun.equals(similarRun2));
        Assert.assertEquals(false, similarRun.equals(differentRun));
        
        //Reflexive
        Assert.assertEquals(true, similarRun.equals(similarRun));
        
        //Symmetric
        Assert.assertEquals(true, similarRun.equals(similarRun2) && similarRun2.equals(similarRun));
       
        Assert.assertEquals(false, similarRun.equals(null));
    }

    @Test public void testHashCode(){
        Assert.assertEquals(similarRun.hashCode(), similarRun.hashCode());
        Assert.assertEquals(similarRun.hashCode(), similarRun2.hashCode());
        Assert.assertNotEquals(similarRun.hashCode(), differentRun.hashCode());
    }

    @Test public void testPaceUpdate(){
        Run run = Run.fromKilometers(1, 1000, 0, 0);
        long kmPace1 = run.getPace(DistanceUnit.KM);

        run.setTime(2000);
        long kmPace2 = run.getPace(DistanceUnit.KM);

        Assert.assertEquals(false, kmPace1 == kmPace2);

        run.setDistanceKilometres(5);
        long kmPace3 = run.getPace(DistanceUnit.KM);

        Assert.assertEquals(false, kmPace2 == kmPace3);
    }

    @Test public void testCompareTo(){
        List<Run> list = new ArrayList<>();
        list.add(Run.fromKilometers(0, 0, 3, 0));
        list.add(Run.fromKilometers(0, 0, 1, 0));
        list.add(Run.fromKilometers(0, 0, 2, 0));
        Collections.sort(list);

        Assert.assertEquals(true, list.get(0).getDate() == 1);
        Assert.assertEquals(true, list.get(1).getDate() == 2);
    }
}
