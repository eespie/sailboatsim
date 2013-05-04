/**
 * 
 */
package com.sailboatsim.game.boat;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import com.jme3.math.FastMath;
import com.jme3.math.Vector3f;
import com.sailboatsim.utils.Conf;

/**
 * @author eric
 * 
 */
public class DefaultBoatData {

    // Speed data: wind speed -> (angle, boat speed)
    protected Float[][]  speedData;
    protected String     boatModel;
    protected Vector3f[] sailAxis;
    protected Vector3f   modelLocalTranslation;
    protected float      yawInertia;

    private DefaultBoatData() {
    }

    /*
     * raw format: 
     * 6 kt
     * 43.8° 3.46 5.34 5.71 6.01 5.95 5.69 5.46 4.81 4.01 3.48
     * 142.7°
     * 
     * angles are 52,60,75,90,110,120,135,150
     */
    private static final Integer[] RAW_ANGLES = { 0, 0, 52, 60, 75, 90, 110, 120, 135, 150 };

    public void convertRaw() throws IOException {
        Map<Integer, Map<Integer, Float>> data = new TreeMap<Integer, Map<Integer, Float>>();
        Map<Integer, Float> speedByAngle = new TreeMap<Integer, Float>();
        speedByAngle.put(-1, 0F);
        speedByAngle.put(180, 0F);
        data.put(-1, speedByAngle);
        speedByAngle = new TreeMap<Integer, Float>();
        speedByAngle.put(-1, 0F);
        speedByAngle.put(180, 0F);
        data.put(0, speedByAngle);

        FileReader fr = new FileReader("assets/Data/Boat/First_raw.txt");
        BufferedReader br = new BufferedReader(fr);
        String line;
        List<String> lines = new ArrayList<String>();
        while ((line = br.readLine()) != null) {
            lines.add(line);
            if (lines.size() == 3) {
                speedByAngle = new TreeMap<Integer, Float>();
                speedByAngle.put(-1, 0F);
                speedByAngle.put(30, 0F);

                // Wind Speed
                Integer windSpeed = Integer.valueOf(lines.get(0).split(" ")[0]);
                data.put(windSpeed, speedByAngle);

                String[] values = lines.get(1).split(" ");
                Integer angle = (int) FastMath.floor(Float.valueOf(values[0].replace("�", "")));
                Float value = Float.valueOf(values[1]) / FastMath.cos(angle * FastMath.DEG_TO_RAD);
                speedByAngle.put(angle, value);
                for (int index = 2; index < 10; index++) {
                    angle = RAW_ANGLES[index];
                    value = Float.valueOf(values[index]);
                    speedByAngle.put(angle, value);
                    System.out.print(angle + ":" + value + " ");
                }

                angle = (int) FastMath.floor(Float.valueOf(lines.get(2).replace("�", "")));
                value = Float.valueOf(values[10]) / -FastMath.cos(angle * FastMath.DEG_TO_RAD);
                speedByAngle.put(angle, value);
                System.out.print(angle + ":" + value + " ");

                speedByAngle.put(180, Float.valueOf(values[10]) * -FastMath.cos(angle * FastMath.DEG_TO_RAD));
                System.out.println(180 + ":" + (Float.valueOf(values[10]) * -FastMath.cos(angle * FastMath.DEG_TO_RAD)) + " ");

                lines.clear();
            }
        }
        fr.close();
        System.out.println("");
        System.out.println("");

        speedData = new Float[21][181];

        // First convert the angles to 1 degree step
        for (Integer rawWS : data.keySet()) {
            speedByAngle = data.get(rawWS);
            Integer prevRawWA = null;
            List<Integer> keys = new ArrayList<Integer>(speedByAngle.keySet());
            for (Integer rawWA : keys) {
                if (prevRawWA != null) {
                    float deltaWA = rawWA - prevRawWA;
                    float spd1 = speedByAngle.get(prevRawWA);
                    float spd2 = speedByAngle.get(rawWA);
                    for (int wa = (prevRawWA + 1); wa < rawWA; wa++) {
                        float ka = (wa - prevRawWA) / deltaWA;
                        float currS = (spd1 * (1F - ka)) + (spd2 * ka);
                        speedByAngle.put(wa, currS);
                        System.out.print(wa + ":" + currS + " ");
                    }
                }
                prevRawWA = rawWA;
            }
            System.out.println("");
        }

        // now convert the wind speed to 1kt step
        Map<Integer, Float> prevSpeedByAngle = null;
        Integer prevRawWS = null;
        for (Integer rawWS : data.keySet()) {
            speedByAngle = data.get(rawWS);
            if (prevRawWS != null) {
                float deltaWS = rawWS - prevRawWS;
                for (int ws = (prevRawWS + 1); ws < (rawWS + 1); ws++) {
                    float ks = (ws - prevRawWS) / deltaWS;
                    for (int wa = 0; wa < 181; wa++) {
                        speedData[ws][wa] = (prevSpeedByAngle.get(wa) * (1F - ks)) + (speedByAngle.get(wa) * ks);
                    }
                    System.out.println("");
                }
            }
            prevSpeedByAngle = speedByAngle;
            prevRawWS = rawWS;
        }

        Conf<DefaultBoatData> conf = new Conf<DefaultBoatData>();
        conf.save(this, "DefaultBoat", "First");
    }

    private void init() {
        boatModel = "Models/boat/boat.j3o";
        sailAxis = new Vector3f[2];
        sailAxis[0] = new Vector3f(0f, 3.82f, -1.7294f).normalize();
        sailAxis[1] = new Vector3f(0f, 6f, -0.3265f).normalize();
        modelLocalTranslation = new Vector3f(0, 0.3f, 0);
        yawInertia = 0.005f;
    }

    public static DefaultBoatData load(String boatName) {
        return new Conf<DefaultBoatData>().load("Boat", boatName);
    }

    public static void main(String[] args) throws IOException {
        DefaultBoatData polarData = new DefaultBoatData();
        polarData.init();
        polarData.convertRaw();
    }

}
