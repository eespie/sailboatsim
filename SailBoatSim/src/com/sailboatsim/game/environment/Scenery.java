package com.sailboatsim.game.environment;

import com.jme3.math.Vector3f;

public interface Scenery {
    public abstract float getTerrainHeight(Vector3f position);

    public abstract boolean isWaterOk(Vector3f position, float margin);
}