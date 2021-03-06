/**
 * 
 */
package com.sailboatsim.game.boat;

import static com.jme3.math.FastMath.DEG_TO_RAD;
import static com.jme3.math.FastMath.PI;
import static com.jme3.math.FastMath.QUARTER_PI;
import static com.jme3.math.FastMath.RAD_TO_DEG;

import java.util.List;

import com.jme3.material.Material;
import com.jme3.material.RenderState;
import com.jme3.math.FastMath;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.sailboatsim.game.GameState;
import com.sailboatsim.game.course.BoatCourse;
import com.sailboatsim.game.course.Buoy;
import com.sailboatsim.game.course.Course;
import com.sailboatsim.game.environment.Scenery;
import com.sailboatsim.game.environment.Weather;
import com.sailboatsim.utils.Utils;

/**
 * @author eric
 * 
 */
public class DefaultBoat implements Boat {

    private final DefaultBoatData data;

    private final Node            rootBoat;
    private final Node            boat;
    private final Node            camNode;
    private final Spatial         boatModel;

    protected boolean             left        = false;
    protected boolean             right       = false;

    protected BoatPosition        position;

    private boolean               hasSpinaker = false;

    private BoatCourse            boatCourse;
    private final Weather         weather;
    private final Scenery         scenery;

    private float                 relWindAngle;
    private float                 windAspect;
    private Vector3f              windRelVector;
    private Vector3f              boatSpeed;

    private boolean               finished    = false;

    public DefaultBoat(GameState inGameState) {
        scenery = inGameState.getScenery();
        weather = inGameState.getWeather();
        data = DefaultBoatData.load("first");

        rootBoat = new Node();
        camNode = new Node();
        rootBoat.attachChild(camNode);
        boat = new Node();
        rootBoat.attachChild(boat);
        boatModel = inGameState.getAssetManager().loadModel(data.boatModel);
        boatModel.setLocalTranslation(data.modelLocalTranslation);
        boat.attachChild(boatModel);

        List<Spatial> nodes = rootBoat.descendantMatches("jib");
        if (!nodes.isEmpty()) {
            Spatial jibNode = nodes.get(0);
            Material mat = new Material(inGameState.getAssetManager(), "MatDefs/Sail/Sail.j3md");
            mat.setTexture("ColorMap", inGameState.getAssetManager().loadTexture("Textures/Sail.png"));
            mat.getAdditionalRenderState().setFaceCullMode(RenderState.FaceCullMode.Off);
            jibNode.setMaterial(mat);
            jibNode.addControl(new SailControl(mat, this));
        }

        nodes = rootBoat.descendantMatches("main-sail");
        if (!nodes.isEmpty()) {
            Spatial mainSailNode = nodes.get(0);
            Material mat = new Material(inGameState.getAssetManager(), "MatDefs/MainSail/MainSail.j3md");
            mat.setTexture("ColorMap", inGameState.getAssetManager().loadTexture("Textures/Sail.png"));
            mat.getAdditionalRenderState().setFaceCullMode(RenderState.FaceCullMode.Off);
            mainSailNode.addControl(new SailControl(mat, this));
            rootBoat.descendantMatches("node-sail").get(0).setMaterial(mat);
        }

        position = new BoatPosition(0, rootBoat.getLocalTranslation(), 0, 0, 0, 0, 0);
    }

    @Override
    public void setCourse(Course defaultCourse) {
        boatCourse = new BoatCourse(defaultCourse);
    }

