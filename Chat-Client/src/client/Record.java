package client;

import client.display.ChatSceneController;
import logging.SetupLogger;
import sun.rmi.runtime.Log;

import javax.sound.sampled.*;
import javax.sound.sampled.AudioFileFormat.Type;
import javax.sound.sampled.DataLine.Info;
import java.io.*;
import java.lang.annotation.Target;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by Daric on 11/27/2016.
 *
 * sender class/object? that accepts a record object
 *
 */
public class Record {

    private static Logger LOGGER = SetupLogger.startLogger(Record.class.getName());
    private Type type;
    private File wavFile;
    private TargetDataLine mic;
    private AudioFormat format;
    private Info info;
    private boolean isGoodState;
    private AudioInputStream audioInStream;

    public Record() {
        type = AudioFileFormat.Type.WAVE;
        wavFile = new File("RecentAudio.wav");
        format = new AudioFormat(8000.0f, 16, 1, true, true);
        try {
            if (!wavFile.exists()) {
                wavFile.createNewFile();
            }
            isGoodState = true;
        }
        catch(IOException ioe){
            LOGGER.log(Level.SEVERE, ioe.getMessage(), ioe);
            isGoodState = false;
            return;
        }

    }

    public boolean state(){
        return this.isGoodState;
    }

    public void startRec(Object asker){ //TODO make button stop rec or time
        try {

            Thread stopper = new Thread(new Runnable() {
                public void run() {
                    try {
                        Thread.sleep(5000);//change time if needed
                        stopRec(asker);
                    } catch (InterruptedException ie) {
                        Thread.currentThread().interrupt();
                        LOGGER.log(Level.SEVERE, ie.getMessage(), ie);
                        ((ChatSceneController)asker).notifyClient("Connection Interrupted, may not have gotten all audio ");
                        isGoodState = false;
                        stopRec(asker);
                    }
                }
            });

            mic = AudioSystem.getTargetDataLine(format);
            mic.open(format);
            mic.start();

            stopper.start();//TODO test if these lines should be flipped
            audioInStream = new AudioInputStream(mic);

            AudioSystem.write(audioInStream, type, wavFile);    //make output streams to everyone for live audio??
            ((ChatSceneController)asker).notifyClient("Recording Successful");
        }
        catch(LineUnavailableException lue){
            format = new AudioFormat(8000.0f, 16, 1, true, true);
            LOGGER.log(Level.SEVERE, lue.getMessage(), lue);
            ((ChatSceneController)asker).notifyClient("Unable to record");
            isGoodState = false;
        }
        catch(IOException ioe){
            LOGGER.log(Level.SEVERE, ioe.getMessage(), ioe);
            ((ChatSceneController)asker).notifyClient("The audio was not saved correctly/lost");
            isGoodState = false;
        }
    }

    public void stopRec(Object asker){
        mic.stop();
        mic.flush();
        mic.close();
        try{
            audioInStream.close();
        }
        catch(IOException ioe){
            LOGGER.log(Level.SEVERE, ioe.getMessage(), ioe);
            //TODO dont know if this matters
        }
    }
}
