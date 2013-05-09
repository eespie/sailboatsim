/**
 * 
 */
package com.sailboatsim.client;

import static com.jme3.math.FastMath.RAD_TO_DEG;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.Callable;

import com.jme3.app.Application;
import com.jme3.app.SimpleApplication;
import com.jme3.app.state.AppStateManager;
import com.jme3.input.KeyInput;
import com.jme3.math.FastMath;
import com.jme3.math.Vector3f;
import com.jme3.network.Client;
import com.jme3.network.Message;
import com.jme3.network.MessageListener;
import com.jme3.scene.Node;
import com.sailboatsim.game.InGameState;
import com.sailboatsim.game.boat.Boat;
import com.sailboatsim.game.boat.DefaultBoat;
import com.sailboatsim.game.course.Buoy;
import com.sailboatsim.player.CamManager;
import com.sailboatsim.player.PlayerBoat;
import com.sailboatsim.player.PlayerUI;
import com.sailboatsim.player.WindGrid;
import com.sailboatsim.utils.KeyboardInput;
import com.sailboatsim.utils.SBSNetwork.PosMessage;
import com.sailboatsim.utils.SBSNetwork.ServiceMessage;
import com.sailboatsim.utils.Utils;

/**
 * @author eric
 * 
 */
public class InGameStateClient extends InGameState {
    private final Client            client;
    private PlayerUI                playerUI;
    private PlayerBoat              playerBoat;
    private CamManager              camManager;
    private WindGrid                windGrid;
    private String                  myName;
    private boolean                 canStart   = false;
    private final Map<String, Boat> otherBoats = new HashMap<String, Boat>();

    /**
     * @param app
     */
    public InGameStateClient(SimpleApplication app, Client client) {
        super(app);
        this.client = client;
        if (client != null) {
            client.addMessageListener(new ClientListener(this), PosMessage.class, ServiceMessage.class);
        }
    }

    @Override
    public void initialize(AppStateManager stateManager, Application app) {
        // FIRST Action
        playerUI = new PlayerUI(this);

        super.initialize(stateManager, app);

        playerBoat = new PlayerBoat(this);
        playerBoat.setPosition(course.getARandomStartPos());
        playerBoat.setCourse(course);
        getRootNode().attachChild(playerBoat.getBoat());

        camManager = new CamManager(this, cam, flyBy, playerBoat);

        windGrid = new WindGrid(this, getRootNode(), getAssetManager());

        // LAST Action
        playerUI.init(playerBoat);

        setUpKeys();
        if (client != null) {
            myName = "Player " + new Random().nextInt(100);
            client.send(new ServiceMessage("CONNECT", myName));
        }
    }

    /**
     * We over-write some navigation key mappings here
     */
    private void setUpKeys() {
        playerUI.registerKey("Pause", KeyInput.KEY_P, this);
        playerUI.registerKey("Quit", KeyInput.KEY_ESCAPE, this);
        playerUI.registerKey("Start", KeyInput.KEY_S, this);

    }

    @Override
    public void onEvent(String name, Object eventData) {
        if (eventData instanceof KeyboardInput) {
            KeyboardInput input = (KeyboardInput) eventData;
            if (canStart && "Start".equals(name) && !input.keyPressed) {
                client.send(new ServiceMessage("START", myName));
                Thread thrClient = new Thread(new ClientThread(this));
                thrClient.start();
            }
        }
    }

    @Override
    public void update(float tpf) {
        super.update(tpf);
        windGrid.update(tpf);
        playerBoat.update(tpf);
        camManager.update(tpf);
        playerUI.update(tpf);
        for (Boat boat : otherBoats.values()) {
            boat.update(tpf);
        }

        Buoy nextBuoy = playerBoat.getNextBuoy();
        if (nextBuoy != null) {
            Vector3f buoyPos = nextBuoy.getPos();
            Vector3f toNextBuoy = buoyPos.subtract(playerBoat.getPos());
            Vector3f toNextBuoyNorm = toNextBuoy.normalize();
            float toBuoySpeed = playerBoat.getBoatSpeed().dot(toNextBuoyNorm);
            Vector3f boatSpeedNorm = playerBoat.getBoatSpeed().normalize();
            float buoyAngle = FastMath.RAD_TO_DEG * Utils.angleToMinusPiPi((FastMath.atan2(toNextBuoyNorm.z, toNextBuoyNorm.x) - FastMath.atan2(boatSpeedNorm.z, boatSpeedNorm.x)));

            display("Speed " + playerBoat.getCurSpeed() + "kts Heading " + (int) (RAD_TO_DEG * Utils.angleToZero2Pi(playerBoat.getHeading())) + " rel wind angle " + (int) (RAD_TO_DEG * playerBoat.getRelWindAspect()) + "  rel wind "
                    + (int) playerBoat.getRelWindSpeed() + " Next Buoy " + (int) toNextBuoy.length() + "m at " + (int) buoyAngle + " spd " + (FastMath.floor(toBuoySpeed * 10f) / 10f) + "kts");
        } else {
            display("Speed " + playerBoat.getCurSpeed() + " Heading " + (int) (RAD_TO_DEG * Utils.angleToZero2Pi(playerBoat.getHeading())) + " rel wind angle " + (int) (RAD_TO_DEG * playerBoat.getRelWindAspect()) + "  rel wind "
                    + (int) playerBoat.getRelWindSpeed());
        }

    }

    /**
     * @return the client
     */
    public Client getClient() {
        return client;
    }

    public class ClientListener implements MessageListener<Client> {
        private final InGameStateClient inGameStateClient;

        /**
         * @param inGameStateClient
         */
        public ClientListener(InGameStateClient inGameStateClient) {
            super();
            this.inGameStateClient = inGameStateClient;
        }

        @Override
        public void messageReceived(Client source, Message message) {
            if (message instanceof PosMessage) {
                PosMessage posMessage = (PosMessage) message;
                if ((myName != null) && myName.equals(posMessage.name)) {
                    playerBoat.setPosition(posMessage.pos);
                } else {
                    Boat boat = otherBoats.get(posMessage.name);
                    if (boat == null) {
                        boat = new DefaultBoat(inGameStateClient);
                        boat.setCourse(course);
                        final Boat aBoat = boat;
                        inGameStateClient.getApp().enqueue(new Callable<Node>() {
                            @Override
                            public Node call() throws Exception {
                                getRootNode().attachChild(aBoat.getBoat());
                                return getRootNode();
                            }
                        });
                        otherBoats.put(posMessage.name, boat);
                    }
                    boat.setPosition(posMessage.pos);
                }
            }
            if (message instanceof ServiceMessage) {
                ServiceMessage svc = (ServiceMessage) message;
                System.out.println("Received Service message " + svc.type);

                if ("CONNECTED".equals(svc.type)) {
                    System.out.println(svc.type + " " + svc.strVal);
                    if (myName.equals(svc.strVal)) {
                        canStart = true;
                    }
                }
            }
        }
    }

    /**
     * @return the playerUI
     */
    public PlayerUI getPlayerUI() {
        return playerUI;
    }

    /**
     * @return the playerBoat
     */
    public DefaultBoat getPlayerBoat() {
        return playerBoat;
    }

}
