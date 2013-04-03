/**
 * 
 */
package com.sailboatsim.game.environment;

import com.jme3.asset.AssetManager;
import com.jme3.material.Material;
import com.jme3.math.Vector3f;
import com.jme3.renderer.queue.RenderQueue.ShadowMode;
import com.jme3.scene.Node;
import com.jme3.terrain.geomipmap.TerrainQuad;
import com.jme3.terrain.heightmap.AbstractHeightMap;
import com.jme3.terrain.heightmap.ImageBasedHeightMap;
import com.jme3.texture.Texture;
import com.jme3.texture.Texture.WrapMode;
import com.sailboatsim.game.InGameState;

/**
 * @author eric
 * 
 */
public class Scenery {

    private final SceneryData data;
    private TerrainQuad       terrain;

    public Scenery(InGameState inGameState, String scenery) {
        data = SceneryData.load(scenery);
        init(inGameState);
    }

    private void init(InGameState inGameState) {
        createTerrain(inGameState.getRootNode(), inGameState.getAssetManager());
    }

    private void createTerrain(Node rootNode, AssetManager assetManager) {
        Material matRock = new Material(assetManager, data.matLighting);
        matRock.setBoolean("useTriPlanarMapping", false);
        matRock.setBoolean("WardIso", true);
        matRock.setTexture("AlphaMap", assetManager.loadTexture(data.matAlpha));
        Texture heightMapImage = assetManager.loadTexture(data.heightMap);
        Texture grass = assetManager.loadTexture(data.texture0);
        grass.setWrap(WrapMode.Repeat);
        matRock.setTexture("DiffuseMap", grass);
        matRock.setFloat("DiffuseMap_0_scale", 64);
        Texture dirt = assetManager.loadTexture(data.texture1);
        dirt.setWrap(WrapMode.Repeat);
        matRock.setTexture("DiffuseMap_1", dirt);
        matRock.setFloat("DiffuseMap_1_scale", 16);
        Texture rock = assetManager.loadTexture(data.texture2);
        rock.setWrap(WrapMode.Repeat);
        matRock.setTexture("DiffuseMap_2", rock);
        matRock.setFloat("DiffuseMap_2_scale", 128);
        Texture normalMap0 = assetManager.loadTexture(data.textureNormalMap0);
        normalMap0.setWrap(WrapMode.Repeat);
        Texture normalMap1 = assetManager.loadTexture(data.textureNormalMap1);
        normalMap1.setWrap(WrapMode.Repeat);
        Texture normalMap2 = assetManager.loadTexture(data.textureNormalMap2);
        normalMap2.setWrap(WrapMode.Repeat);
        matRock.setTexture("NormalMap", normalMap0);
        matRock.setTexture("NormalMap_1", normalMap2);
        matRock.setTexture("NormalMap_2", normalMap2);

        AbstractHeightMap heightmap = null;
        try {
            heightmap = new ImageBasedHeightMap(heightMapImage.getImage(), 0.25f);
            heightmap.load();
        } catch (Exception e) {
            e.printStackTrace();
        }
        terrain = new TerrainQuad("terrain", 65, 513, heightmap.getHeightMap());
        terrain.setMaterial(matRock);
        terrain.setLocalScale(new Vector3f(5, 5, 5));
        terrain.setLocalTranslation(new Vector3f(0, -30, 0));
        terrain.setLocked(true); // unlock it so we can edit the height

        terrain.setShadowMode(ShadowMode.CastAndReceive);
        rootNode.attachChild(terrain);
    }

    /**
     * @return the terrain
     */
    public TerrainQuad getTerrain() {
        return terrain;
    }
}
