package com.sailboatsim.game.boat;

import com.jme3.math.Vector3f;
import com.jme3.scene.Node;
import com.sailboatsim.game.course.Buoy;

public interface Boat {

    public abstract void update(float tpf);

    public abstract Vector3f getPos();

    /**
     * @return the camNode
     */
    public abstract Node getCamNode();

    public abstract void setPosition(Vector3f pos);

    /**
     * @return
     * @see com.sailboatsim.game.course.BoatCourse#getNextBuoy()
     */
    public abstract Buoy getNextBuoy();

    /**
     * @return the windAspect -PI to PI
     */
    public abstract float getRelWindAspect();

    /**
     * @return relative wind speed
     */
    public abstract float getRelWindSpeed();

    /**
     * @return the left
     */
    public abstract boolean getLeft();

    /**
     * @param left
     *            the left to set
     */
    public abstract void setLeft(boolean left);

    /**
     * @return the right
     */
    public abstract boolean getRight();

    /**
     * @param right
     *            the right to set
     */
    public abstract void setRight(boolean right);

}