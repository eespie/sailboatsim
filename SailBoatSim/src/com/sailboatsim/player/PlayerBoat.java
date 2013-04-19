/**
 * 
 */
package com.sailboatsim.player;

import com.jme3.input.KeyInput;
import com.sailboatsim.game.InGameState;
import com.sailboatsim.game.boat.Boat;
import com.sailboatsim.game.boat.BoatPosition;
import com.sailboatsim.utils.KeyboardInput;
import com.sailboatsim.utils.Recorder;
import com.sailboatsim.utils.SimpleEventListener;

/**
 * @author eric
 * 
 */
public class PlayerBoat extends Boat implements SimpleEventListener {
    private final Recorder<BoatPosition> boatRecorder = new Recorder<BoatPosition>("boat.log");
    private final Recorder<KeyLogger>    keyRecorder  = new Recorder<PlayerBoat.KeyLogger>("keys.log");
    private final float                  lastGameTime = 0;

    /**
     * @param inGameState
     */
    public PlayerBoat(InGameState inGameState) {
        super(inGameState);

        boatRecorder.recording(inGameState.getGameTime(), new BoatPosition(position.boatPos, position.heading, position.pitch, position.roll, position.curSpeed, position.rotSpeed));

        inGameState.getPlayerUI().registerKey("Turn Left", KeyInput.KEY_J, this);
        inGameState.getPlayerUI().registerKey("Turn Right", KeyInput.KEY_K, this);
        inGameState.getPlayerUI().registerKey("Set Spinaker", KeyInput.KEY_S, this);
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
        float gameTime = inGameState.getGameTime();
        if (((gameTime - lastGameTime) > 0.1f) && !hasFinished()) {
            boatRecorder.recording(gameTime, new BoatPosition(position.boatPos, position.heading, position.pitch, position.roll, position.curSpeed, position.rotSpeed));
        }
        super.update(tpf);
        if (!hasFinished()) {
            keyRecorder.recording(gameTime, new KeyLogger(left, right, tpf));
        }
    }

    class KeyLogger {
        public boolean left;
        public boolean right;
        public float   tpf;

        public KeyLogger(boolean left, boolean right, float tpf) {
            this.left = left;
            this.right = right;
            this.tpf = tpf;
        }
    }
}
