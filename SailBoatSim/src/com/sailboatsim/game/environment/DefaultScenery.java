/**
 * 
 */
package com.sailboatsim.game.environment;

import com.jme3.asset.AssetManager;
import com.jme3.material.Material;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.renderer.queue.RenderQueue.ShadowMode;
import com.jme3.scene.Node;
import com.jme3.terrain.geomipmap.TerrainQuad;
import com.jme3.terrain.heightmap.AbstractHeightMap;
import com.jme3.terrain.heightmap.ImageBasedHeightMap;
import com.jme3.texture.Texture;
import com.jme3.texture.Texture.WrapMode;
import com.sailboatsim.game.GameState;

/**
 * @author eric
 * 
 */
public class DefaultScenery implements Scenery {

    private final DefaultSceneryData data;
    private TerrainQuad              terrain;

    public DefaultScenery(GameState inGameState, String scenery) {
        data = DefaultSceneryData.load(scenery);
        init(inGameState);
    }

    private void init(GameState inGameState) {
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

    @Override
    public float getTerrainHeight(Vector3f position) {
        float height = terrain.getHeight(new Vector2f(position.x, position.z)) - 30f;
        if (Float.isNaN(height)) {
            height = -30f;
        }
        return height;
    }

    @Override
    public boolean isWaterOk(Vector3f position, float margin) {
        float height = terrain.getHeight(new Vector2f(position.x, position.z)) - 30f;
        if (Float.isNaN(height)) {
            return true;
        }
        return height < -margin;
    }

}
