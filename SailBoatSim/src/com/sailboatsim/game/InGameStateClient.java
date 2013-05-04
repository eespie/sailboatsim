/**
 * 
 */
package com.sailboatsim.game;

import com.jme3.app.SimpleApplication;
import com.jme3.network.Client;

/**
 * @author eric
 * 
 */
public class InGameStateClient extends InGameState {
    Client client;

    /**
     * @param app
     */
    public InGameStateClient(SimpleApplication app, Client client) {
        super(app);
        this.client = client;
    }

    @Override
    public void update(float tpf) {
        super.update(tpf);
    }
}
