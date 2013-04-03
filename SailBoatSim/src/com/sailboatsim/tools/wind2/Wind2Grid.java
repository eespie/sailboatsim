/**
 * 
 */
package com.sailboatsim.tools.wind2;

import com.jme3.asset.AssetManager;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Mesh;
import com.jme3.scene.Node;
import com.jme3.scene.VertexBuffer.Type;
import com.jme3.util.BufferUtils;

/**
 * @author eric
 * 
 */
public class Wind2Grid {
    private final AssetManager   assetManager;
    private final Node           root;
    private final GeneWind2State windState;

    public Wind2Grid(GeneWind2State windState, Node rootNode, AssetManager assetManager) {
        this.windState = windState;
        this.assetManager = assetManager;
        root = rootNode;
        createGrid();
    }

    public void createGrid() {
        int increment = 50;
        int max = 1800;
        int gridHeight = 30;
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
                Vector3f wind = windState.getWeather().getWindComposant(location);
                positions[i++] = location.add(wind);
            }
        }

        //System.out.println("i=" + i + "  nb=" + nb + " nb2=" + (nb * nb));

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
        mat.setColor("Color", ColorRGBA.Gray);
        Geometry gridGeometry = new Geometry("Grid", m);
        gridGeometry.setMaterial(mat);
        root.attachChild(gridGeometry);
    }

}
