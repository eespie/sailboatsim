/**
 * 
 */
package com.sailboatsim.game.course;

import com.jme3.material.Material;
import com.jme3.material.RenderState;
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

    public Node init(GameState inGameState) {
        if (model != null) {
            Spatial spatial = inGameState.getAssetManager().loadModel(model);
            rootBuoy = new Node();
            rootBuoy.attachChild(spatial);
            rootBuoy.setLocalTranslation(pos);

            Spatial flagNode = rootBuoy.descendantMatches("flag").get(0);

            Material mat = new Material(inGameState.getAssetManager(), "MatDefs/Flag/Flag.j3md");
            mat.setTexture("ColorMap", inGameState.getAssetManager().loadTexture("Textures/buoy.png"));
            mat.getAdditionalRenderState().setFaceCullMode(RenderState.FaceCullMode.Off);
            flagNode.setMaterial(mat);
            flagNode.addControl(new FlagControl(mat, inGameState.getWeather(), pos));

            return rootBuoy;
        }
        return new Node();
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
     * @param boatPos
     *            ship position
     * @param tpf
     *            Time per frame
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
