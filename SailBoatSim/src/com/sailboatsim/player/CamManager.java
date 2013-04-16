/**
 * 
 */
package com.sailboatsim.player;

import com.jme3.input.FlyByCamera;
import com.jme3.input.KeyInput;
import com.jme3.math.FastMath;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.renderer.Camera;
import com.jme3.scene.CameraNode;
import com.jme3.scene.control.CameraControl.ControlDirection;
import com.sailboatsim.game.InGameState;
import com.sailboatsim.game.boat.Boat;
import com.sailboatsim.utils.KeyboardInput;
import com.sailboatsim.utils.SimpleEventListener;

/**
 * @author eric
 * 
 */
public class CamManager implements SimpleEventListener {
    private final CameraNode  camNode;
    //private final Vector3f   initialPos    = new Vector3f(0, 10, -30);
    private final Vector3f    initialPos    = new Vector3f(0, 20, -50);
    private final Vector3f    topPos        = new Vector3f(0, 200, 0);
    private Vector3f          currPos;
    private float             zoomFactor    = 1.0f;
    private final float       zoomFactorMin = 0.1f;
    private final Boat        playerBoat;
    private float             camHeading;
    private float             headingOffset = 0;
    private boolean           zoomIn        = false;
    private boolean           zoomOut       = false;
    private boolean           easeCam       = true;
    private final InGameState inGameState;

    public CamManager(InGameState inGameState, Camera cam, FlyByCamera flyBy, Boat playerBoat) {
        // Disable the default fly by camera
        flyBy.setEnabled(false);
        // create the camera Node
        camNode = new CameraNode("Camera Node", cam);
        // This mode means that camera copies the movements of the target:
        camNode.setControlDir(ControlDirection.SpatialToCamera);
        this.playerBoat = playerBoat;
        playerBoat.getCamNode().attachChild(camNode);
        // Move camNode, e.g. behind and above the target:
        currPos = new Vector3f(initialPos);
        camNode.setLocalTranslation(currPos);
        // Rotate the camNode to look at the target:
        camNode.lookAt(playerBoat.getPos(), Vector3f.UNIT_Y);

        this.inGameState = inGameState;
        registerDefaultKeys(inGameState);
    }

    private void registerDefaultKeys(InGameState inGameState) {
        inGameState.getPlayerUI().registerKey("Look Left", KeyInput.KEY_NUMPAD4, this);
        inGameState.getPlayerUI().registerKey("Look Right", KeyInput.KEY_NUMPAD6, this);
        inGameState.getPlayerUI().registerKey("Look Front", KeyInput.KEY_NUMPAD8, this);
        inGameState.getPlayerUI().registerKey("Reset view", KeyInput.KEY_NUMPAD2, this);
        inGameState.getPlayerUI().registerKey("Look Down", KeyInput.KEY_NUMPAD5, this);
        inGameState.getPlayerUI().registerKey("Zoom in", KeyInput.KEY_ADD, this);
        inGameState.getPlayerUI().registerKey("Zoom out", KeyInput.KEY_SUBTRACT, this);
    }

    public void onEvent(String name, Object eventData) {
        if (eventData instanceof KeyboardInput) {
            KeyboardInput input = (KeyboardInput) eventData;
            if (name.equals("Look Left")) {
                headingOffset = -FastMath.HALF_PI;
            } else if (name.equals("Look Right")) {
                headingOffset = FastMath.HALF_PI;
            } else if (name.equals("Look Front")) {
                headingOffset = FastMath.PI;
            } else if (name.equals("Look Down") && !input.keyPressed) {
                currPos = new Vector3f(topPos);
                camNode.setLocalTranslation(currPos.mult(zoomFactor));
                camNode.lookAt(playerBoat.getPos(), Vector3f.UNIT_Z);
                playerBoat.getCamNode().setLocalRotation(new Quaternion().fromAngleAxis(0, Vector3f.UNIT_Y));
                easeCam = false;
                headingOffset = 0;
            } else if (name.equals("Reset view") && !input.keyPressed) {
                playerBoat.getCamNode().setLocalRotation(new Quaternion().fromAngleAxis(0, Vector3f.UNIT_Y));
                currPos = new Vector3f(initialPos);
                zoomFactor = 1.0f;
                camNode.setLocalTranslation(currPos.mult(zoomFactor));
                camNode.lookAt(playerBoat.getPos(), Vector3f.UNIT_Y);
                playerBoat.getCamNode().setLocalRotation(new Quaternion().fromAngleAxis(-camHeading, Vector3f.UNIT_Y));
                easeCam = true;
                headingOffset = 0;
            } else if (name.equals("Zoom in")) {
                zoomIn = input.keyPressed;
            } else if (name.equals("Zoom out")) {
                zoomOut = input.keyPressed;
            }
            if (!input.keyPressed) {
                headingOffset = 0;
            }
        }
    }

    public void update(float tpf) {
        if (zoomIn) {
            zoomFactor -= tpf;
            if (zoomFactor < zoomFactorMin) {
                zoomFactor = zoomFactorMin;
            }
            camNode.setLocalTranslation(currPos.mult(zoomFactor));
        }
        if (zoomOut) {
            zoomFactor += tpf;
            camNode.setLocalTranslation(currPos.mult(zoomFactor));
        }
        // rotate camera
        camHeading = FastMath.interpolateLinear(0.01f, camHeading, playerBoat.getHeading());
        if (easeCam) {
            playerBoat.getCamNode().setLocalRotation(new Quaternion().fromAngleAxis(-camHeading + headingOffset, Vector3f.UNIT_Y));
        }
    }
}
