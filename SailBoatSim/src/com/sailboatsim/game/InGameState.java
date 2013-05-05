/**
 * 
 */
package com.sailboatsim.game;

import com.jme3.app.Application;
import com.jme3.app.SimpleApplication;
import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppStateManager;
import com.jme3.asset.AssetManager;
import com.jme3.font.BitmapFont;
import com.jme3.font.BitmapText;
import com.jme3.input.FlyByCamera;
import com.jme3.input.InputManager;
import com.jme3.post.FilterPostProcessor;
import com.jme3.post.filters.BloomFilter;
import com.jme3.renderer.Camera;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.Node;
import com.sailboatsim.game.course.Course;
import com.sailboatsim.game.course.DefaultCourse;
import com.sailboatsim.game.environment.DefaultScenery;
import com.sailboatsim.game.environment.DefaultWeather;
import com.sailboatsim.game.environment.Scenery;
import com.sailboatsim.game.environment.Weather;
import com.sailboatsim.utils.KeyboardInput;
import com.sailboatsim.utils.SimpleEventListener;

/**
 * @author eric
 * 
 */
public class InGameState extends AbstractAppState implements SimpleEventListener, GameState {
    private final Node          rootNode;
    private final Node          guiNode;
    private final AssetManager  assetManager;
    // private final AppStateManager stateManager;
    private final InputManager  inputManager;
    private final ViewPort      viewPort;
    protected final FlyByCamera flyBy;
    protected final Camera      cam;

    private boolean             isRunning = true;
    private Node                localGuiNode;

    private BitmapText          displaytext;
    private BitmapText          pausetext;
    private Weather             weather;
    private Scenery             scenery;
    protected Course            course;
    private float               gameTime;

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

        scenery = new DefaultScenery(this, "Island-e1");

        weather = new DefaultWeather(this, "sunny");

        course = DefaultCourse.load("eRace-1");
        course.init(this);

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

        gameTime = 0;
    }

    @Override
    public void update(float tpf) {
        super.update(tpf);
        gameTime += tpf;

        weather.update(tpf);

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

    /* (non-Javadoc)
     * @see com.sailboatsim.game.GameState#getAssetManager()
     */
    @Override
    public AssetManager getAssetManager() {
        return assetManager;
    }

    /* (non-Javadoc)
     * @see com.sailboatsim.game.GameState#isRunning()
     */
    @Override
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

    /* (non-Javadoc)
     * @see com.sailboatsim.game.GameState#getRootNode()
     */
    @Override
    public Node getRootNode() {
        return rootNode;
    }

    /* (non-Javadoc)
     * @see com.sailboatsim.game.GameState#getViewPort()
     */
    @Override
    public ViewPort getViewPort() {
        return viewPort;
    }

    /* (non-Javadoc)
     * @see com.sailboatsim.game.GameState#getWeather()
     */
    @Override
    public Weather getWeather() {
        return weather;
    }

    /* (non-Javadoc)
     * @see com.sailboatsim.game.GameState#getInputManager()
     */
    @Override
    public InputManager getInputManager() {
        return inputManager;
    }

    /**
     * @return the gameTime
     */
    public float getGameTime() {
        return gameTime;
    }

    /* (non-Javadoc)
     * @see com.sailboatsim.game.GameState#getScenery()
     */
    @Override
    public Scenery getScenery() {
        return scenery;
    }

}
