package ru.sudrf.fairrecord.fairrecord.helpers;

import javax.sound.sampled.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

public class MixerHelper {
    /**
     * @deprecated Этот метод устарел и будет удален в будущих версиях.
     *             Оставлен, вдруг понадобится.
     * <p>
     * Возвращает миксер по имени устройства.
     *
     * <p>Метод проходит по всем доступным миксерам и ищет миксер, имя которого содержит указанное имя устройства.
     * Если миксер найден, он возвращается. В противном случае возвращается {@code null}.
     *
     * @param deviceName Имя устройства для поиска.
     * @return Миксер, соответствующий указанному имени устройства, или {@code null}, если миксер не найден.
     */
    public static Mixer getMixerByName(String deviceName) {
        Mixer.Info[] mixerInfos = AudioSystem.getMixerInfo();
        for (Mixer.Info mixerInfo : mixerInfos) {
            if (mixerInfo.getName().contains(deviceName)) {
                return AudioSystem.getMixer(mixerInfo);
            }
        }
        return null;
    }
    /**
     * Возвращает {@link TargetDataLine} для указанного миксера и формата аудиоданных.
     *
     * <p>Метод создает объект {@link DataLine.Info} для {@link TargetDataLine} с указанным форматом аудиоданных.
     * Затем проверяет, поддерживает ли миксер данный тип линии. Если линия поддерживается, метод возвращает {@link TargetDataLine}.
     * В противном случае выбрасывается исключение {@link IllegalArgumentException}.
     *
     * @param mixer Миксер, для которого нужно получить {@link TargetDataLine}.
     * @param audioFormat Формат аудиоданных, который должен поддерживаться {@link TargetDataLine}.
     * @return {@link TargetDataLine} для указанного миксера и формата аудиоданных.
     * @throws LineUnavailableException Если линия недоступна.
     * @throws IllegalArgumentException Если миксер не поддерживает указанный тип линии.
     */
    public static TargetDataLine getTargetDataLine(Mixer mixer, AudioFormat audioFormat) throws LineUnavailableException {
        DataLine.Info info = new DataLine.Info(TargetDataLine.class, audioFormat);
        if (!mixer.isLineSupported(info)) {
            throw new IllegalArgumentException("Line unsupported: " + info);
        }
        return (TargetDataLine) mixer.getLine(info);
    }
    /**
     * Возвращает список всех доступных миксеров, которые поддерживают запись аудиоданных.
     *
     * <p>Метод проходит по всем доступным миксерам и проверяет, поддерживают ли они запись аудиоданных.
     * Для этого он проверяет, есть ли среди доступных линий миксера линия, которая является экземпляром {@link TargetDataLine}.
     *
     * @return Список миксеров, поддерживающих запись аудиоданных.
     */
    public static List<Mixer> getAllMixers() {
        Mixer.Info[] mixerInfos = AudioSystem.getMixerInfo();
        List<Mixer> availableMixers = new ArrayList<>();
        for (Mixer.Info mixerInfo : mixerInfos) {
            Mixer mixer = AudioSystem.getMixer(mixerInfo);
            Line.Info[] lineInfos = mixer.getTargetLineInfo();
            for (Line.Info lineInfo : lineInfos) {
                if (lineInfo instanceof DataLine.Info dataLineInfo) {
                    if (dataLineInfo.getLineClass() == TargetDataLine.class) {
                        availableMixers.add(mixer);
                        break;
                    }
                }
            }
        }
        return availableMixers;
    }

    /**
     * Возвращает наилучший поддерживаемый аудиоформат для указанного микшера.
     * Метод проверяет все доступные целевые линии (`TargetDataLine`) и выбирает формат
     * с наивысшей частотой дискретизации и размером сэмпла. Если подходящий формат
     * не найден, метод возвращает формат по умолчанию:
     * <ul>
     *     <li>Кодировка: PCM_SIGNED</li>
     *     <li>Частота дискретизации: 44100 Гц</li>
     *     <li>Размер сэмпла: 16 бит</li>
     *     <li>Каналы: 1 (моно)</li>
     *     <li>Байты на фрейм: 2</li>
     *     <li>Частота байтов: 44100 Гц</li>
     *     <li>Big-endian: true</li>
     * </ul>
     *
     * @param mixer Микшер, для которого необходимо определить поддерживаемый аудиоформат.
     * @return Наилучший поддерживаемый аудиоформат или формат по умолчанию, если подходящий не найден.
     */
    public static AudioFormat getSupportedAudioFormats(Mixer mixer) {
        Line.Info[] lineInfos = mixer.getTargetLineInfo();
        AudioFormat bestFormat = null;

        for (Line.Info lineInfo : lineInfos) {
            if (lineInfo instanceof DataLine.Info dataLineInfo) {
                if (dataLineInfo.getLineClass() == TargetDataLine.class) {
                    AudioFormat[] formats = dataLineInfo.getFormats();
                    Arrays.sort(formats, (f1, f2) -> {
                        int sampleRateCompare = Double.compare(f2.getSampleRate(), f1.getSampleRate());
                        if (sampleRateCompare != 0) {
                            return sampleRateCompare;
                        }
                        return Integer.compare(f2.getSampleSizeInBits(), f1.getSampleSizeInBits());
                    });
                    for (AudioFormat format : formats) {
                        if (format.getSampleRate() != AudioSystem.NOT_SPECIFIED && format.getSampleSizeInBits() != AudioSystem.NOT_SPECIFIED) {
                            bestFormat = format;
                            break;
                        }
                    }
                }
            }
        }

        if (bestFormat == null) {
            bestFormat = new AudioFormat(
                    AudioFormat.Encoding.PCM_SIGNED,
                    44100,
                    16,
                    1,
                    2,
                    44100,
                    true
            );
        }

        return bestFormat;
    }

    /**
     * Выводит информацию обо всех доступных миксерах и поддерживаемых ими форматах аудиоданных.
     *
     * <p>Метод проходит по всем доступным миксерам и выводит их имена. Затем для каждого миксера
     * проверяется, поддерживает ли он линии типа {@link TargetDataLine}. Если поддерживает,
     * выводятся все поддерживаемые форматы аудиоданных.
     *
     * <p>Этот метод полезен для отладки и проверки доступных миксеров и форматов аудиоданных.
     */
    public static void printAllMixersInfo() {
        Mixer.Info[] mixerInfos = AudioSystem.getMixerInfo();
        for (Mixer.Info mixerInfo : mixerInfos) {
            System.out.println("Mixer: " + mixerInfo.getName());
            Mixer mixer = AudioSystem.getMixer(mixerInfo);
            Line.Info[] lineInfos = mixer.getTargetLineInfo();
            for (Line.Info lineInfo : lineInfos) {
                if (lineInfo instanceof DataLine.Info dataLineInfo) {
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