/**
 * 
 */
package com.sailboatsim.player;

import com.jme3.input.KeyInput;
import com.sailboatsim.game.InGameState;
import com.sailboatsim.game.boat.Boat;
import com.sailboatsim.utils.KeyboardInput;
import com.sailboatsim.utils.SimpleEventListener;

/**
 * @author eric
 * 
 */
public class PlayerBoat extends Boat implements SimpleEventListener {

    /**
     * @param inGameState
     */
    public PlayerBoat(InGameState inGameState) {
        super(inGameState);

        inGameState.getPlayerUI().registerKey("Turn Left", KeyInput.KEY_J, this);
        inGameState.getPlayerUI().registerKey("Turn Right", KeyInput.KEY_K, this);
        inGameState.getPlayerUI().registerKey("Set Spinaker", KeyInput.KEY_S, this);
    }

    /* (non-Javadoc)
     * @see com.sailboatsim.utils.SimpleEventListener#onEvent(java.lang.String, java.lang.Object)
     */
    public void onEvent(String name, Object eventData) {
        if (eventData instanceof KeyboardInput) {
            KeyboardInput input = (KeyboardInput) eventData;
            if ("Turn Left".equals(name)) {
                setLeft(input.keyPressed);
            } else if ("Turn Right".equals(name)) {
                setRight(input.keyPressed);
            } else if ("Set Spinaker".equals(name) && !input.keyPressed) {
                setSpinaker(!hasSpinaker());
            }
        }
    }

}
