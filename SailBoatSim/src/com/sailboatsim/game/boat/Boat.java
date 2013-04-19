/**
 * 
 */
package com.sailboatsim.game.boat;

import static com.jme3.math.FastMath.DEG_TO_RAD;
import static com.jme3.math.FastMath.PI;
import static com.jme3.math.FastMath.QUARTER_PI;
import static com.jme3.math.FastMath.RAD_TO_DEG;

import java.util.List;

import com.jme3.math.FastMath;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.Spatial.CullHint;
import com.sailboatsim.game.InGameState;
import com.sailboatsim.game.course.BoatCourse;
import com.sailboatsim.game.course.Buoy;
import com.sailboatsim.game.course.Course;
import com.sailboatsim.utils.Utils;

/**
 * @author eric
 * 
 */
/**
 * @author eric
 * 
 */
public class Boat {

    private final BoatData       data;

    private final Node           rootBoat;
    private final Node           boat;
    private final Node           camNode;
    private final Spatial        boatModel;
    protected final InGameState  inGameState;

    protected boolean            left        = false;
    protected boolean            right       = false;

    protected final BoatPosition position;

    private final List<Node>     starboardSails;
    private final List<Node>     portSails;
    private final Node           spiSail;
    private boolean              hasSpinaker = false;

    private BoatCourse           boatCourse;

    private float                relWindAngle;
    private float                windAspect;
    private Vector3f             windRelVector;
    private Vector3f             boatSpeed;

    private boolean              finished    = false;

    public Boat(InGameState inGameState) {
        this.inGameState = inGameState;
        data = BoatData.load("first");

        rootBoat = new Node();
        camNode = new Node();
        rootBoat.attachChild(camNode);
        boat = new Node();
        rootBoat.attachChild(boat);
        boatModel = inGameState.getAssetManager().loadModel(data.boatModel);
        boatModel.setLocalTranslation(data.modelLocalTranslation);
        boat.attachChild(boatModel);
        starboardSails = boat.descendantMatches("sail.-1");
        portSails = boat.descendantMatches("sail.-2");
        spiSail = (Node) boat.descendantMatches("sail3").get(0);

        position = new BoatPosition(rootBoat.getLocalTranslation(), 0, 0, 0, 0, 0);
    }

    public void setCourse(Course course) {
        boatCourse = new BoatCourse(course);
    }

