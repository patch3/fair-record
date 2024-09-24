package ru.sudrf.fairrecord.fairrecord.helpers;

import javax.sound.sampled.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MixerHelper {

    public static Mixer getMixerByName(String deviceName) {
        Mixer.Info[] mixerInfos = AudioSystem.getMixerInfo();
        for (Mixer.Info mixerInfo : mixerInfos) {
            if (mixerInfo.getName().contains(deviceName)) {
                return AudioSystem.getMixer(mixerInfo);
            }
        }
        return null;
    }

    public static TargetDataLine getTargetDataLine(Mixer mixer, AudioFormat audioFormat) throws LineUnavailableException {
        DataLine.Info info = new DataLine.Info(TargetDataLine.class, audioFormat);
        if (!mixer.isLineSupported(info)) {
            throw new IllegalArgumentException("Line unsupported: " + info);
        }
        return (TargetDataLine) mixer.getLine(info);
    }

    public static List<Mixer> getAllMixers() {
        Mixer.Info[] mixerInfos = AudioSystem.getMixerInfo();
        List<Mixer> availableMixers = new ArrayList<>();
        for (Mixer.Info mixerInfo : mixerInfos) {
            Mixer mixer = AudioSystem.getMixer(mixerInfo);
            Line.Info[] lineInfos = mixer.getTargetLineInfo();
            for (Line.Info lineInfo : lineInfos) {
                if (lineInfo instanceof DataLine.Info) {
                    DataLine.Info dataLineInfo = (DataLine.Info) lineInfo;
                    if (dataLineInfo.getLineClass() == TargetDataLine.class) {
                        availableMixers.add(mixer);
                        break;
                    }
                }
            }
        }
        return availableMixers;
    }

    public static AudioFormat getSupportedAudioFormats(Mixer mixer) {
        Line.Info[] lineInfos = mixer.getTargetLineInfo();
        for (Line.Info lineInfo : lineInfos) {
            if (lineInfo instanceof DataLine.Info) {
                DataLine.Info dataLineInfo = (DataLine.Info) lineInfo;
                if (dataLineInfo.getLineClass() == TargetDataLine.class) {
                    AudioFormat[] formats = dataLineInfo.getFormats();
                    for (AudioFormat format : formats) {
                        if (format.getSampleSizeInBits() == 16 && format.getChannels() == 1 && format.isBigEndian()) {
                            return new AudioFormat(format.getEncoding(), 44100, format.getSampleSizeInBits(), format.getChannels(), format.getFrameSize(), 44100, format.isBigEndian());
                        }
                    }
                }
            }
        }
        return new AudioFormat(
                AudioFormat.Encoding.PCM_SIGNED, // Кодировка: PCM_SIGNED
                44100,                          // Частота дискретизации: 44100 Гц
                16,                             // Размер сэмпла: 16 бит
                1,                              // Количество каналов: 1 (моно)
                2,                              // Размер фрейма: 2 байта (16 бит * 1 канал / 8 бит на байт)
                44100,                          // Частота дискретизации фрейма: 44100 Гц
                true                           // Порядок байтов: little-endian
        );
    }

    public static void printAllMixersInfo() {
        Mixer.Info[] mixerInfos = AudioSystem.getMixerInfo();
        for (Mixer.Info mixerInfo : mixerInfos) {
            System.out.println("Mixer: " + mixerInfo.getName());
            Mixer mixer = AudioSystem.getMixer(mixerInfo);
            Line.Info[] lineInfos = mixer.getTargetLineInfo();
            for (Line.Info lineInfo : lineInfos) {
                if (lineInfo instanceof DataLine.Info) {
                    DataLine.Info dataLineInfo = (DataLine.Info) lineInfo;
                    if (dataLineInfo.getLineClass() == TargetDataLine.class) {
                        AudioFormat[] supportedFormats = dataLineInfo.getFormats();
                        System.out.println("  Supported Formats:");
                        for (AudioFormat format : supportedFormats) {
                            System.out.println("    " + format);
                        }
                    }
                }
            }
        }
    }
}