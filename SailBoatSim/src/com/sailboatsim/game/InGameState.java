/**
 * 
 */
package com.sailboatsim.game;

import static com.jme3.math.FastMath.RAD_TO_DEG;

import com.jme3.app.Application;
import com.jme3.app.SimpleApplication;
import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppStateManager;
import com.jme3.asset.AssetManager;
import com.jme3.font.BitmapFont;
import com.jme3.font.BitmapText;
import com.jme3.input.FlyByCamera;
import com.jme3.input.InputManager;
import com.jme3.input.KeyInput;
import com.jme3.math.FastMath;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.post.FilterPostProcessor;
import com.jme3.post.filters.BloomFilter;
import com.jme3.renderer.Camera;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.Node;
import com.sailboatsim.game.boat.Boat;
import com.sailboatsim.game.course.Buoy;
import com.sailboatsim.game.course.Course;
import com.sailboatsim.game.environment.Scenery;
import com.sailboatsim.game.environment.Weather;
import com.sailboatsim.player.CamManager;
import com.sailboatsim.player.PlayerBoat;
import com.sailboatsim.player.PlayerUI;
import com.sailboatsim.player.WindGrid;
import com.sailboatsim.utils.KeyboardInput;
import com.sailboatsim.utils.SimpleEventListener;
import com.sailboatsim.utils.Utils;

/**
 * @author eric
 * 
 */
public class InGameState extends AbstractAppState implements SimpleEventListener {
    private final Node         rootNode;
    private final Node         guiNode;
    private final AssetManager assetManager;
    // private final AppStateManager stateManager;
    private final InputManager inputManager;
    private final ViewPort     viewPort;
    private final FlyByCamera  flyBy;
    private final Camera       cam;
    private CamManager         camManager;

    private Boat               playerBoat;
    private boolean            isRunning = true;
    private Node               localGuiNode;

    private BitmapText         displaytext;
    private BitmapText         pausetext;
    private Weather            weather;
    private Scenery            scenery;
    private Course             course;
    private PlayerUI           playerUI;
    private WindGrid           windGrid;
    private float              gameTime;

    public InGameState(SimpleApplication app) {
        rootNode = app.getRootNode();
        guiNode = app.getGuiNode();
        assetManager = app.getAssetManager();
        // stateManager = app.getStateManager();
        inputManager = app.getInputManager();
        viewPort = app.getViewPort();
        flyBy = app.getFlyByCamera();
        cam = app.getCamera();

    }

    @Override
    public void initialize(AppStateManager stateManager, Application app) {
        super.initialize(stateManager, app);

        // FIRST Action
        playerUI = new PlayerUI(this);

        FilterPostProcessor fpp = new FilterPostProcessor(assetManager);
        BloomFilter bloom = new BloomFilter(BloomFilter.GlowMode.Objects);
        fpp.addFilter(bloom);
        viewPort.addProcessor(fpp);

        localGuiNode = new Node();
        guiNode.attachChild(localGuiNode);

        scenery = new Scenery(this, "Island-e1");

        course = Course.load("eRace-1");
        course.init(this);

        playerBoat = new PlayerBoat(this);
        playerBoat.setPosition(course.getARandomStartPos());
        playerBoat.setCourse(course);
        rootNode.attachChild(playerBoat.getBoat());

        camManager = new CamManager(this, cam, flyBy, playerBoat);

        weather = new Weather(this, "sunny");

        setUpKeys();

        /** Load the HUD */
        BitmapFont guiFont = assetManager.loadFont("Interface/Fonts/Default.fnt");
        displaytext = new BitmapText(guiFont);
        displaytext.setSize(guiFont.getCharSet().getRenderedSize());
        displaytext.move(200, displaytext.getLineHeight() + 20, 0);
        displaytext.setText("");
        localGuiNode.attachChild(displaytext);

        pausetext = new BitmapText(guiFont);
        pausetext.setSize(50f);
        pausetext.move(200, pausetext.getLineHeight() + 200, 0);
        pausetext.setText("");
        localGuiNode.attachChild(pausetext);

        windGrid = new WindGrid(this, rootNode, assetManager);

        gameTime = 0;

        // LAST Action
        playerUI.init(playerBoat);
    }

