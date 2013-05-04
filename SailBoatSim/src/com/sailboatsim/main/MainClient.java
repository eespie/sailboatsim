package com.sailboatsim.main;

import java.io.IOException;
import java.util.prefs.BackingStoreException;

import com.jme3.app.SimpleApplication;
import com.jme3.network.Client;
import com.jme3.network.Network;
import com.jme3.system.AppSettings;
import com.jme3.system.JmeSystem;
import com.sailboatsim.game.InGameStateClient;
import com.sailboatsim.utils.SBSNetwork;

/**
 *
 */
public class MainClient extends SimpleApplication {

    private Client client;

    public static void main(String[] args) {
        if (System.getProperty("javawebstart.version") != null) {
            JmeSystem.setLowPermissions(true);
        }
        AppSettings settings = new AppSettings(true);
        // settings.setResolution(1280, 720);
        // settings.setBitsPerPixel(32);
        // settings.setTitle("Sail DefaultBoat Sim");
        try {
            settings.load("com.eboreal.sailboatsim");
            settings.save("com.eboreal.sailboatsim");
        } catch (BackingStoreException e1) {
        }

        SBSNetwork.networkInitilizer();

        MainClient app = new MainClient();
        app.setShowSettings(false);
        app.setSettings(settings);
        app.start();
    }

    @Override
    public void simpleInitApp() {
        try {
            client = Network.connectToServer("127.0.0.1", 19664);
            client.start();
        } catch (IOException e) {
            e.printStackTrace();
        }

        InGameStateClient inGameState = new InGameStateClient(this, client);
        stateManager.attach(inGameState);
        pauseOnFocus = false;
    }

    @Override
    public void destroy() {
        if (client != null) {
            client.close();
        }
        super.destroy();
    }
}
