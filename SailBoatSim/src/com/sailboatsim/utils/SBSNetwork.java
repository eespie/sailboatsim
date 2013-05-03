package com.sailboatsim.utils;

import com.jme3.network.AbstractMessage;
import com.jme3.network.serializing.Serializable;
import com.jme3.network.serializing.Serializer;

public class SBSNetwork {

    public static void networkInitilizer() {
        Serializer.registerClass(HelloMessage.class);
    }

    @Serializable
    public static class HelloMessage extends AbstractMessage {
        private String name;

        public HelloMessage() {
        }

        public HelloMessage(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }
    }
}
