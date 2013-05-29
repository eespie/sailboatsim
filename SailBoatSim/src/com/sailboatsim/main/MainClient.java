package com.sailboatsim.main;

import java.io.IOException;
import java.util.prefs.BackingStoreException;

import com.jme3.app.SimpleApplication;
import com.jme3.network.Client;
import com.jme3.network.Network;
import com.jme3.system.AppSettings;
import com.sailboatsim.client.InGameStateClient;
import com.sailboatsim.utils.SBSNetwork;

/**
 *
 */
public class MainClient extends SimpleApplication {

    private Client            client;
    private InGameStateClient inGameState;

    public static void main(String[] args) {
        AppSettings settings = new AppSettings(true);

        try {
            settings.load("com.eboreal.sailboatsim");
        } catch (BackingStoreException e1) {
            settings.setResolution(1024, 768);
            settings.setBitsPerPixel(32);
            settings.setTitle("Sail Boat Sim");
        }

        SBSNetwork.networkInitilizer();

        MainClient app = new MainClient();
        app.setShowSettings(true);
        app.setSettings(settings);
        // app.setDisplayStatView(false);
        app.start();
    }

    @Override
    public void simpleInitApp() {
        try {
            settings.save("com.eboreal.sailboatsim");
        } catch (BackingStoreException e) {
        }
        startClientConnection("192.168.0.11");

        inGameState = new InGameStateClient(this, client);
        stateManager.attach(inGameState);
        pauseOnFocus = false;
    }

    private void startClientConnection(String addr) {
        try {
            client = Network.connectToServer(addr, 19664);
            client.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void destroy() {
        if (client != null) {
            client.close();
        }
        inGameState.setRunning(false);
        super.destroy();
    }
}
