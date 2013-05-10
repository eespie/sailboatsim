/**
 * 
 */
package com.sailboatsim.server;

import java.util.HashMap;
import java.util.Map;

import com.jme3.app.Application;
import com.jme3.app.SimpleApplication;
import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppStateManager;
import com.jme3.asset.AssetManager;
import com.jme3.input.InputManager;
import com.jme3.network.Server;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.Node;
import com.sailboatsim.game.GameState;
import com.sailboatsim.game.boat.Boat;
import com.sailboatsim.game.course.Course;
import com.sailboatsim.game.course.DefaultCourse;
import com.sailboatsim.game.environment.DefaultScenery;
import com.sailboatsim.game.environment.DefaultWeather;
import com.sailboatsim.game.environment.Scenery;
import com.sailboatsim.game.environment.Weather;
import com.sailboatsim.utils.SBSNetwork.KeyMessage;
import com.sailboatsim.utils.SBSNetwork.ServiceMessage;

/**
 * @author eric
 * 
 */
public class ServerState extends AbstractAppState implements GameState {
    private final Server            server;
    private final Node              rootNode;
    private final Node              guiNode;
    private final AssetManager      assetManager;
    private final ViewPort          viewPort;
    private final InputManager      inputManager;

    private final Map<String, Boat> boats     = new HashMap<String, Boat>();
    private boolean                 isRunning = false;

    private Scenery                 scenery;
    private Weather                 weather;
    private Course                  course;
    private final Application       mainApp;

    /**
     * @param app
     */
    public ServerState(SimpleApplication app, Server server) {
        rootNode = app.getRootNode();
        guiNode = app.getGuiNode();
        assetManager = app.getAssetManager();
        viewPort = app.getViewPort();
        inputManager = app.getInputManager();
        mainApp = app;

        this.server = server;
        server.addMessageListener(new ServerListener(this), KeyMessage.class, ServiceMessage.class);
    }

    @Override
    public void initialize(AppStateManager stateManager, Application app) {
        super.initialize(stateManager, app);
        scenery = new DefaultScenery(this, "Island-e1");
        weather = new DefaultWeather(this, "sunny");
        course = DefaultCourse.load("eRace-1");
        course.init(this);
    }

    /**
     * @return the server
     */
    public Server getServer() {
        return server;
    }

    /**
     * @return the rootNode
     */
    @Override
    public Node getRootNode() {
        return rootNode;
    }

    /**
     * @return the guiNode
     */
    public Node getGuiNode() {
        return guiNode;
    }

    /**
     * @return the assetManager
     */
    @Override
    public AssetManager getAssetManager() {
        return assetManager;
    }

    @Override
    public boolean isRunning() {
        return isRunning;
    }

    @Override
    public ViewPort getViewPort() {
        return viewPort;
    }

    @Override
    public Weather getWeather() {
        return weather;
    }

    @Override
    public InputManager getInputManager() {
        return inputManager;
    }

    @Override
    public Scenery getScenery() {
        return scenery;
    }

    /**
     * @return the boats
     */
    public Map<String, Boat> getBoats() {
        return boats;
    }

    /**
     * @return the course
     */
    public Course getCourse() {
        return course;
    }

    /**
     * @param isRunning
     *            the isRunning to set
     */
    public void setRunning(boolean isRunning) {
        this.isRunning = isRunning;
    }

    @Override
    public Application getApp() {
        return mainApp;
    }
}
