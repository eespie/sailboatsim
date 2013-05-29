/**
 * 
 */
package com.sailboatsim.game.course;

import com.jme3.material.Material;
import com.jme3.math.FastMath;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.Spatial;
import com.jme3.scene.control.AbstractControl;
import com.jme3.scene.control.Control;
import com.sailboatsim.game.environment.Weather;

/**
 * @author eric
 * 
 */
public class FlagControl extends AbstractControl {
    private float    gameTime = 0;
    private Material mat;
    private Weather  weather;
    private Vector3f pos;

    /**
     * 
     */
    public FlagControl() {
    }

    /**
     * @param mat
     * @param weather
     */
    public FlagControl(Material mat, Weather weather, Vector3f pos) {
        super();
        this.mat = mat;
        this.pos = pos;
        this.weather = weather;
    }

    /**
     * This is your init method. Optionally, you can modify the spatial from here (transform it, initialize userdata, etc).
     */
    @Override
    public void setSpatial(Spatial spatial) {
        super.setSpatial(spatial);
        // spatial.setUserData("index", i); // example
    }

    /**
     * Implement your spatial's behaviour here. From here you can modify the scene graph and the spatial (transform them, get and set userdata, etc). This loop controls the spatial while the Control
     * is enabled.
     */
    @Override
    protected void controlUpdate(float tpf) {
        if (spatial != null) {
            Vector3f wind = weather.getWindComposant(pos);
            spatial.setLocalRotation(new Quaternion().fromAngleAxis(FastMath.atan2(wind.x, wind.z), Vector3f.UNIT_Y));
            float speed = wind.length();
            gameTime += tpf * speed;
            mat.clearParam("Time");
            mat.setFloat("Time", gameTime);
            mat.clearParam("Amplitude");
            mat.setFloat("Amplitude", 1.14f - (0.028f * speed));
        }
    }

    @Override
    public Control cloneForSpatial(Spatial spatial) {
        final FlagControl control = new FlagControl();
        /* Optional: use setters to copy userdata into the cloned control */
        // control.setIndex(i); // example
        control.setSpatial(spatial);
        return control;
    }

    @Override
    protected void controlRender(RenderManager rm, ViewPort vp) {
        /* Optional: rendering manipulation (for advanced users) */
    }
}