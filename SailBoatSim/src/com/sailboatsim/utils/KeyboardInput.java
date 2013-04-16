package com.sailboatsim.utils;

public class KeyboardInput {
    public String  name;
    public boolean keyPressed;
    public float   tpf;

    public KeyboardInput(String name, boolean keyPressed, float tpf) {
        this.name = name;
        this.keyPressed = keyPressed;
        this.tpf = tpf;
    }
}
