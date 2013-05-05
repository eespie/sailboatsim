/**
 * 
 */
package com.sailboatsim.game;

import com.jme3.app.SimpleApplication;
import com.jme3.app.state.AbstractAppState;
import com.jme3.asset.AssetManager;
import com.jme3.network.HostedConnection;
import com.jme3.network.Message;
import com.jme3.network.MessageListener;
import com.jme3.network.Server;
import com.jme3.scene.Node;
import com.sailboatsim.utils.SBSNetwork.KeyMessage;
import com.sailboatsim.utils.SBSNetwork.ServiceMessage;

/**
 * @author eric
 * 
 */
public class InGameStateServer extends AbstractAppState {
    private final Server       server;
    private final Node         rootNode;
    private final Node         guiNode;
    private final AssetManager assetManager;

    /**
     * @param app
     */
    public InGameStateServer(SimpleApplication app, Server server) {
        rootNode = app.getRootNode();
        guiNode = app.getGuiNode();
        assetManager = app.getAssetManager();

        this.server = server;
        server.addMessageListener(new ServerListener(), KeyMessage.class, ServiceMessage.class);
    }

    /**
     * @return the server
     */
    public Server getServer() {
        return server;
    }

    public class ServerListener implements MessageListener<HostedConnection> {
        @Override
        public void messageReceived(HostedConnection source, Message message) {
            if (message instanceof KeyMessage) {

            }
            if (message instanceof ServiceMessage) {
                ServiceMessage svc = (ServiceMessage) message;
                System.out.println("Received Service message " + svc.type);
                if ("Connect".equals(svc.type)) {
                    source.setAttribute("name", svc.strVal);

                    source.send(new ServiceMessage("Connection Error", "Server Full"));
                }
            }
        }
    }

    /**
     * @return the rootNode
     */
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
    public AssetManager getAssetManager() {
        return assetManager;
    }
}
