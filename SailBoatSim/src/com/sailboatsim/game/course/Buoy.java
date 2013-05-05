/**
 * 
 */
package com.sailboatsim.game.course;

import com.jme3.math.Vector3f;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.sailboatsim.game.GameState;

/**
 * @author eric
 * 
 */
public abstract class Buoy {
    private final String name;
    private final String description;
    private final String model;      // 3D Model
    protected Vector3f   pos;
    protected Node       rootBuoy;

    /**
     * @param name
     *            Buoy name
     * @param pos
     *            buoy pos (mandatory)
     * @param description
     *            buoy desc
     * @param ThreeDModel
     *            3D model associated (null if no model associated)
     */
    public Buoy(String name, Vector3f pos, String description, String ThreeDModel) {
        this.description = description;
        this.name = name;
        model = ThreeDModel;
        this.pos = pos;
    }

    public void init(GameState inGameState) {
        if (model != null) {
            Node rootNode = inGameState.getRootNode();
            Spatial spatial = inGameState.getAssetManager().loadModel(model);
            rootBuoy = new Node();
            rootBuoy.attachChild(spatial);
            rootBuoy.setLocalTranslation(pos);
            rootNode.attachChild(rootBuoy);
        }
    }

    /**
     * @return Returns the description.
     */
    public String getDescription() {
        return description;
    }

    /**
     * @return Returns the name.
     */
    public String getName() {
        return name;
    }

    public BuoyState getState() {
        return new BuoyState();
    }

    /**
     * update the buoy state according to the ship position
     * 
     * @param buoyState
     *            current state
     * @param posx
     *            ship position
     * @param posy
     *            ship position
     * @return true if the buoy is passed
     */
    public boolean update(BuoyState buoyState, Vector3f boatPos) {
        return false;
    }

    /**
     * @return the model
     */
    public String getModel() {
        return model;
    }

    /**
     * @return the pos
     */
    public Vector3f getPos() {
        return pos;
    }
}
