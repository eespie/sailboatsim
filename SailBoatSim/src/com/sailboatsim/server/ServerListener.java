package com.sailboatsim.server;

import com.jme3.network.HostedConnection;
import com.jme3.network.Message;
import com.jme3.network.MessageListener;
import com.sailboatsim.game.boat.Boat;
import com.sailboatsim.game.boat.DefaultBoat;
import com.sailboatsim.utils.SBSNetwork.KeyMessage;
import com.sailboatsim.utils.SBSNetwork.ServiceMessage;

public class ServerListener implements MessageListener<HostedConnection> {

    private final ServerState serverState;

    public ServerListener(ServerState serverState) {
        this.serverState = serverState;
    }

    @Override
    public void messageReceived(HostedConnection source, Message message) {
        if (message instanceof KeyMessage) {

            KeyMessage keyMsg = (KeyMessage) message;
            Boat boat = serverState.getBoats().get(source.getAttribute("name"));
            boat.setLeft(keyMsg.left);
            boat.setRight(keyMsg.right);

        } else if (message instanceof ServiceMessage) {

            ServiceMessage svc = (ServiceMessage) message;
            System.out.println("Received Service message " + svc.type);

            if ("CONNECT".equals(svc.type)) {
                source.setAttribute("name", svc.strVal);
                source.send(new ServiceMessage("CONNECTED", svc.strVal));
            } else if ("START".equals(svc.type)) {
                Boat boat = new DefaultBoat(serverState);
                serverState.getBoats().put(svc.strVal, boat);
                boat.setPosition(serverState.getCourse().getARandomStartPos());
                boat.setCourse(serverState.getCourse());
                serverState.setRunning(true);
                Thread thr = new Thread(new ServerThread(serverState));
                thr.start();
            }
        }
    }
}