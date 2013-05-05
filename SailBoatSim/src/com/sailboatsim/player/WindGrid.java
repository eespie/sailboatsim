/**
 * 
 */
package com.sailboatsim.player;

import com.jme3.asset.AssetManager;
import com.jme3.input.KeyInput;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Mesh;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial.CullHint;
import com.jme3.scene.VertexBuffer.Type;
import com.jme3.util.BufferUtils;
import com.sailboatsim.game.InGameStateClient;
import com.sailboatsim.utils.KeyboardInput;
import com.sailboatsim.utils.SimpleEventListener;

/**
 * @author eric
 * 
 */
public class WindGrid implements SimpleEventListener {
    private final AssetManager      assetManager;
    private final Node              gridRoot;
    private final InGameStateClient inGameStateClient;
    private boolean                 visible = false;

    public WindGrid(InGameStateClient inGameStateClient, Node rootNode, AssetManager assetManager) {
        this.inGameStateClient = inGameStateClient;
        this.assetManager = assetManager;
        gridRoot = new Node();
        rootNode.attachChild(gridRoot);
        createGrid();
    }

    public void createGrid() {
        int increment = 50;
        int max = 1800;
        int gridHeight = 1;
        Mesh m = new Mesh();
        m.setMode(Mesh.Mode.Lines);
        m.setLineWidth(1f);

        int nb = (((max / increment) * 2) + 1);
        Vector3f[] positions = new Vector3f[nb * nb * 4];

        int i = 0;
        for (int z = -max; z < (max + 1); z += increment) {
            for (int x = -max; x < (max + 1); x += increment) {
                Vector3f location = new Vector3f(x, gridHeight, z);
                positions[i++] = location;
                Vector3f wind = inGameStateClient.getWeather().getWindComposant(location);
                positions[i++] = location.add(wind);
            }
        }

        int nbv = nb * nb * 4;

        int[] indices = new int[nbv];

        for (int index = 0; index < nbv; index++) {
            indices[index] = index;
        }

        m.setBuffer(Type.Position, 3, BufferUtils.createFloatBuffer(positions));
        m.setBuffer(Type.Index, 1, indices);

        m.updateBound();
        m.updateCounts();
        Material mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        mat.setColor("Color", ColorRGBA.Yellow);
        Geometry gridGeometry = new Geometry("Grid", m);
        gridGeometry.setMaterial(mat);
        gridRoot.attachChild(gridGeometry);
        registerDefaultKeys(inGameStateClient);
        setVisibility();
    }

    private void registerDefaultKeys(InGameStateClient inGameStateClient) {
        inGameStateClient.getPlayerUI().registerKey("Show grid", KeyInput.KEY_W, this);
    }

    private void setVisibility() {
        if (visible) {
            gridRoot.setCullHint(CullHint.Dynamic);
        } else {
            gridRoot.setCullHint(CullHint.Always);
        }
    }

    @Override
    public void onEvent(String name, Object eventData) {
        if (eventData instanceof KeyboardInput) {
            KeyboardInput input = (KeyboardInput) eventData;
            if ("Show grid".equals(name) && !input.keyPressed) {
                visible = !visible;
                setVisibility();
            }
        }
    }

    public void update(float tpf) {

    }
}
