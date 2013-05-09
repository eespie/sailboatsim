/**
 * 
 */
package com.sailboatsim.player;

import com.jme3.asset.AssetManager;
import com.jme3.input.InputManager;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.material.Material;
import com.jme3.material.RenderState.BlendMode;
import com.jme3.math.ColorRGBA;
import com.jme3.math.FastMath;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.renderer.queue.RenderQueue.Bucket;
import com.jme3.scene.Geometry;
import com.jme3.scene.Mesh;
import com.jme3.scene.Node;
import com.jme3.scene.VertexBuffer.Type;
import com.jme3.scene.shape.Quad;
import com.jme3.util.BufferUtils;
import com.sailboatsim.client.InGameStateClient;
import com.sailboatsim.game.boat.DefaultBoat;
import com.sailboatsim.game.course.Buoy;
import com.sailboatsim.utils.KeyboardInput;
import com.sailboatsim.utils.SimpleEventListener;
import com.sailboatsim.utils.SimpleEventManager;
import com.sailboatsim.utils.Utils;

/**
 * @author eric
 * 
 */
public class PlayerUI implements ActionListener {
    private final InGameStateClient  inGameStateClient;
    private Node                     gaugeNode;
    private Node                     windNode;
    private Node                     buoyNode;
    private AssetManager             assetManager;
    // Player input keys management
    private final SimpleEventManager eventManager = new SimpleEventManager();

    // Called first (before other modules)
    public PlayerUI(InGameStateClient inGameStateClient) {
        this.inGameStateClient = inGameStateClient;
    }

    // Called last (after all other modules init)
    public void init(DefaultBoat playerBoat) {
        gaugeNode = new Node("BoatGauge");
        playerBoat.getBoat().attachChild(gaugeNode);

        // Create gauge
        Quad quad = new Quad(20f, 20f);
        Geometry geom = new Geometry("square", quad);
        assetManager = inGameStateClient.getAssetManager();
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

        windNode = new Node();
        gaugeNode.attachChild(windNode);
        Node gauge = getGauge(ColorRGBA.Blue);
        gauge.setLocalTranslation(0, 0, 10);
        windNode.attachChild(gauge);

        buoyNode = new Node();
        gaugeNode.attachChild(buoyNode);
        gauge = getGauge(ColorRGBA.Green);
        gauge.setLocalRotation(new Quaternion().fromAngleAxis(FastMath.PI, Vector3f.UNIT_Y));
        gauge.setLocalTranslation(0, 0, 8);
        buoyNode.attachChild(gauge);

    }

    public void update(float tpf) {
        DefaultBoat playerBoat = inGameStateClient.getPlayerBoat();
        gaugeNode.setLocalRotation(new Quaternion().fromAngleAxis(-playerBoat.getHeading(), Vector3f.UNIT_Y));
        windNode.setLocalRotation(new Quaternion().fromAngleAxis(-playerBoat.getRelWindAspect(), Vector3f.UNIT_Y));

        Buoy nextBuoy = playerBoat.getNextBuoy();
        if (nextBuoy != null) {
            Vector3f buoyPos = nextBuoy.getPos();
            Vector3f toNextBuoy = buoyPos.subtract(playerBoat.getPos());
            Vector3f toNextBuoyNorm = toNextBuoy.normalize();
            Vector3f boatSpeedNorm = playerBoat.getBoatSpeed().normalize();
            float buoyAngle = Utils.angleToMinusPiPi((FastMath.atan2(toNextBuoyNorm.z, toNextBuoyNorm.x) - FastMath.atan2(boatSpeedNorm.z, boatSpeedNorm.x)));
            buoyNode.setLocalRotation(new Quaternion().fromAngleAxis(-buoyAngle, Vector3f.UNIT_Y));
        } else {
            buoyNode.setLocalRotation(new Quaternion().fromAngleAxis(0, Vector3f.UNIT_Y));
        }
    }

    private Node getGauge(ColorRGBA color) {
        Node root = new Node();

        Mesh mesh = new Mesh();
        Vector3f[] vertices = new Vector3f[3];
        vertices[0] = new Vector3f(0, 0, 0);
        vertices[1] = new Vector3f(1, 0, 2);
        vertices[2] = new Vector3f(-1, 0, 2);

        int[] indexes = { 0, 2, 1 };

        mesh.setBuffer(Type.Position, 3, BufferUtils.createFloatBuffer(vertices));
        mesh.setBuffer(Type.Index, 3, BufferUtils.createIntBuffer(indexes));
        mesh.updateBound();

        Geometry geo = new Geometry("arrow", mesh);
        Material mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        mat.setColor("Color", color);
        geo.setMaterial(mat);
        root.attachChild(geo);
        return root;
    }

    /**
     * @param event
     * @param listener
     * @see com.sailboatsim.utils.SimpleEventManager#register(java.lang.String, com.sailboatsim.utils.SimpleEventListener)
     */
    public void registerKey(String event, int defaultKey, SimpleEventListener listener) {
        eventManager.register(event, listener);
        InputManager inputManager = inGameStateClient.getInputManager();
        inputManager.addMapping(event, new KeyTrigger(defaultKey));
        inputManager.addListener(this, event);
    }

    // On keyboard input
    @Override
    public void onAction(String name, boolean keyPressed, float tpf) {
        KeyboardInput input = new KeyboardInput(name, keyPressed, tpf);
        eventManager.triggerEvent(name, input);
    }
}
