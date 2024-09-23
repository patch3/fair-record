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
        return (TargetDataLine) mixer.getLine(info);
    }

    public static List<String> getAllMixers() {
        Mixer.Info[] mixerInfos = AudioSystem.getMixerInfo();
        List<String> availableDeviceNames = new ArrayList<>();
        for (Mixer.Info mixerInfo : mixerInfos) {
            Mixer mixer = AudioSystem.getMixer(mixerInfo);
            Line.Info[] lineInfos = mixer.getTargetLineInfo();
            for (Line.Info lineInfo : lineInfos) {
                if (lineInfo instanceof DataLine.Info) {
                    DataLine.Info dataLineInfo = (DataLine.Info) lineInfo;
                    if (dataLineInfo.getLineClass() == TargetDataLine.class) {
                        availableDeviceNames.add(mixerInfo.getName());
                        break;
                    }
                }
            }
        }
        return availableDeviceNames;
    }

    public static AudioFormat getSupportedAudioFormats(Mixer mixer) {
        List<AudioFormat> supportedFormats = new ArrayList<>();
        Line.Info[] lineInfos = mixer.getSourceLineInfo();
        for (Line.Info lineInfo : lineInfos) {
            if (lineInfo instanceof DataLine.Info) {
                DataLine.Info dataLineInfo = (DataLine.Info) lineInfo;
                if (dataLineInfo.getLineClass() == TargetDataLine.class) {
                    AudioFormat[] formats = dataLineInfo.getFormats();
                    supportedFormats.addAll(Arrays.asList(formats));
                }
            }
        }

        // Проверка на пустой список
        if (supportedFormats.isEmpty()) {
            return new AudioFormat(8000.0f, 16, 1, true, false);
        }

        // Фильтрация форматов по вашим критериям
        for (AudioFormat format : supportedFormats) {
            if (format.getSampleRate() != AudioSystem.NOT_SPECIFIED) {
                return format; // Возвращаем первый формат, который соответствует критериям
            }
        }

        // Если ни один формат не соответствует критериям, возвращаем первый формат
        return supportedFormats.get(0);
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