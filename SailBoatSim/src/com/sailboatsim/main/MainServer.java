package com.sailboatsim.main;

import java.io.IOException;

import com.jme3.app.SimpleApplication;
import com.jme3.network.Network;
import com.jme3.network.Server;
import com.jme3.system.AppSettings;
import com.jme3.system.JmeContext;
import com.jme3.system.JmeSystem;
import com.sailboatsim.server.ServerState;
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
        settings.setResolution(640, 480);
        settings.setTitle("SailBoatSim Server");

        SBSNetwork.networkInitilizer();

        MainServer app = new MainServer();
        app.setShowSettings(false);
        app.setSettings(settings);
        app.start(JmeContext.Type.Headless);
    }

    @Override
    public void simpleInitApp() {
        try {
            server = Network.createServer(19664);
            server.start();

            ServerState inGameState = new ServerState(this, server);
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
