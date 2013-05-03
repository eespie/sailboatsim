package com.sailboatsim.game.boat;

import com.jme3.math.Vector3f;

public class BoatPosition {
    public float    gameTime;
    public Vector3f boatPos;
    public float    heading;
    public float    pitch;
    public float    roll;
    public float    curSpeed;
    public float    rotSpeed;

    public BoatPosition(float gameTime, Vector3f boatPos, float heading, float pitch, float roll, float curSpeed, float rotSpeed) {
        this.gameTime = gameTime;
        this.boatPos = boatPos;
        this.heading = heading;
        this.pitch = pitch;
        this.roll = roll;
        this.curSpeed = curSpeed;
        this.rotSpeed = rotSpeed;
    }
}