package com.sailboatsim.main;

import java.util.prefs.BackingStoreException;

import com.jme3.app.SimpleApplication;
import com.jme3.bullet.BulletAppState;
import com.jme3.system.AppSettings;
import com.jme3.system.JmeSystem;
import com.sailboatsim.game.InGameState;

/**
 *
 */
public class Main extends SimpleApplication {

    public static void main(String[] args) {
        if (System.getProperty("javawebstart.version") != null) {
            JmeSystem.setLowPermissions(true);
        }
        AppSettings settings = new AppSettings(true);
        // settings.setResolution(1280, 720);
        // settings.setBitsPerPixel(32);
        // settings.setTitle("Sail Boat Sim");
        try {
            settings.load("com.eboreal.sailboatsim");
            settings.save("com.eboreal.sailboatsim");
        } catch (BackingStoreException e1) {
        }
        Main app = new Main();
        app.setShowSettings(false);
        app.setSettings(settings);
        app.start();
    }

    @Override
    public void simpleInitApp() {
        BulletAppState bulletAppState = new BulletAppState();
        stateManager.attach(bulletAppState);

        InGameState inGameState = new InGameState(this);
        stateManager.attach(inGameState);
    }
}
