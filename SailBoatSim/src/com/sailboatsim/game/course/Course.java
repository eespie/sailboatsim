/**
 * 
 */
package com.sailboatsim.game.course;

import java.util.List;

import com.jme3.math.Vector3f;
import com.jme3.scene.Node;
import com.sailboatsim.game.GameState;

/**
 * @author eric
 * 
 */
public interface Course {
    public List<Buoy> getBuoyList();

    public Buoy getNextBuoy(Buoy buoy);

    public Vector3f getARandomStartPos();

    public Node init(GameState inGameState);
}
