package com.sailboatsim.game.environment;

import com.jme3.math.Vector3f;

public interface Weather {

    public abstract Vector3f getWindComposant(Vector3f location);

    public abstract void update(float tpf);

}