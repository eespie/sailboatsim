/**
 * 
 */
package com.sailboatsim.game.course;

import com.jme3.math.Vector3f;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.sailboatsim.game.InGameState;

/**
 * @author eric
 * 
 */
public abstract class Buoy {
    private final String     name;
    private final String     description;
    private final String     model;
    protected final Vector3f pos;
    protected Node rootBuoy;

    /**
     * @param description
     * @param name
     */
    public Buoy(String name, Vector3f pos, String description, String model) {
        this.description = description;
        this.name = name;
        this.model = model;
        this.pos = pos;
    }

    public void init(InGameState inGameState) {
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
