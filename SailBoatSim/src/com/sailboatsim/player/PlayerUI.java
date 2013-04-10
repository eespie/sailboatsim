/**
 * 
 */
package com.sailboatsim.player;

import com.jme3.asset.AssetManager;
import com.jme3.material.Material;
import com.jme3.material.RenderState.BlendMode;
import com.jme3.math.FastMath;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.renderer.queue.RenderQueue.Bucket;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.shape.Quad;
import com.sailboatsim.game.InGameState;

/**
 * @author eric
 * 
 */
public class PlayerUI {
    private final InGameState inGameState;
    private Node              gaugeNode;

    public PlayerUI(InGameState inGameState) {
        this.inGameState = inGameState;
        init();
    }

    private void init() {
        gaugeNode = new Node("BoatGauge");
        inGameState.getPlayerBoat().getBoat().attachChild(gaugeNode);

        // Create gauge
        Quad quad = new Quad(20f, 20f);
        Geometry geom = new Geometry("square", quad);
        AssetManager assetManager = inGameState.getAssetManager();
        Material mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md"); // create a simple material
        mat.setTexture("ColorMap", assetManager.loadTexture("Textures/gauge.png"));
        mat.getAdditionalRenderState().setBlendMode(BlendMode.Alpha);
        geom.setMaterial(mat);
        geom.setQueueBucket(Bucket.Translucent);
        Node internalNode = new Node();
        internalNode.attachChild(geom);
        Quaternion quat = new Quaternion().fromAngles(-FastMath.HALF_PI, FastMath.PI, 0);
        internalNode.setLocalRotation(quat);
        internalNode.setLocalTranslation(10f, 0f, -10f);
        gaugeNode.attachChild(internalNode);

        Node windNode = new Node();
        gaugeNode.attachChild(windNode);

        Node buoyNode = new Node();
        gaugeNode.attachChild(buoyNode);

    }

    public void update(float tpf) {
        gaugeNode.setLocalRotation(new Quaternion().fromAngleAxis(-inGameState.getPlayerBoat().getHeading(), Vector3f.UNIT_Y));
    }

}
