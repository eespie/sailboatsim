/**
 * 
 */
package com.sailboatsim.game.environment;

import com.sailboatsim.utils.Conf;

/**
 * @author eric
 * 
 */
public class DefaultSceneryData {
    protected String matLighting;
    protected String matAlpha;
    protected String heightMap;
    protected String texture0;
    protected String texture1;
    protected String texture2;
    protected String textureNormalMap0;
    protected String textureNormalMap1;
    protected String textureNormalMap2;

    private DefaultSceneryData() {
    }

    private void init() {
        matLighting = "Common/MatDefs/Terrain/TerrainLighting.j3md";
        matAlpha = "Textures/Terrain/splat/alphamap.png";
        heightMap = "Textures/Terrain/splat/TheIslandInLake512.png";
        texture0 = "Textures/Terrain/splat/grass.jpg";
        texture1 = "Textures/Terrain/splat/dirt.jpg";
        texture2 = "Textures/Terrain/splat/road.jpg";
        textureNormalMap0 = "Textures/Terrain/splat/grass_normal.jpg";
        textureNormalMap1 = "Textures/Terrain/splat/dirt_normal.png";
        textureNormalMap2 = "Textures/Terrain/splat/road_normal.png";
    }

    public static DefaultSceneryData load(String scenery) {
        return new Conf<DefaultSceneryData>().load("Scenery", scenery);
    }

    public static void main(String[] args) {
        DefaultSceneryData scenery = new DefaultSceneryData();
        scenery.init();

        new Conf<DefaultSceneryData>().save(scenery, "Scenery", "Island-e1");
    }

}
