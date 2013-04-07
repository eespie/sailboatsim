/**
 * 
 */
package com.sailboatsim.game;

import java.util.HashMap;
import java.util.Map;

import com.jme3.input.FlyByCamera;
import com.jme3.input.KeyInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.math.FastMath;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.renderer.Camera;
import com.jme3.scene.CameraNode;
import com.jme3.scene.control.CameraControl.ControlDirection;
import com.sailboatsim.game.boat.Boat;

/**
 * @author eric
 * 
 */
public class CamManager implements ActionListener {
    private final CameraNode camNode;
    //private final Vector3f   initialPos    = new Vector3f(0, 10, -30);
    private final Vector3f   initialPos    = new Vector3f(0, 20, -50);
    private Vector3f         currPos;
    private float            zoomFactor    = 1.0f;
    private final float      zoomFactorMin = 0.1f;
    private final Boat       playerBoat;
    private float            camHeading;
    private float            headingOffset = 0;
    private boolean          zoomIn        = false;
    private boolean          zoomOut       = false;

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

        registerDefaultKeys(inGameState);
    }

    private void registerDefaultKeys(InGameState inGameState) {
        Map<String, Integer> keys = new HashMap<String, Integer>();
        keys.put("Look Left", KeyInput.KEY_NUMPAD4);
        keys.put("Look Right", KeyInput.KEY_NUMPAD6);
        keys.put("Look Front", KeyInput.KEY_NUMPAD8);
        keys.put("Look Rear", KeyInput.KEY_NUMPAD2);
        keys.put("Reset view", KeyInput.KEY_NUMPAD5);
        keys.put("Zoom in", KeyInput.KEY_ADD);
        keys.put("Zoom out", KeyInput.KEY_SUBTRACT);

        inGameState.registerKeys(keys, this);
    }

    public void onAction(String name, boolean keyPressed, float tpf) {
        if (name.equals("Look Left")) {
            headingOffset = -FastMath.HALF_PI;
        } else if (name.equals("Look Right")) {
            headingOffset = FastMath.HALF_PI;
        } else if (name.equals("Look Front")) {
            headingOffset = FastMath.PI;
        } else if (name.equals("Look Rear")) {
            headingOffset = 0;
        } else if (name.equals("Reset view")) {
            currPos = initialPos;
            zoomFactor = 1.0f;
            headingOffset = 0;
            camNode.setLocalTranslation(currPos.mult(zoomFactor));
        } else if (name.equals("Zoom in")) {
            zoomIn = keyPressed;
        } else if (name.equals("Zoom out")) {
            zoomOut = keyPressed;
        }
        if (!keyPressed) {
            headingOffset = 0;
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
        playerBoat.getCamNode().setLocalRotation(new Quaternion().fromAngleAxis(-camHeading + headingOffset, Vector3f.UNIT_Y));
    }
}
