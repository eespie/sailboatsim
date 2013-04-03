/**
 * 
 */
package com.sailboatsim.tools.wind2;

import java.util.prefs.BackingStoreException;

import com.jme3.app.SimpleApplication;
import com.jme3.system.AppSettings;
import com.jme3.system.JmeSystem;

/**
 * @author eric
 * 
 */
public class GenerateWind2Grid extends SimpleApplication {

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
        GenerateWind2Grid app = new GenerateWind2Grid();
        app.setShowSettings(false);
        app.setSettings(settings);
        app.start();
    }

    @Override
    public void simpleInitApp() {
        GeneWind2State geneWindState = new GeneWind2State(this);
        stateManager.attach(geneWindState);
    }
}