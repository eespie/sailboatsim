/**
 * 
 */
package com.sailboatsim.game.course;

import com.jme3.asset.AssetManager;
import com.jme3.material.Material;
import com.jme3.material.RenderState.BlendMode;
import com.jme3.math.ColorRGBA;
import com.jme3.math.FastMath;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.renderer.queue.RenderQueue.Bucket;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial.CullHint;
import com.jme3.scene.shape.Box;
import com.sailboatsim.game.GameState;

/**
 * @author eric
 * 
 */
public class BuoyLine extends Buoy {

    private final BuoyOriented[] buoys;
    private Node                 lineNode;

    public BuoyLine(String name, Vector3f posLeft, Vector3f posRight, String modelLeft, String ModelRight, String description) {
        super(name, posLeft.add(posRight).divide(2f), description, null);
        buoys = new BuoyOriented[2];
        buoys[0] = new BuoyOriented("Left " + name, false, posLeft, posRight, description, modelLeft);
        buoys[1] = new BuoyOriented("Right " + name, true, posRight, posLeft, description, ModelRight);
    }

    @Override
    public Node init(GameState inGameState) {
        Node buoyNode = new Node();
        for (BuoyOriented buoy : buoys) {
            buoyNode.attachChild(buoy.init(inGameState));
        }
        Vector3f p0 = buoys[0].getPos();
        Vector3f p1 = buoys[1].getPos();
        super.pos = p0.add(p1).divide(2f);

        Box box = new Box(Vector3f.ZERO, p0.distance(p1) / 2, 1f, 0.1f);
        Geometry geom = new Geometry("Box", box); // create cube geometry from the shape
        AssetManager assetManager = inGameState.getAssetManager();
        Material mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md"); // create a simple material
        mat.setColor("Color", new ColorRGBA(0, 0, 1, 0.5f)); // set color of material to blue
        mat.getAdditionalRenderState().setBlendMode(BlendMode.Alpha);
        mat.setColor("GlowColor", ColorRGBA.Green);
        geom.setMaterial(mat);
        geom.setQueueBucket(Bucket.Transparent);
        lineNode = new Node();
        lineNode.attachChild(geom);
        Vector3f pdelta = p1.subtract(p0);
        float angle = FastMath.atan2(pdelta.z, pdelta.x);
        lineNode.setLocalRotation(new Quaternion().fromAngleAxis(angle, Vector3f.UNIT_Y));
        lineNode.setLocalTranslation(p0.add(p1).divide(2f).add(new Vector3f(0, -1f, 0)));
        lineNode.setCullHint(CullHint.Always);
        buoyNode.attachChild(lineNode);
        return buoyNode;
    }

    @Override
    public BuoyState getState() {
        return new BuoyLineState();
    }

    @Override
    public boolean update(BuoyState buoyState, Vector3f boatPos) {
        super.update(buoyState, boatPos);

        if (!(buoyState instanceof BuoyLineState)) {
            return false;
        }
        lineNode.setCullHint(CullHint.Inherit);

        BuoyLineState lineState = (BuoyLineState) buoyState;
        for (int i = 0; i < buoys.length; i++) {
            buoys[i].update(lineState.getState(i), boatPos);
        }

        boolean passed = lineState.isPassed();
        if (passed) {
            lineNode.setCullHint(CullHint.Always);
        }
        return passed;
    }
}
