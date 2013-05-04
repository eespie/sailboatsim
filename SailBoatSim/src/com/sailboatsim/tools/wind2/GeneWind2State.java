/**
 * 
 */
package com.sailboatsim.tools.wind2;

import com.jme3.app.Application;
import com.jme3.app.SimpleApplication;
import com.jme3.app.state.AppStateManager;
import com.jme3.light.AmbientLight;
import com.jme3.light.DirectionalLight;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.renderer.Camera;
import com.jme3.scene.Node;
import com.sailboatsim.game.InGameState;
import com.sailboatsim.game.environment.DefaultScenery;
import com.sailboatsim.game.environment.DefaultWeather;

/**
 * @author eric
 * 
 */
public class GeneWind2State extends InGameState {

    public GeneWind2State(SimpleApplication app) {
        super(app);
    }

    private DefaultScenery defaultScenery;
    private DefaultWeather defaultWeather;
    private Camera  cam;

    @Override
    public void initialize(AppStateManager stateManager, Application app) {
        //super.initialize(stateManager, app);

        defaultScenery = new DefaultScenery(this, "Island-e1");
        defaultWeather = new DefaultWeather(this, "sunny");
        createLight(getRootNode());
        new Wind2Grid(this, getRootNode(), getAssetManager());

        cam = app.getCamera();
        //cam.setLocation(new Vector3f(0, 2000, 3500));
        cam.setLocation(new Vector3f(0, 3500, 0));
        cam.setFrustumFar(6000);
        //cam.lookAt(Vector3f.ZERO, Vector3f.UNIT_Y);
        cam.setAxes(new Vector3f(1, 0, 0), new Vector3f(0, 0, 1), new Vector3f(0, -1, 0));
        cam.update();
    }

    @Override
    public float getTerrainHeight(Vector3f position) {
        float height = defaultScenery.getTerrain().getHeight(new Vector2f(position.x, position.z)) - 30f;
        if (Float.isNaN(height) || (height < 0f)) {
            height = 0f;
        }
        return height;
    }

    @Override
    public void update(float tpf) {
        // TODO Auto-generated method stub
        //super.update(tpf);
    }

    private void createLight(Node rootNode) {
        // You must add a light to make the model visible
        DirectionalLight sun = new DirectionalLight();
        sun.setDirection(new Vector3f(-4.9f, -1.3f, 5.9f));
        sun.setColor(ColorRGBA.White.clone().multLocal(1.7f));
        rootNode.addLight(sun);
        AmbientLight amb = new AmbientLight();
        amb.setColor(ColorRGBA.White);
        rootNode.addLight(amb);
    }

    /**
     * @return the defaultWeather
     */
    @Override
    public DefaultWeather getWeather() {
        return defaultWeather;
    }

}
