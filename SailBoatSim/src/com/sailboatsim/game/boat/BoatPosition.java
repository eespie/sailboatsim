package com.sailboatsim.game.boat;

import com.jme3.math.Vector3f;

public class BoatPosition {
    public Vector3f boatPos;
    public float    heading;
    public float    pitch;
    public float    roll;
    public float    curSpeed;
    public float    rotSpeed;

    public BoatPosition(Vector3f boatPos, float heading, float pitch, float roll, float curSpeed, float rotSpeed) {
        this.boatPos = boatPos;
        this.heading = heading;
        this.pitch = pitch;
        this.roll = roll;
        this.curSpeed = curSpeed;
        this.rotSpeed = rotSpeed;
    }
}