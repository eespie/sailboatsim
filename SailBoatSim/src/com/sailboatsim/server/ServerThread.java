/**
 * 
 */
package com.sailboatsim.server;

import com.jme3.network.Server;
import com.sailboatsim.game.boat.Boat;
import com.sailboatsim.utils.SBSNetwork.PosMessage;

/**
 * @author eric some code from kgp
 * 
 */
public class ServerThread implements Runnable {
    private final Server      server;
    private final ServerState serverState;

    private static int        NO_DELAY_PER_YIELD = 16;

    private final long        period_ns          = 30 * 1000000;

    /**
     * 
     */
    public ServerThread(ServerState serverState) {
        this.serverState = serverState;
        server = serverState.getServer();
    }

    /* (non-Javadoc)
     * @see java.lang.Runnable#run()
     */
    @Override
    public void run() {
        long afterTime;
        long timeDiff = 0;
        long sleepTime;
        long overSleepTime = 0;
        int noDelays = 0;
        long startTime = System.nanoTime();
        long beforeTime = startTime;
        long previousTime = startTime;
        int sequenceNumber = 0;

        while (serverState.isRunning() && serverState.isEnabled()) {
            float tpf = (beforeTime - previousTime) / 1000000000F;

            for (String name : serverState.getBoats().keySet()) {
                Boat boat = serverState.getBoats().get(name);
                boat.update(tpf);
                server.broadcast(new PosMessage(name, sequenceNumber, boat.getPosition()));
            }

            afterTime = System.nanoTime();
            timeDiff = afterTime - beforeTime;
            sleepTime = (period_ns - timeDiff) - overSleepTime;
            if (sleepTime > 0) {
                try {
                    Thread.sleep(sleepTime / 1000000);
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
            previousTime = beforeTime;
            beforeTime = System.nanoTime();
            sequenceNumber++;
        }
    }

}
