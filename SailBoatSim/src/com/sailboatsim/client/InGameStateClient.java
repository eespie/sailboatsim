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
import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppStateManager;
import com.jme3.asset.AssetManager;
import com.jme3.font.BitmapFont;
import com.jme3.font.BitmapText;
import com.jme3.input.FlyByCamera;
import com.jme3.input.InputManager;
import com.jme3.input.KeyInput;
import com.jme3.network.Client;
import com.jme3.network.Message;
import com.jme3.network.MessageListener;
import com.jme3.post.FilterPostProcessor;
import com.jme3.post.filters.BloomFilter;
import com.jme3.renderer.Camera;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.Node;
import com.sailboatsim.game.GameState;
import com.sailboatsim.game.boat.Boat;
import com.sailboatsim.game.boat.DefaultBoat;
import com.sailboatsim.game.course.Buoy;
import com.sailboatsim.game.course.Course;
import com.sailboatsim.game.course.DefaultCourse;
import com.sailboatsim.game.environment.DefaultScenery;
import com.sailboatsim.game.environment.DefaultWeather;
import com.sailboatsim.game.environment.Scenery;
import com.sailboatsim.game.environment.Weather;
import com.sailboatsim.player.CamManager;
import com.sailboatsim.player.PlayerBoat;
import com.sailboatsim.player.PlayerUI;
import com.sailboatsim.player.WindGrid;
import com.sailboatsim.utils.KeyboardInput;
import com.sailboatsim.utils.SBSNetwork.PosMessage;
import com.sailboatsim.utils.SBSNetwork.ServiceMessage;
import com.sailboatsim.utils.SimpleEventListener;
import com.sailboatsim.utils.Utils;

/**
 * @author eric
 * 
 */
public class InGameStateClient extends AbstractAppState implements SimpleEventListener, GameState {
    private final Client            client;
    private PlayerUI                playerUI;
    private PlayerBoat              playerBoat;
    private CamManager              camManager;
    private WindGrid                windGrid;
    private String                  myName;
    private boolean                 canStart   = false;
    private final Map<String, Boat> otherBoats = new HashMap<String, Boat>();
    private final Node              rootNode;
    private final Node              guiNode;
    private final AssetManager      assetManager;
    // private final AppStateManager stateManager;
    private final InputManager      inputManager;
    private final ViewPort          viewPort;
    protected final FlyByCamera     flyBy;
    protected final Camera          cam;
    private Application             app;

    private boolean                 isRunning  = true;
    private Node                    localGuiNode;

    private BitmapText              displaytext;
    private BitmapText              pausetext;
    private Weather                 weather;
    private Scenery                 scenery;
    protected Course                course;
    private float                   gameTime;

