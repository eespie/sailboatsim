package com.sailboatsim.game.course;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.jme3.math.Vector3f;

public class BoatCourse {

    private final Map<Buoy, BuoyState> states;
    private int                        lastBuoyPassed = -1;
    private final List<Buoy>           buoyList;
    private Buoy                       nextBuoy;

    public BoatCourse(Course defaultCourse) {
        states = new HashMap<Buoy, BuoyState>();
        buoyList = defaultCourse.getBuoyList();
        for (Buoy buoy : buoyList) {
            states.put(buoy, buoy.getState());
        }
    }

    public boolean update(Vector3f boatPos) {
        nextBuoy = null;
        int index = lastBuoyPassed;
        if (index < 0) {
            index = 0;
        }
        if (index == (buoyList.size() - 1)) {
            // finished
            System.out.println("finished !!!!");
            return true;
        }

        Buoy buoy = buoyList.get(index);

        while (buoy.update(states.get(buoy), boatPos)) {
            index++;
            if (index == buoyList.size()) {
                // finished
                lastBuoyPassed = index - 1;
                return true;
            }
            // check next buoy
            buoy = buoyList.get(index);
        }
        nextBuoy = buoy;
        lastBuoyPassed = index - 1;
        return false;
    }

    /**
     * @return the nextBuoy
     */
    public Buoy getNextBuoy() {
        return nextBuoy;
    }
}
