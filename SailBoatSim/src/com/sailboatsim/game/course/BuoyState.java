package com.sailboatsim.game.course;

//-----------------------------------------------------------------------------
/**
 * State of a buoy attached to a ship.
 */
public class BuoyState {
    private int mState;
    private int mZone;

    public BuoyState() {
        mState = 0;
        mZone = 0;
    }

    // -----------------------------------------------------------------------------
    /**
     * Get the state of the buoy
     * 
     * @return true if buoy is passed
     */
    public boolean isPassed() {
        return mState >= 2;
    }

    /**
     * @return the mZone
     */
    protected int getmZone() {
        return mZone;
    }

    // -----------------------------------------------------------------------------
    /**
     * Update the state of the buoy
     * 
     * @param currentZone
     *            zone where the ship is positioned
     * @return true if buoy is passed
     */
    public boolean updateState(int currentZone) {
        if (mZone == currentZone) {
            return isPassed();
        }
        if ((currentZone == (mZone + 1)) || (currentZone == (mZone - 2))) {
            mState++;
        } else {
            mState--;
        }
        mZone = currentZone;

        return isPassed();
    }
}