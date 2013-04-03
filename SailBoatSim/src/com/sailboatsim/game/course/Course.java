package com.sailboatsim.game.course;

import java.util.ArrayList;
import java.util.List;

import com.jme3.math.FastMath;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.sailboatsim.game.InGameState;
import com.sailboatsim.utils.Conf;

//-----------------------------------------------------------------------------
/**
 * Define the race course including start and finish. A course is composed of buoys.
 */
public class Course {
    private ArrayList<Buoy> mBuoyList;
    private Vector3f        startPos;
    private float           startPosRadius;

    private Course() {
    }

    public void init(InGameState inGameState) {
        for (Buoy buoy : mBuoyList) {
            buoy.init(inGameState);
        }
    }

    public List<Buoy> getBuoyList() {
        return mBuoyList;
    }

    public Buoy getNextBuoy(Buoy buoy) {
        int i = mBuoyList.indexOf(buoy);
        if ((i == -1) || (i == mBuoyList.size())) {
            return null;
        }

        return mBuoyList.get(i + 1);
    }

    public Buoy getStart() {
        return mBuoyList.get(0);
    }

    private void create() {
        mBuoyList = new ArrayList<Buoy>();

        BuoyLine buoyLine = new BuoyLine("Start", new Vector3f(280, 0, 0), new Vector3f(180, 0, 0), "Models/buoy.j3o", "Models/buoy.j3o", "Start Line");
        mBuoyList.add(buoyLine);

        BuoyOriented buoy = new BuoyOriented("1", true, new Vector3f(250, 0, 625), new Vector3f(250, 0, 700), "1", "Models/buoy.j3o");
        mBuoyList.add(buoy);

        buoy = new BuoyOriented("2", true, new Vector3f(-625, 0, 400), new Vector3f(-700, 0, 400), "2", "Models/buoy.j3o");
        mBuoyList.add(buoy);

        buoy = new BuoyOriented("3", true, new Vector3f(-1000, 0, -250), new Vector3f(-1100, 0, -250), "3", "Models/buoy.j3o");
        mBuoyList.add(buoy);

        buoy = new BuoyOriented("4", true, new Vector3f(0, 0, -625), new Vector3f(0, 0, -700), "4", "Models/buoy.j3o");
        mBuoyList.add(buoy);

        buoyLine = new BuoyLine("Finish", new Vector3f(320, 0, 0), new Vector3f(130, 0, 0), "Models/buoy.j3o", "Models/buoy.j3o", "Finish Line");
        mBuoyList.add(buoyLine);

        startPos = new Vector3f(250, 0, -250);
        startPosRadius = 125f;

        // TODO Populate buoys
        new Conf<Course>().save(this, "Course", "eRace-1");
    }

    public Vector3f getARandomStartPos() {
        float angle = FastMath.nextRandomFloat() * FastMath.TWO_PI;
        float length = FastMath.nextRandomFloat() * startPosRadius;
        return new Quaternion().fromAngleAxis(angle, Vector3f.UNIT_Y).mult(Vector3f.UNIT_Z).mult(length).add(startPos);
    }

    public static Course load(String name) {
        return new Conf<Course>().load("Course", name);
    }

    public static void main(String[] args) {
        Course course = new Course();
        course.create();
    }
}
