package com.sailboatsim.main;

import java.io.IOException;
import java.util.prefs.BackingStoreException;

import com.jme3.app.SimpleApplication;
import com.jme3.network.Network;
import com.jme3.network.Server;
import com.jme3.system.AppSettings;
import com.jme3.system.JmeSystem;
import com.sailboatsim.game.InGameState;
import com.sailboatsim.utils.SBSNetwork;

/**
 *
 */
public class MainServer extends SimpleApplication {

    private Server server;

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

        SBSNetwork.networkInitilizer();

        MainServer app = new MainServer();
        app.setShowSettings(false);
        app.setSettings(settings);
        app.start();
    }

    @Override
    public void simpleInitApp() {
        try {
            server = Network.createServer(19664);
            server.start();

            InGameState inGameState = new InGameState(this);
            stateManager.attach(inGameState);
            pauseOnFocus = false;

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void destroy() {
        server.close();
        super.destroy();
    }
}
