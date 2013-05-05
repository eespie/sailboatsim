/**
 * 
 */
package com.sailboatsim.game;

import com.jme3.network.Client;
import com.sailboatsim.game.boat.Boat;
import com.sailboatsim.utils.SBSNetwork.KeyMessage;

/**
 * @author eric some code from kgp
 * 
 */
public class ClientThread implements Runnable {
    private final Client            client;
    private final InGameStateClient inGameStateClient;
    private final Boat              playerBoat;

    private static int              NO_DELAY_PER_YIELD = 16;
    private static long             period             = 30 * 1000000;

    /**
     * 
     */
    public ClientThread(InGameStateClient inGameStateClient) {
        this.inGameStateClient = inGameStateClient;
        client = inGameStateClient.getClient();
        playerBoat = inGameStateClient.getPlayerBoat();
    }

    /* (non-Javadoc)
     * @see java.lang.Runnable#run()
     */
    @Override
    public void run() {
        if (client == null) {
            return;
        }
        long afterTime;
        long timeDiff = 0;
        long sleepTime;
        long overSleepTime = 0;
        int noDelays = 0;
        long startTime = System.nanoTime();
        long beforeTime = startTime;
        int sequenceNumber = 0;

        while (inGameStateClient.isRunning() && inGameStateClient.isEnabled()) {
            float gameTime = (beforeTime - startTime) / 1000000000F;
            boolean left = playerBoat.getLeft();
            boolean right = playerBoat.getRight();

            KeyMessage msg = new KeyMessage(sequenceNumber, gameTime, left, right);
            client.send(msg);

            afterTime = System.nanoTime();
            timeDiff = afterTime - beforeTime;
            sleepTime = (period - timeDiff) - overSleepTime;
            if (sleepTime > 0) {
                try {
                    Thread.sleep(sleepTime / 1000000, (int) (sleepTime % 1000000));
                } catch (InterruptedException e) {
                }
                overSleepTime = System.nanoTime() - afterTime - sleepTime;
            } else {
                overSleepTime = 0;
                if (++noDelays >= NO_DELAY_PER_YIELD) {
                    Thread.yield();
                    noDelays = 0;
                }
            }
            beforeTime = System.nanoTime();
            sequenceNumber++;
        }
    }

}
