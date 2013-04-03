package com.sailboatsim.game.course;

import com.jme3.asset.AssetManager;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.FastMath;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.Spatial.CullHint;
import com.sailboatsim.game.InGameState;

//-----------------------------------------------------------------------------
/*
 * Buoy used for courses.
 * 
 * understanding buoys: Zone based buoys: the buoy has an orientation that gives 3 zones. example a north (orientation 0) buoy in clockwise:
 * 
 *      ^
 *    B | C
 *  ----o----
 *      A
 * 
 * The ship must come from zone A then go to B and C to validate the buoy.
 * 
 * example an east buoy (90) counter-clockwise:
 * 
 *      |
 *      | C
 *      |
 *    A o--->
 *      |
 *      | B
 *      |
 * 
 * NOTE: If the ship should always travel through the zones A the B and C in this order whatever the zone she was in when activating the buoy.
 */
public class BuoyOriented extends Buoy {
    private final boolean  isclockwise;
    private final Vector3f orientation;
    private Node           arrowNode;

    public BuoyOriented(String name, boolean isClockwise, Vector3f pos, Vector3f lookAt, String description, String model) {
        super(name, pos, description, model);
        isclockwise = isClockwise;
        orientation = lookAt.subtract(pos);
    }

    @Override
    public void init(InGameState inGameState) {
        super.init(inGameState);
        AssetManager assetManager = inGameState.getAssetManager();
        Spatial spatial = assetManager.loadModel("Models/arrow.j3o");
        spatial.setLocalScale(3);
        Material mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md"); // create a simple material
        mat.setColor("Color", new ColorRGBA(0, 1, 0, 1)); // set color of material to blue
        //mat.getAdditionalRenderState().setBlendMode(BlendMode.Alpha);
        spatial.setMaterial(mat);
        Node node = new Node();
        node.attachChild(spatial);
        node.setLocalRotation(new Quaternion().fromAngleAxis(-FastMath.QUARTER_PI, Vector3f.UNIT_X));
        arrowNode = new Node();
        arrowNode.attachChild(node);
        arrowNode.setLocalTranslation(0, 5, 0);
        arrowNode.setCullHint(CullHint.Always);
        rootBuoy.attachChild(arrowNode);
    }

    @Override
    public boolean update(BuoyState buoyState, Vector3f boatPos) {
        Vector3f toBuoy = pos.subtract(boatPos);
        float angle = FastMath.atan2(-toBuoy.z, toBuoy.x);
        if (isclockwise) {
            angle += FastMath.PI;
        }
        arrowNode.setLocalRotation(new Quaternion().fromAngleAxis(angle, Vector3f.UNIT_Y));

        int zone = getZone(boatPos);
        boolean isPassed = buoyState.updateState(zone);
        if (isPassed || (toBuoy.lengthSquared() < 100)) {
            arrowNode.setCullHint(CullHint.Always);
        } else {
            arrowNode.setCullHint(CullHint.Dynamic);
        }
        return isPassed;
    }

    protected int getZone(Vector3f boatPos) {
        // ship position relative to the buoy
        Vector3f vec = boatPos.subtract(pos);

        // first determine the zone relative to the buoy
        int zone = 0; // zone A

        // Dot product
        if ((vec.dot(orientation)) > 0) {
            zone = 1; // zone B
            // Vector product
            float vp = vec.cross(orientation).y;
            if ((isclockwise && (vp > 0)) || (!isclockwise && (vp < 0))) {
                zone = 2; // zone C
            }
        }
        return zone;
    }

}