    public void update(float tpf) {
        if (!inGameState.isRunning()) {
            return;
        }
        float inTpf = tpf * 1.50f;

        Vector3f boatDir = boat.getLocalRotation().mult(Vector3f.UNIT_Z).mult(position.curSpeed);

        Vector3f windVector = inGameState.getWeather().getWindComposant(position.boatPos);
        windRelVector = windVector.subtract(boatDir);

        relWindAngle = FastMath.atan2(windRelVector.x, -windRelVector.z) - position.heading;
        relWindAngle = Utils.angleToMinusPiPi(relWindAngle);

        float relWindAbs = FastMath.abs(relWindAngle);

        windAspect = Utils.angleToMinusPiPi(FastMath.atan2(windVector.x, -windVector.z) - position.heading);
        float windSpeed = windVector.length();
        float targetSpeed = getSpeed(windAspect, windSpeed);
        if (!inGameState.isWaterOk(position.boatPos, 2f)) {
            targetSpeed /= 2.0f;
        }

        position.curSpeed = FastMath.interpolateLinear(data.yawInertia, position.curSpeed, targetSpeed);

        if (!inGameState.isWaterOk(position.boatPos, 0)) {
            position.curSpeed = 0.1f;
        }

        if (left) {
            position.rotSpeed = FastMath.interpolateLinear(data.yawInertia, position.rotSpeed, -(FastMath.sqrt(position.curSpeed + 1f) + 0.5f) / 2.0f);
        } else if (right) {
            position.rotSpeed = FastMath.interpolateLinear(data.yawInertia, position.rotSpeed, (FastMath.sqrt(position.curSpeed + 1f) + 0.5f) / 2.0f);
        } else {
            position.rotSpeed = FastMath.interpolateLinear(0.05f, position.rotSpeed, 0);
        }
        position.heading += inTpf * position.rotSpeed;
        float displacement = inTpf * position.curSpeed * 2.0f;

        position.roll = FastMath.interpolateLinear(0.05f, position.roll, (FastMath.abs(windAspect) < QUARTER_PI ? windAspect / QUARTER_PI : (FastMath.sign(windAspect) * (PI - FastMath.abs(windAspect))) / (3 * QUARTER_PI)) * windRelVector.length()
                * DEG_TO_RAD);

        position.pitch += inTpf * (1f + (position.curSpeed / (5f + FastMath.abs(windAspect))));
        float pitchAngle = (5f - (windSpeed / 8f)) * DEG_TO_RAD * ((FastMath.sin(position.pitch) * FastMath.cos(position.pitch)) + FastMath.sin(position.pitch + (FastMath.abs(windAspect) / 2f)));

        Quaternion rot = new Quaternion().fromAngles(pitchAngle, -position.heading, -position.roll);

        boat.setLocalRotation(rot);

        Vector3f ld = new Vector3f(0f, 0f, 1f);
        rot = new Quaternion().fromAngleAxis(-position.heading, Vector3f.UNIT_Y);
        Vector3f gd = rot.mult(ld);
        position.boatPos = position.boatPos.add(gd.mult(displacement));
        boatSpeed = gd.mult(position.curSpeed);

        rootBoat.setLocalTranslation(position.boatPos);

        // Display sails
        float sailRot = (relWindAbs < 0.60f ? (relWindAbs * 0.1f) / 0.6f : (0.51f * relWindAbs) - 0.21f);
        if (relWindAngle > 0) {
            int i = 0;
            for (Node node : starboardSails) {
                node.setCullHint(CullHint.Dynamic);
                node.setLocalRotation(new Quaternion().fromAngleAxis(-sailRot, data.sailAxis[i++]));
            }
            for (Node node : portSails) {
                node.setCullHint(CullHint.Always);
            }
        } else {
            for (Node node : starboardSails) {
                node.setCullHint(CullHint.Always);
            }
            int i = 0;
            for (Node node : portSails) {
                node.setCullHint(CullHint.Dynamic);
                node.setLocalRotation(new Quaternion().fromAngleAxis(sailRot, data.sailAxis[i++]));
            }
        }
        if (hasSpinaker) {
            spiSail.setCullHint(CullHint.Dynamic);
        } else {
            spiSail.setCullHint(CullHint.Always);
        }

        finished = boatCourse.update(position.boatPos);
    }

    private float getSpeed(float windAngle, float windSpeed) {
        int wa = (int) FastMath.abs(windAngle * RAD_TO_DEG);
        if (wa < 0) {
            wa = 0;
        } else if (wa > 180) {
            wa = 180;
        }
        int ws = (int) windSpeed;
        if (ws < 0) {
            ws = 0;
        } else if (ws > 20) {
            ws = 20;
        }
        return data.speedData[ws][wa];
    }

    /**
     * @return the boat
     */
    public Node getBoat() {
        return rootBoat;
    }

    public Vector3f getPos() {
        return rootBoat.getLocalTranslation();
    }

    /**
     * @return the heading
     */
    public float getHeading() {
        return position.heading;
    }

    /**
     * @return the relWind
     */
    public float getRelWind() {
        return relWindAngle;
    }

    /**
     * @return the hasSpinaker
     */
    public boolean hasSpinaker() {
        return hasSpinaker;
    }

    /**
     * @param hasSpinaker
     *            the hasSpinaker to set
     */
    public void setSpinaker(boolean hasSpinaker) {
        this.hasSpinaker = hasSpinaker;
    }

    /**
     * @return the camNode
     */
    public Node getCamNode() {
        return camNode;
    }

    public void setPosition(Vector3f pos) {
        position.boatPos = pos;
    }

    /**
     * @return
     * @see com.sailboatsim.game.course.BoatCourse#getNextBuoy()
     */
    public Buoy getNextBuoy() {
        return boatCourse.getNextBuoy();
    }

    /**
     * @return the windAspect -PI to PI
     */
    public float getRelWindAspect() {
        return relWindAngle;
    }

    /**
     * @return relative wind speed
     */
    public float getRelWindSpeed() {
        return windRelVector.length();
    }

    /**
     * @return the curSpeed
     */
    public float getCurSpeed() {
        return FastMath.floor(position.curSpeed * 10.0f) / 10.0f;
    }

    /**
     * @return the boatSpeed
     */
    public Vector3f getBoatSpeed() {
        return boatSpeed;
    }

    /**
     * @return the finished
     */
    public boolean hasFinished() {
        return finished;
    }

}