    /**
     * @param app
     */
    public InGameStateClient(SimpleApplication app, Client client) {
        rootNode = app.getRootNode();
        guiNode = app.getGuiNode();
        assetManager = app.getAssetManager();
        // stateManager = app.getStateManager();
        inputManager = app.getInputManager();
        viewPort = app.getViewPort();
        flyBy = app.getFlyByCamera();
        cam = app.getCamera();
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

        this.app = app;

        FilterPostProcessor fpp = new FilterPostProcessor(assetManager);
        BloomFilter bloom = new BloomFilter(BloomFilter.GlowMode.Objects);
        fpp.addFilter(bloom);
        viewPort.addProcessor(fpp);

        localGuiNode = new Node();
        guiNode.attachChild(localGuiNode);

        scenery = new DefaultScenery(this, "Island-e1");

        weather = new DefaultWeather(this, "sunny");

        course = DefaultCourse.load("eRace-1");
        rootNode.attachChild(course.init(this));

        /** Load the HUD */
        BitmapFont guiFont = assetManager.loadFont("Interface/Fonts/Default.fnt");
        displaytext = new BitmapText(guiFont);
        displaytext.setSize(guiFont.getCharSet().getRenderedSize());
        displaytext.move(200, displaytext.getLineHeight() + 20, 0);
        displaytext.setText("");
        localGuiNode.attachChild(displaytext);

        pausetext = new BitmapText(guiFont);
        pausetext.setSize(50f);
        pausetext.move(200, pausetext.getLineHeight() + 200, 0);
        pausetext.setText("");
        localGuiNode.attachChild(pausetext);

        gameTime = 0;

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
                playerBoat.setGameTime(0);
            }
        }
    }

    @Override
    public void update(float tpf) {
        super.update(tpf);
        gameTime += tpf;

        weather.update(tpf);
        windGrid.update(tpf);
        playerBoat.update(tpf);
        camManager.update(tpf);
        playerUI.update(tpf);
        for (Boat boat : otherBoats.values()) {
            boat.update(tpf);
        }

        Buoy nextBuoy = playerBoat.getNextBuoy();
        if (nextBuoy != null) {
            //Vector3f buoyPos = nextBuoy.getPos();
            //Vector3f toNextBuoy = buoyPos.subtract(playerBoat.getPos());
            //Vector3f toNextBuoyNorm = toNextBuoy.normalize();
            //float toBuoySpeed = playerBoat.getBoatSpeed().dot(toNextBuoyNorm);
            //Vector3f boatSpeedNorm = playerBoat.getBoatSpeed().normalize();
            //float buoyAngle = FastMath.RAD_TO_DEG * Utils.angleToMinusPiPi((FastMath.atan2(toNextBuoyNorm.z, toNextBuoyNorm.x) - FastMath.atan2(boatSpeedNorm.z, boatSpeedNorm.x)));
            //            display("Speed " + playerBoat.getCurSpeed() + "kts Heading " + (int) (RAD_TO_DEG * Utils.angleToZero2Pi(playerBoat.getHeading())) + " rel wind angle " + (int) (RAD_TO_DEG * playerBoat.getRelWindAspect()) + "  rel wind "
            //                    + (int) playerBoat.getRelWindSpeed() + " Next Buoy " + (int) toNextBuoy.length() + "m at " + (int) buoyAngle + " spd " + (FastMath.floor(toBuoySpeed * 10f) / 10f) + "kts");
            display("Speed " + playerBoat.getCurSpeed() + "kts delta " + (playerBoat.getGameTime() - playerBoat.getPosition().gameTime));
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
            if ((message instanceof PosMessage) && canStart) {
                PosMessage posMessage = (PosMessage) message;
                if ((myName != null) && myName.equals(posMessage.name)) {
                    playerBoat.setPosition(posMessage.pos);
                } else {
                    Boat boat = otherBoats.get(posMessage.name);
                    if (boat == null) {
                        boat = new DefaultBoat(inGameStateClient);
                        Course aCourse = DefaultCourse.load("eRace-1");
                        aCourse.init(inGameStateClient);
                        boat.setCourse(aCourse);
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

    /* (non-Javadoc)
     * @see com.sailboatsim.game.GameState#getAssetManager()
     */
    @Override
    public AssetManager getAssetManager() {
        return assetManager;
    }

    /* (non-Javadoc)
     * @see com.sailboatsim.game.GameState#isRunning()
     */
    @Override
    public boolean isRunning() {
        return isRunning;
    }

    /**
     * @param isRunning
     *            the isRunning to set
     */
    public void setRunning(boolean isRunning) {
        this.isRunning = isRunning;
    }

    public void display(String message) {
        displaytext.setText(message);
    }

    /* (non-Javadoc)
     * @see com.sailboatsim.game.GameState#getRootNode()
     */
    @Override
    public Node getRootNode() {
        return rootNode;
    }

    /* (non-Javadoc)
     * @see com.sailboatsim.game.GameState#getViewPort()
     */
    @Override
    public ViewPort getViewPort() {
        return viewPort;
    }

    /* (non-Javadoc)
     * @see com.sailboatsim.game.GameState#getWeather()
     */
    @Override
    public Weather getWeather() {
        return weather;
    }

    /* (non-Javadoc)
     * @see com.sailboatsim.game.GameState#getInputManager()
     */
    @Override
    public InputManager getInputManager() {
        return inputManager;
    }

    /**
     * @return the gameTime
     */
    public float getGameTime() {
        return gameTime;
    }

    /* (non-Javadoc)
     * @see com.sailboatsim.game.GameState#getScenery()
     */
    @Override
    public Scenery getScenery() {
        return scenery;
    }

    /**
     * @return the app
     */
    @Override
    public Application getApp() {
        return app;
    }

}
