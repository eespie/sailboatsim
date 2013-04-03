/**
 * 
 */
package com.sailboatsim.game.course;

/**
 * @author eric
 * 
 */
public class BuoyLineState extends BuoyState {
    private final BuoyState[] buoyStates = new BuoyState[2];

    public BuoyLineState() {
        for (int i = 0; i < buoyStates.length; i++) {
            buoyStates[i] = new BuoyState();
        }
    }

    @Override
    public boolean isPassed() {
        return buoyStates[0].isPassed() && buoyStates[1].isPassed();
    }

    public BuoyState getState(int i) {
        return buoyStates[i];
    }

}
