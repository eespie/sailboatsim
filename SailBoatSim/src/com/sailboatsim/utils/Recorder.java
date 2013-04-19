/**
 * 
 */
package com.sailboatsim.utils;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Map;
import java.util.TreeMap;

import org.msgpack.MessagePack;

/**
 * @author eric
 * 
 */
public class Recorder<T> {

    Map<Float, T>          records = new TreeMap<Float, T>();
    private BufferedWriter outBuffer;
    private MessagePack    msgPack;

    /**
     * @throws IOException
     * 
     */
    public Recorder(String filename) {
        FileWriter fstream;
        try {
            fstream = new FileWriter(filename);
            outBuffer = new BufferedWriter(fstream);
            msgPack = new MessagePack();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public void recording(float gameTime, T data) {
        records.put(gameTime, data);
        //outBuffer.write(msgPack.write(gameTime));
    }

    public T getRecord(float gameTime) {
        return records.get(gameTime);
    }

}
