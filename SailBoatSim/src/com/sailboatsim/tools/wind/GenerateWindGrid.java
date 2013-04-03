/**
 * 
 */
package com.sailboatsim.tools.wind;

import java.util.prefs.BackingStoreException;

import com.jme3.app.SimpleApplication;
import com.jme3.system.AppSettings;
import com.jme3.system.JmeSystem;

/**
 * @author eric
 * 
 */
public class GenerateWindGrid extends SimpleApplication {

    public static void main(String[] args) {
        if (System.getProperty("javawebstart.version") != null) {
            JmeSystem.setLowPermissions(true);
        }
        AppSettings settings = new AppSettings(true);
        // settings.setResolution(1280, 720);
        // settings.setBitsPerPixel(32);
        // settings.setTitle("Sail Boat Sim");
        try {
            settings.load("com.eboreal.sailboatsim");
            settings.save("com.eboreal.sailboatsim");
        } catch (BackingStoreException e1) {
        }
        GenerateWindGrid app = new GenerateWindGrid();
        app.setShowSettings(false);
        app.setSettings(settings);
        app.start();
    }

    @Override
    public void simpleInitApp() {
        GeneWindState geneWindState = new GeneWindState(this);
        stateManager.attach(geneWindState);
    }
}