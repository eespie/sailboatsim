/**
 * 
 */
package com.sailboatsim.game.environment;

import com.jme3.math.FastMath;
import com.jme3.math.Vector3f;
import com.sailboatsim.utils.Conf;

/**
 * @author eric
 * 
 */
public class DefaultWeatherData {

    protected Vector3f lightDir;
    protected float    initialWaterHeight;
    protected String   textureSky;
    protected String   textureWaterFoam;
    protected float    globalWindSpeed;
    protected float    globalWindDirection;

    private DefaultWeatherData() {
        // forbidden
    }

    public static DefaultWeatherData load(String weather) {
        return new Conf<DefaultWeatherData>().load("Weather", weather);
    }

    public static void main(String[] args) {
        // Create sunny weather
        DefaultWeatherData weather = new DefaultWeatherData();
        weather.lightDir = new Vector3f(-4.9f, -1.3f, 5.9f);
        weather.initialWaterHeight = 0.0f;
        weather.textureWaterFoam = "Common/MatDefs/Water/Textures/foam2.jpg";
        weather.textureSky = "Scenes/Beach/FullskiesSunset0068.dds";
        weather.globalWindSpeed = 20.0f;
        weather.globalWindDirection = FastMath.PI; // from north to south
        new Conf<DefaultWeatherData>().save(weather, "Weather", "sunny");
    }

}
