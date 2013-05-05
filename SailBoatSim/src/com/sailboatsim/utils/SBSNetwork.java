package com.sailboatsim.utils;

import com.jme3.network.AbstractMessage;
import com.jme3.network.serializing.Serializable;
import com.jme3.network.serializing.Serializer;
import com.sailboatsim.game.boat.BoatPosition;

public class SBSNetwork {

    public static void networkInitilizer() {
        Serializer.registerClass(ServiceMessage.class);
        Serializer.registerClass(KeyMessage.class);
        Serializer.registerClass(PosMessage.class);
        Serializer.registerClass(BoatPosition.class);
    }

    @Serializable
    public static class ServiceMessage extends AbstractMessage {
        public String type;
        public String strVal;
        public int    intVal;
        public float  floatVal;

        public ServiceMessage() {
            setReliable(true);
        }

        /**
         * @param type
         * @param strVal
         */
        public ServiceMessage(String type, String strVal) {
            super();
            this.type = type;
            this.strVal = strVal;
        }

        /**
         * @param type
         * @param intVal
         */
        public ServiceMessage(String type, int intVal) {
            super();
            this.type = type;
            this.intVal = intVal;
        }

        /**
         * @param type
         * @param floatVal
         */
        public ServiceMessage(String type, float floatVal) {
            super();
            this.type = type;
            this.floatVal = floatVal;
        }

    }

    @Serializable
    public static class KeyMessage extends AbstractMessage {
        public float   gameTime;
        public int     sequenceNumber;
        public boolean left;
        public boolean right;

        public KeyMessage() {
            setReliable(false);
        }

        public KeyMessage(int sequenceNumber, float gameTime, boolean left, boolean right) {
            this.sequenceNumber = sequenceNumber;
            this.gameTime = gameTime;
            this.left = left;
            this.right = right;
        }
    }

    @Serializable
    public static class PosMessage extends AbstractMessage {
        public String       name;
        public int          sequenceNumber;
        public BoatPosition pos;

        public PosMessage() {
            setReliable(false);
        }

        /**
         * @param name
         * @param sequenceNumber
         * @param pos
         */
        public PosMessage(String name, int sequenceNumber, BoatPosition pos) {
            super();
            this.name = name;
            this.sequenceNumber = sequenceNumber;
            this.pos = pos;
        }
    }
}
