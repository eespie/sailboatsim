/**
 * 
 */
package com.sailboatsim.utils;

import com.jme3.math.FastMath;

/**
 * @author eric
 * 
 */
public class Utils {

    /**
     * Constraints an angle between -PI and PI
     * 
     * @param angle
     *            angle to convert
     * @return the corresponding angle between -PI and PI
     */
    public final static float angleToMinusPiPi(float angle) {
        while (angle > FastMath.PI) {
            angle -= FastMath.TWO_PI;
        }
        while (angle < -FastMath.PI) {
            angle += FastMath.TWO_PI;
        }
        return angle;
    }

    /**
     * Constraints an angle between 0 and 2PI
     * 
     * @param angle
     *            angle to convert
     * @return the corresponding angle between -PI and PI
     */
    public final static float angleToZero2Pi(float angle) {
        while (angle > FastMath.TWO_PI) {
            angle -= FastMath.TWO_PI;
        }
        while (angle < 0) {
            angle += FastMath.TWO_PI;
        }
        return angle;
    }

    public static String getDirPath(String section) {
        return "assets/Data/" + section + "/";
    }

    public static String getFilePath(String section, String name) {
        return getDirPath(section) + name + ".xml";
    }

}
