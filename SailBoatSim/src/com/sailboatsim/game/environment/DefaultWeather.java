/**
 * DefaultWeather including wind, water (sea), sky and light
 */
package com.sailboatsim.game.environment;

import com.jme3.asset.AssetManager;
import com.jme3.light.AmbientLight;
import com.jme3.light.DirectionalLight;
import com.jme3.math.ColorRGBA;
import com.jme3.math.FastMath;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.post.FilterPostProcessor;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.texture.Texture2D;
import com.jme3.util.SkyFactory;
import com.jme3.water.WaterFilter;
import com.sailboatsim.game.GameState;

/**
 * @author eric
 * 
 */
public class DefaultWeather implements Weather {
    protected WaterFilter            water;
    private final DefaultWeatherData data;
    private boolean                  plus  = false;
    private boolean                  minus = false;
    private Vector3f                 mainWindDir;
    private int                      stepAngleDeg;
    private int                      stepDisplacement;
    private int                      maxDisplacement;
    private int                      nbDisplacementStep;
    private float                    coeffSum;
    private float                    maxWindSpeed;
    private final Scenery            scenery;

    public DefaultWeather(GameState inGameState, String weather) {
        data = DefaultWeatherData.load(weather);
        scenery = inGameState.getScenery();
        init(inGameState);
    }

    private void init(GameState inGameState) {
        createLight(inGameState.getRootNode());
        createWater(inGameState.getRootNode(), inGameState.getAssetManager(), inGameState.getViewPort());
        createSky(inGameState.getRootNode(), inGameState.getAssetManager());
        stepAngleDeg = 15;
        stepDisplacement = 50;
        maxDisplacement = 300;
        nbDisplacementStep = maxDisplacement / stepDisplacement;
        coeffSum = 40.0F * ((float) Math.log(nbDisplacementStep) + 0.577215665F + (1F / (2F * nbDisplacementStep)));
        maxWindSpeed = data.globalWindSpeed + 5f;
    }

    /* (non-Javadoc)
     * @see com.sailboatsim.game.environment.Weather#getWindComposant(com.jme3.math.Vector3f)
     */
    @Override
    public Vector3f getWindComposant(Vector3f location) {
        Vector3f currWind = mainWindDir.mult(data.globalWindSpeed);
        //Vector3f currWind = new Vector3f(0, 0, 0);
        for (int angleDeg = -180; angleDeg < 0; angleDeg += stepAngleDeg) {
            float direction = data.globalWindDirection + (FastMath.DEG_TO_RAD * angleDeg);
            Vector3f dirToExplore = new Quaternion().fromAngleAxis(direction, Vector3f.UNIT_Y).mult(Vector3f.UNIT_Z);
            float heightCoeff = 0;
            for (int displacement = -maxDisplacement; displacement <= maxDisplacement; displacement += stepDisplacement) {
                if (displacement == 0) {
                    continue;
                }
                Vector3f posToExplore = location.add(dirToExplore.mult(displacement));
                float height = scenery.getTerrainHeight(posToExplore);
                if (height < 0) {
                    height = 0;
                }
                heightCoeff += FastMath.abs((float) stepDisplacement / (float) displacement) * height;
            }
            heightCoeff = (heightCoeff * data.globalWindSpeed) / coeffSum;
            float deviationAngle = data.globalWindDirection + (FastMath.DEG_TO_RAD * ((2 * angleDeg) + 180f));
            Vector3f deviation = new Quaternion().fromAngleAxis(deviationAngle, Vector3f.UNIT_Y).mult(Vector3f.UNIT_Z).mult(heightCoeff);
            currWind.addLocal(deviation);
        }
        float currWindspeed2 = currWind.lengthSquared();
        if (currWindspeed2 > (maxWindSpeed * maxWindSpeed)) {
            currWind.normalizeLocal().multLocal(maxWindSpeed);
        } else if (currWindspeed2 < 1) {
            currWind.normalizeLocal();
        }
        return currWind;
    }

    /* (non-Javadoc)
     * @see com.sailboatsim.game.environment.Weather#update(float)
     */
    @Override
    public void update(float tpf) {

        if (plus) {
            data.globalWindSpeed += tpf * 5f;
        }
        if (minus) {
            data.globalWindSpeed -= tpf * 5f;
        }

        if (data.globalWindSpeed < 1f) {
            data.globalWindSpeed = 1f;
        }
        if (data.globalWindSpeed > 20f) {
            data.globalWindSpeed = 20f;
        }
    }

    public void windPlus(boolean plus) {
        this.plus = plus;
    }

    public void windMinus(boolean minus) {
        this.minus = minus;
    }

    private void createSky(Node rootNode, AssetManager assetManager) {
        Spatial sky = SkyFactory.createSky(assetManager, data.textureSky, false);
        sky.setLocalScale(350);

        rootNode.attachChild(sky);
    }

    private void createWater(Node rootNode, AssetManager assetManager, ViewPort viewPort) {
        water = new WaterFilter(rootNode, data.lightDir);

        FilterPostProcessor fpp = new FilterPostProcessor(assetManager);

        water.setWaveScale(0.0045f);
        water.setMaxAmplitude(1f);
        water.setFoamExistence(new Vector3f(1f, 4, 0.5f));
        water.setFoamTexture((Texture2D) assetManager.loadTexture(data.textureWaterFoam));
        // water.setNormalScale(0.5f);

        // water.setRefractionConstant(0.25f);
        water.setRefractionStrength(0.2f);
        water.setReflectionMapSize(512);
        // water.setFoamHardness(0.6f);
        water.setWaterTransparency(0.8f);
        water.setColorExtinction(new Vector3f(5f, 20f, 30f));

        water.setWaterHeight(data.initialWaterHeight);
        water.setSpeed(0.5f);

        mainWindDir = new Quaternion().fromAngleAxis(data.globalWindDirection, Vector3f.UNIT_Y).mult(Vector3f.UNIT_Z);
        water.setWindDirection(new Vector2f(-mainWindDir.x, -mainWindDir.z));

        fpp.addFilter(water);
        viewPort.addProcessor(fpp);
    }

    private void createLight(Node rootNode) {
        // You must add a light to make the model visible
        DirectionalLight sun = new DirectionalLight();
        sun.setDirection(data.lightDir);
        sun.setColor(ColorRGBA.White.clone().multLocal(1.7f));
        rootNode.addLight(sun);
        AmbientLight amb = new AmbientLight();
        amb.setColor(ColorRGBA.White);
        rootNode.addLight(amb);
    }

}
