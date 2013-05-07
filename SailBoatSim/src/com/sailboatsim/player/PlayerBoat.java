/**
 * 
 */
package com.sailboatsim.player;

import com.jme3.input.KeyInput;
import com.sailboatsim.game.InGameStateClient;
import com.sailboatsim.game.boat.DefaultBoat;
import com.sailboatsim.utils.KeyboardInput;
import com.sailboatsim.utils.SimpleEventListener;

/**
 * @author eric
 * 
 */
public class PlayerBoat extends DefaultBoat implements SimpleEventListener {

    /**
     * @param inGameStateClient
     */
    public PlayerBoat(InGameStateClient inGameStateClient) {
        super(inGameStateClient);

        inGameStateClient.getPlayerUI().registerKey("Turn Left", KeyInput.KEY_J, this);
        inGameStateClient.getPlayerUI().registerKey("Turn Right", KeyInput.KEY_K, this);
        inGameStateClient.getPlayerUI().registerKey("Turn Left", KeyInput.KEY_LEFT, this);
        inGameStateClient.getPlayerUI().registerKey("Turn Right", KeyInput.KEY_RIGHT, this);
    }

    /* (non-Javadoc)
     * @see com.sailboatsim.utils.SimpleEventListener#onEvent(java.lang.String, java.lang.Object)
     */
    @Override
    public void onEvent(String name, Object eventData) {
        if (eventData instanceof KeyboardInput) {
            KeyboardInput input = (KeyboardInput) eventData;
            if ("Turn Left".equals(name)) {
                left = input.keyPressed;
            } else if ("Turn Right".equals(name)) {
                right = input.keyPressed;
            } else if ("Set Spinaker".equals(name) && !input.keyPressed) {
                setSpinaker(!hasSpinaker());
            }
        }
    }

    @Override
    public void update(float tpf) {
        super.update(tpf);
    }
}