    @Override
    public void update(float tpf) {
        super.update(tpf);
        gameTime += tpf;

        weather.update(tpf);
        windGrid.update(tpf);
        playerBoat.update(tpf);
        camManager.update(tpf);
        playerUI.update(tpf);

        Buoy nextBuoy = playerBoat.getNextBuoy();
        if (nextBuoy != null) {
            Vector3f buoyPos = nextBuoy.getPos();
            Vector3f toNextBuoy = buoyPos.subtract(playerBoat.getPos());
            Vector3f toNextBuoyNorm = toNextBuoy.normalize();
            float toBuoySpeed = playerBoat.getBoatSpeed().dot(toNextBuoyNorm);
            Vector3f boatSpeedNorm = playerBoat.getBoatSpeed().normalize();
            float buoyAngle = FastMath.RAD_TO_DEG * Utils.angleToMinusPiPi((FastMath.atan2(toNextBuoyNorm.z, toNextBuoyNorm.x) - FastMath.atan2(boatSpeedNorm.z, boatSpeedNorm.x)));

            display("Speed " + playerBoat.getCurSpeed() + "kts Heading " + (int) (RAD_TO_DEG * Utils.angleToZero2Pi(playerBoat.getHeading())) + " rel wind angle " + (int) (RAD_TO_DEG * playerBoat.getRelWindAspect()) + "  rel wind "
                    + (int) playerBoat.getRelWindSpeed() + " Next Buoy " + (int) toNextBuoy.length() + "m at " + (int) buoyAngle + " spd " + (FastMath.floor(toBuoySpeed * 10f) / 10f) + "kts");
        } else {
            display("Speed " + playerBoat.getCurSpeed() + " Heading " + (int) (RAD_TO_DEG * Utils.angleToZero2Pi(playerBoat.getHeading())) + " rel wind angle " + (int) (RAD_TO_DEG * playerBoat.getRelWindAspect()) + "  rel wind "
                    + (int) playerBoat.getRelWindSpeed());
        }

    }

    public boolean isWaterOk(Vector3f position, float margin) {
        float h = scenery.getTerrain().getHeight(new Vector2f(position.x, position.z));
        if (Float.isNaN(h)) {
            return true;
        }
        return h < (30f - margin);
    }

    public float getTerrainHeight(Vector3f position) {
        float height = scenery.getTerrain().getHeight(new Vector2f(position.x, position.z)) - 30f;
        if (Float.isNaN(height) || (height < 0f)) {
            height = 0f;
        }
        return height;
    }

    /**
     * We over-write some navigation key mappings here
     */
    private void setUpKeys() {
        playerUI.registerKey("Pause", KeyInput.KEY_P, this);
    }

    @Override
    public void onEvent(String name, Object eventData) {
        if (eventData instanceof KeyboardInput) {
            KeyboardInput input = (KeyboardInput) eventData;
            if ("Pause".equals(name) && !input.keyPressed) {
                isRunning = !isRunning;
                if (isRunning) {
                    pausetext.setText("");
                } else {
                    pausetext.setText("PAUSED");
                }
            }
        }

    }

    /**
     * @return the assetManager
     */
    public AssetManager getAssetManager() {
        return assetManager;
    }

    /**
     * @return the isRunning
     */
    public boolean isRunning() {
        return isRunning;
    }

    /**
     * @param isRunning
     *            the isRunning to set
     */
    public void setRunning(boolean isRunning) {
        this.isRunning = isRunning;
    }

    public void display(String message) {
        displaytext.setText(message);
    }

    public Node getRootNode() {
        return rootNode;
    }

    public ViewPort getViewPort() {
        return viewPort;
    }

    /**
     * @return the weather
     */
    public Weather getWeather() {
        return weather;
    }

    /**
     * @return the playerBoat
     */
    public Boat getPlayerBoat() {
        return playerBoat;
    }

    /**
     * @return the inputManager
     */
    public InputManager getInputManager() {
        return inputManager;
    }

    /**
     * @return the playerUI
     */
    public PlayerUI getPlayerUI() {
        return playerUI;
    }

    /**
     * @return the gameTime
     */
    public float getGameTime() {
        return gameTime;
    }

}
