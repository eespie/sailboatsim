/**
 * 
 */
package com.sailboatsim.game;

import static com.jme3.math.FastMath.RAD_TO_DEG;

import java.util.HashMap;
import java.util.Map;

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
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.post.FilterPostProcessor;
import com.jme3.post.filters.BloomFilter;
import com.jme3.renderer.Camera;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.Node;
import com.sailboatsim.game.boat.Boat;
import com.sailboatsim.game.course.Course;
import com.sailboatsim.game.environment.Scenery;
import com.sailboatsim.game.environment.Weather;
import com.sailboatsim.utils.Utils;

/**
 * @author eric
 * 
 */
public class InGameState extends AbstractAppState implements ActionListener {
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

        FilterPostProcessor fpp = new FilterPostProcessor(assetManager);
        BloomFilter bloom = new BloomFilter(BloomFilter.GlowMode.Objects);
        fpp.addFilter(bloom);
        viewPort.addProcessor(fpp);

        localGuiNode = new Node();
        guiNode.attachChild(localGuiNode);

        scenery = new Scenery(this, "Island-e1");

        course = Course.load("eRace-1");
        course.init(this);

        playerBoat = new Boat(this);
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

    }

    @Override
    public void update(float tpf) {
        super.update(tpf);

        weather.update(tpf);
        playerBoat.update(tpf);
        camManager.update(tpf);

        //Buoy nextBuoy = playerBoat.getNextBuoy();

        display("Speed " + playerBoat.getCurSpeed() + " Heading " + (int) (RAD_TO_DEG * Utils.angleToZero2Pi(playerBoat.getHeading())) + " rel wind angle " + (int) (RAD_TO_DEG * playerBoat.getRelWindAspect()) + "  rel wind "
                + (int) playerBoat.getRelWindSpeed() + " Next Buoy ");

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
        // keys
        Map<String, Integer> keys = new HashMap<String, Integer>();
        keys.put("Pause", KeyInput.KEY_P);
        registerKeys(keys, this);
    }

    public void registerKeys(Map<String, Integer> keys, ActionListener listener) {
        for (String binding : keys.keySet()) {
            inputManager.addMapping(binding, new KeyTrigger(keys.get(binding)));
            inputManager.addListener(listener, binding);
        }
    }

    /**
     * These are our custom actions triggered by key presses.
     */
    public void onAction(String name, boolean keyPressed, float tpf) {
        if (name.equals("Pause") && !keyPressed) {
            isRunning = !isRunning;
            if (isRunning) {
                pausetext.setText("");
            } else {
                pausetext.setText("PAUSED");
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

}