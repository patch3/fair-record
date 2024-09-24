package ru.sudrf.fairrecord.fairrecord;

import ru.sudrf.fairrecord.fairrecord.helpers.MixerHelper;

import javax.sound.sampled.*;
import java.io.File;
import java.io.IOException;

public class AudioRecorder {
    public static final int BUFFER_SIZE = 4096;
    private AudioInputStream audioInputStream;
    private File outputFile;
    private TargetDataLine targetDataLine;
    private Mixer deviceToRecord;

    public AudioRecorder(File outputFile, Mixer deviceName) {
        this.outputFile = outputFile;
        this.deviceToRecord = deviceName;
    }

    public void start() {
        MixerHelper.printAllMixersInfo();
        System.out.printf("Starting AudioRecorder %s\n", deviceToRecord);
        try {
            if (deviceToRecord == null) {
                System.out.println("Device not found");
                return;
            }

            AudioFormat supportedFormat = MixerHelper.getSupportedAudioFormats(deviceToRecord);
            if (supportedFormat == null) {
                System.out.println("No supported audio format found");
                return;
            }

            targetDataLine = MixerHelper.getTargetDataLine(deviceToRecord, supportedFormat);
            targetDataLine.open(supportedFormat);
            targetDataLine.start();

            audioInputStream = new AudioInputStream(targetDataLine);

            new Thread(() -> {
                try {
                    AudioSystem.write(audioInputStream, AudioFileFormat.Type.WAVE, outputFile);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }).start();

        } catch (LineUnavailableException e) {
            e.printStackTrace();
        }
    }

    public void stop() {
        if (targetDataLine != null) {
            targetDataLine.stop();
            targetDataLine.close();
            System.err.printf("Mixer %s stopped\n", deviceToRecord);
        }
        if (audioInputStream != null) {
            try {
                audioInputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}