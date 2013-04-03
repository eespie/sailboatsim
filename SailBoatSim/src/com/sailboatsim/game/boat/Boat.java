/**
 * 
 */
package com.sailboatsim.game.boat;

import static com.jme3.math.FastMath.DEG_TO_RAD;
import static com.jme3.math.FastMath.PI;
import static com.jme3.math.FastMath.QUARTER_PI;
import static com.jme3.math.FastMath.RAD_TO_DEG;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.jme3.input.KeyInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.math.FastMath;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.Spatial.CullHint;
import com.sailboatsim.game.InGameState;
import com.sailboatsim.game.course.BoatCourse;
import com.sailboatsim.game.course.Course;
import com.sailboatsim.utils.Utils;

/**
 * @author eric
 * 
 */
public class Boat implements ActionListener {

    private final BoatData    data;

    private final Node        rootBoat;
    private final Node        boat;
    private final Node        camNode;
    private final Spatial     boatModel;
    private final InGameState inGameState;

    private boolean           left        = false;
    private boolean           right       = false;

    private float             heading;
    private float             pitch;
    private float             roll;
    private float             relWindAngle;
    private float             curSpeed;
    private final List<Node>  starboardSails;
    private final List<Node>  portSails;
    private final Node        spiSail;
    private float             rotSpeed;
    private boolean           hasSpinaker = false;

    private BoatCourse        boatCourse;

    public Boat(InGameState inGameState) {
        data = BoatData.load("first");

        this.inGameState = inGameState;
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

        curSpeed = 0f;
        rotSpeed = 0f;

        //keys
        Map<String, Integer> keys = new HashMap<String, Integer>();
        keys.put("Turn Left", KeyInput.KEY_J);
        keys.put("Turn Right", KeyInput.KEY_K);
        inGameState.registerKeys(keys, this);
    }

    public void setCourse(Course course) {
        boatCourse = new BoatCourse(course);
    }

    public void update(float tpf) {
        if (!inGameState.isRunning()) {
            return;
        }

        Vector3f boatPos = rootBoat.getLocalTranslation();
        Vector3f boatDir = boat.getLocalRotation().mult(Vector3f.UNIT_Z).mult(curSpeed);

        Vector3f windVector = inGameState.getWeather().getWindComposant(boatPos);
        Vector3f windRelVector = windVector.subtract(boatDir);

        relWindAngle = FastMath.atan2(windRelVector.x, -windRelVector.z) - heading;
        relWindAngle = Utils.angleToMinusPiPi(relWindAngle);

        float relWindAbs = FastMath.abs(relWindAngle);

        float windAspect = Utils.angleToMinusPiPi(FastMath.atan2(windVector.x, -windVector.z) - heading);
        float windSpeed = windVector.length();
        float targetSpeed = getSpeed(windAspect, windSpeed);
        if (!inGameState.isWaterOk(boatPos, 2f)) {
            targetSpeed /= 2.0f;
        }

        curSpeed = FastMath.interpolateLinear(data.yawInertia, curSpeed, targetSpeed);

        if (!inGameState.isWaterOk(boatPos, 0)) {
            curSpeed = 0.1f;
        }

        inGameState.display(" Speed " + (int) curSpeed + " Heading " + (int) (RAD_TO_DEG * Utils.angleToZero2Pi(heading)) + " wind angle " + (int) (RAD_TO_DEG * windAspect) + " rel wind angle " + (int) (RAD_TO_DEG * relWindAngle) + "  rel wind "
                + (int) windRelVector.length() + " Pos x=" + (int) (boatPos.x) + " Pos z=" + (int) (boatPos.z));

        if (left) {
            rotSpeed = FastMath.interpolateLinear(data.yawInertia, rotSpeed, -(FastMath.sqrt(curSpeed + 1f) + 0.5f) / 2.0f);
        } else if (right) {
            rotSpeed = FastMath.interpolateLinear(data.yawInertia, rotSpeed, (FastMath.sqrt(curSpeed + 1f) + 0.5f) / 2.0f);
        } else {
            rotSpeed = FastMath.interpolateLinear(0.05f, rotSpeed, 0);
        }
        heading += tpf * rotSpeed;
        float displacement = tpf * curSpeed * 2.0f;

        roll = FastMath.interpolateLinear(0.05f, roll, (FastMath.abs(windAspect) < QUARTER_PI ? windAspect / QUARTER_PI : (FastMath.sign(windAspect) * (PI - FastMath.abs(windAspect))) / (3 * QUARTER_PI)) * windRelVector.length() * DEG_TO_RAD);

        pitch += tpf * (1f + (curSpeed / (5f + FastMath.abs(windAspect))));
        float pitchAngle = (5f - (windSpeed / 8f)) * DEG_TO_RAD * ((FastMath.sin(pitch) * FastMath.cos(pitch)) + FastMath.sin(pitch + (FastMath.abs(windAspect) / 2f)));

        Quaternion rot = new Quaternion().fromAngles(pitchAngle, -heading, -roll);

        boat.setLocalRotation(rot);

        Vector3f ld = new Vector3f(0, 0, displacement);
        rot = new Quaternion().fromAngleAxis(-heading, Vector3f.UNIT_Y);
        Vector3f gd = rot.mult(ld);
        boatPos = boatPos.add(gd);

        //boat.setLocalRotation(rot);
        rootBoat.setLocalTranslation(boatPos);

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

        boatCourse.update(boatPos);
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

    public void onAction(String name, boolean keyPressed, float tpf) {
        if ("Turn Left".equals(name)) {
            left = keyPressed;
        } else if ("Turn Right".equals(name)) {
            right = keyPressed;
        }
    }

    public Vector3f getLocalTranslation() {
        return rootBoat.getLocalTranslation();
    }

    /**
     * @return the heading
     */
    public float getHeading() {
        return heading;
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
        rootBoat.setLocalTranslation(pos);
    }

}
