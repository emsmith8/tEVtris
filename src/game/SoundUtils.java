package game;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;

/**
 * Project: TetrisCloneAttempt
 * Author: Evan Smith
 * Date: 4/8/18
 * Purpose: Provide song generation for Tetris song
 */

class SoundUtils {

    private static double sS = 1;

    private static boolean isPlaying = false;

    private static int[] noteList = {1318, 988, 1046, 1175, 1046, 988, 880, 880, 1046,
        1318, 1175, 1046, 988, 988, 1046, 1175, 1318, 1046, 880,
        880, 1175, 1175, 1397, 1760, 1568, 1397, 1318, 1046, 1318,
        1175, 1046, 988, 988, 1046, 1175, 1318, 1046, 880, 880};

    private static int[] noteLength = {500, 250, 250, 500, 250, 250, 500, 250, 250, 500, 250, 250,
        500, 250, 250, 500, 500, 500, 500, 1000, 500, 250, 250,
        500, 250, 250, 500, 500, 500, 250, 250, 500, 250, 250,
        500, 500, 500, 500, 1000};

    private static int note = 0;

    private static void tone(int hz, double msecs)
        throws LineUnavailableException {
        byte[] buf = new byte[1];
        float SAMPLE_RATE = 8000f;
        AudioFormat af =
            new AudioFormat(
                SAMPLE_RATE,      // sampleRate
                8, // sampleSizeInBits
                1,       // channels
                true,      // signed
                false);  // bigEndian
        SourceDataLine sdl = AudioSystem.getSourceDataLine(af);
        sdl.open(af);
        sdl.start();
        for (int i=0; i < msecs*8; i++) {
            double angle = i / (SAMPLE_RATE / hz) * 2.0 * Math.PI;
            buf[0] = (byte)(Math.sin(angle) * 127.0);
            sdl.write(buf,0,1);
        }
        sdl.drain();
        sdl.stop();
        sdl.close();
    }

    static void setSongSpeed() {
        sS -= .1;
    }

    static void resetSongSpeed() {
        sS = 1;
    }

    static void resetStartNote() {
        note = 0;
    }

    static void stopPlaying() {
        isPlaying = false;
    }

    static void startPlaying() {
        isPlaying = true;
    }

    static boolean getPlaying() {
        return isPlaying;
    }

    static void playSong() {
        while (isPlaying) {
            try {
                if (note == noteList.length-1 || note == noteList.length) {
                    note = 0;
                }
                for (int i = note; i < noteList.length; i++) {
                    if (!isPlaying) return;
                    SoundUtils.tone(noteList[i], noteLength[i]*sS);
                    note = i + 1;
                }
                Thread.sleep(200);
            } catch (InterruptedException | LineUnavailableException e) {
                //
            }
            }


    }

}//end class SoundUtils
