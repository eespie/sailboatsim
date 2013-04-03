/**
 * 
 */
package com.sailboatsim.tools.wind;

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
public class WindGrid {
    private final AssetManager  assetManager;
    private final Node          root;
    private final GeneWindState windState;

    public WindGrid(GeneWindState windState, Node rootNode, AssetManager assetManager) {
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
        Vector3f[] positions = new Vector3f[nb * nb];

        int i = 0;
        Float[] heights = new Float[nb];
        for (int z = -max; z < (max + 1); z += increment) {
            float sum = 0;
            int j = 0;
            for (int x = -max; x < (max + 1); x += increment) {
                float height = (windState.getHeight(new Vector3f(x, 0, z)) / 100f) + 1f;
                //System.out.print(" ," + height);
                heights[j] = height;
                sum += height;
                j++;
            }
            //System.out.println("");
            j = 0;
            float avg = sum / nb;
            float xf = -max;
            positions[i++] = new Vector3f(xf, gridHeight, z);
            while (j < (nb - 1)) {
                xf += (increment * heights[j++]) / avg;
                positions[i++] = new Vector3f(xf, gridHeight, z);
            }
        }

        //System.out.println("i=" + i + "  nb=" + nb + " nb2=" + (nb * nb));

        int nbv = nb * (nb - 1) * 2;

        int[] indices = new int[nbv];

        i = 0;
        int v = 0;
        while (i < nbv) {
            indices[i++] = v;
            indices[i++] = v + nb;
            v++;
        }
        System.out.println("v=" + (v + nb));

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
