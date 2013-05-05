package com.sailboatsim.game;

import com.jme3.asset.AssetManager;
import com.jme3.input.InputManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.Node;
import com.sailboatsim.game.environment.Scenery;
import com.sailboatsim.game.environment.Weather;

public interface GameState {

    /**
     * @return the assetManager
     */
    public abstract AssetManager getAssetManager();

    /**
     * @return the isRunning
     */
    public abstract boolean isRunning();

    public abstract Node getRootNode();

    public abstract ViewPort getViewPort();

    /**
     * @return the weather
     */
    public abstract Weather getWeather();

    /**
     * @return the inputManager
     */
    public abstract InputManager getInputManager();

    public abstract Scenery getScenery();

}