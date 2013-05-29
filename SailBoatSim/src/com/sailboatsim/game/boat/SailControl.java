/**
 * 
 */
package com.sailboatsim.game.boat;

import com.jme3.material.Material;
import com.jme3.math.FastMath;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.Spatial;
import com.jme3.scene.control.AbstractControl;
import com.jme3.scene.control.Control;

/**
 * @author eric
 * 
 */
public class SailControl extends AbstractControl {
    private float    gameTime = 0;
    private Material mat;
    private Boat     boat;

    /**
     * 
     */
    public SailControl() {
    }

    /**
     * @param mat
     * @param boat
     */
    public SailControl(Material mat, Boat boat) {
        super();
        this.mat = mat;
        this.boat = boat;
    }

    /* (non-Javadoc)
     * @see com.jme3.scene.control.Control#cloneForSpatial(com.jme3.scene.Spatial)
     */
    @Override
    public Control cloneForSpatial(Spatial arg0) {
        final SailControl control = new SailControl();
        control.setSpatial(spatial);
        return control;
    }

    /* (non-Javadoc)
     * @see com.jme3.scene.control.AbstractControl#controlRender(com.jme3.renderer.RenderManager, com.jme3.renderer.ViewPort)
     */
    @Override
    protected void controlRender(RenderManager arg0, ViewPort arg1) {

    }

    /* (non-Javadoc)
     * @see com.jme3.scene.control.AbstractControl#controlUpdate(float)
     */
    @Override
    protected void controlUpdate(float tpf) {
        if (spatial != null) {
            float angle = FastMath.sin(boat.getRelWindAspect() / 2.0f) * 1.22f;
            spatial.setLocalRotation(new Quaternion().fromAngleAxis(-angle, Vector3f.UNIT_Y));
            float speed = boat.getRelWindSpeed();
            gameTime += tpf * speed;
            mat.setFloat("WindAngle", -angle * FastMath.RAD_TO_DEG);
            mat.setFloat("Time", gameTime);
            mat.setFloat("Amplitude", 1.14f - (0.028f * speed));
        }
    }
}
