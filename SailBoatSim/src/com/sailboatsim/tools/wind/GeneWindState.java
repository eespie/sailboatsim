/**
 * 
 */
package com.sailboatsim.tools.wind;

import com.jme3.app.Application;
import com.jme3.app.SimpleApplication;
import com.jme3.app.state.AppStateManager;
import com.jme3.light.AmbientLight;
import com.jme3.light.DirectionalLight;
import com.jme3.math.ColorRGBA;
import com.jme3.math.FastMath;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.renderer.Camera;
import com.jme3.scene.Node;
import com.sailboatsim.game.InGameState;
import com.sailboatsim.game.environment.Scenery;
import com.sailboatsim.game.environment.Weather;

/**
 * @author eric
 * 
 */
public class GeneWindState extends InGameState {

    public GeneWindState(SimpleApplication app) {
        super(app);
    }

    private Scenery scenery;
    private Weather weather;
    private Camera  cam;

    private float   angle;

    @Override
    public void initialize(AppStateManager stateManager, Application app) {
        //super.initialize(stateManager, app);

        scenery = new Scenery(this, "Island-e1");
        weather = new Weather(this, "sunny");
        Vector3f wd = weather.getWindComposant(Vector3f.ZERO);
        angle = FastMath.atan2(wd.z, wd.x);
        //System.out.println("Angle = " + (FastMath.RAD_TO_DEG * angle));
        scenery.getTerrain().setLocalRotation(new Quaternion().fromAngleAxis(-angle, Vector3f.UNIT_Y));
        createLight(getRootNode());
        new WindGrid(this, getRootNode(), getAssetManager());

        cam = app.getCamera();
        //cam.setLocation(new Vector3f(0, 2000, 3500));
        cam.setLocation(new Vector3f(0, 3500, 0));
        cam.setFrustumFar(6000);
        //cam.lookAt(Vector3f.ZERO, Vector3f.UNIT_Y);
        cam.setAxes(new Vector3f(1, 0, 0), new Vector3f(0, 0, 1), new Vector3f(0, -1, 0));
        cam.update();
    }

    public float getHeight(Vector3f position) {
        Vector3f rotPos = new Quaternion().fromAngleAxis(angle, Vector3f.UNIT_Y).mult(position);
        float height = scenery.getTerrain().getHeight(new Vector2f(rotPos.x, rotPos.z));
        if (Float.isNaN(height)) {
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
     * @return the weather
     */
    @Override
    public Weather getWeather() {
        return weather;
    }

}