    /* (non-Javadoc)
     * @see com.sailboatsim.game.boat.Boat#update(float)
     */
    @Override
    public void update(float tpf) {
        position.gameTime += tpf;

        float inTpf = 1.5F * tpf;
        float interpFactor = 1f;

        Vector3f boatDir = boat.getLocalRotation().mult(Vector3f.UNIT_Z).mult(position.curSpeed);

        Vector3f windVector = weather.getWindComposant(position.boatPos);
        windRelVector = windVector.subtract(boatDir);

        relWindAngle = FastMath.atan2(windRelVector.x, -windRelVector.z) - position.heading;
        relWindAngle = Utils.angleToMinusPiPi(relWindAngle);

        windAspect = Utils.angleToMinusPiPi(FastMath.atan2(windVector.x, -windVector.z) - position.heading);
        float windSpeed = windVector.length();
        float targetSpeed = getSpeed(windAspect, windSpeed);
        if (!scenery.isWaterOk(position.boatPos, 2f)) {
            targetSpeed /= 2.0f;
        }

        position.curSpeed = FastMath.interpolateLinear(data.yawInertia * interpFactor, position.curSpeed, targetSpeed);

        if (!scenery.isWaterOk(position.boatPos, 0)) {
            position.curSpeed = 0.1f;
        }

        if (left) {
            position.rotSpeed = FastMath.interpolateLinear(data.yawInertia * interpFactor, position.rotSpeed, -(FastMath.sqrt(position.curSpeed + 1f) + 0.5f) / 2.0f);
        } else if (right) {
            position.rotSpeed = FastMath.interpolateLinear(data.yawInertia * interpFactor, position.rotSpeed, (FastMath.sqrt(position.curSpeed + 1f) + 0.5f) / 2.0f);
        } else {
            position.rotSpeed = FastMath.interpolateLinear(0.05f * interpFactor, position.rotSpeed, 0);
        }
        position.heading += inTpf * position.rotSpeed;
        float displacement = inTpf * position.curSpeed * 2.0f;

        position.roll = FastMath.interpolateLinear(0.05f * interpFactor, position.roll, (FastMath.abs(windAspect) < QUARTER_PI ? windAspect / QUARTER_PI : (FastMath.sign(windAspect) * (PI - FastMath.abs(windAspect))) / (3 * QUARTER_PI))
                * windRelVector.length() * DEG_TO_RAD);

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

        finished = boatCourse.update(position.boatPos, tpf);
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
    @Override
    public Node getBoat() {
        return rootBoat;
    }

    /* (non-Javadoc)
     * @see com.sailboatsim.game.boat.Boat#getPos()
     */
    @Override
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

    /* (non-Javadoc)
     * @see com.sailboatsim.game.boat.Boat#getCamNode()
     */
    @Override
    public Node getCamNode() {
        return camNode;
    }

    /* (non-Javadoc)
     * @see com.sailboatsim.game.boat.Boat#setPosition(com.jme3.math.Vector3f)
     */
    @Override
    public void setPosition(Vector3f pos) {
        position.boatPos = pos;
    }

    /* (non-Javadoc)
     * @see com.sailboatsim.game.boat.Boat#getNextBuoy()
     */
    @Override
    public Buoy getNextBuoy() {
        return boatCourse.getNextBuoy();
    }

    /* (non-Javadoc)
     * @see com.sailboatsim.game.boat.Boat#getRelWindAspect()
     */
    @Override
    public float getRelWindAspect() {
        return relWindAngle;
    }

    /* (non-Javadoc)
     * @see com.sailboatsim.game.boat.Boat#getRelWindSpeed()
     */
    @Override
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

    /**
     * @return the left
     */
    @Override
    public boolean getLeft() {
        return left;
    }

    /**
     * @param left
     *            the left to set
     */
    @Override
    public void setLeft(boolean left) {
        this.left = left;
    }

    /**
     * @return the right
     */
    @Override
    public boolean getRight() {
        return right;
    }

    /**
     * @param right
     *            the right to set
     */
    @Override
    public void setRight(boolean right) {
        this.right = right;
    }

    /**
     * @return the position
     */
    @Override
    public BoatPosition getPosition() {
        return position;
    }

    /**
     * @param position
     *            the position to set
     */
    @Override
    public void setPosition(BoatPosition position) {
        this.position = position;
    }

}
