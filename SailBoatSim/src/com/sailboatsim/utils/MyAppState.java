/**
 * 
 */
package com.sailboatsim.utils;

import com.jme3.app.Application;
import com.jme3.app.SimpleApplication;
import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppStateManager;
import com.jme3.asset.AssetManager;
import com.jme3.input.InputManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.Node;

public class MyAppState extends AbstractAppState {
    protected SimpleApplication app;
    protected Node              rootNode;
    protected AssetManager      assetManager;
    protected AppStateManager   stateManager;
    protected InputManager      inputManager;
    protected ViewPort          viewPort;

    @Override
    public void initialize(AppStateManager stateManager, Application app) {
        super.initialize(stateManager, app);
        this.app = (SimpleApplication) app; // can cast Application to something more specific
        rootNode = this.app.getRootNode();
        assetManager = this.app.getAssetManager();
        this.stateManager = this.app.getStateManager();
        inputManager = this.app.getInputManager();
        viewPort = this.app.getViewPort();
    }
}
