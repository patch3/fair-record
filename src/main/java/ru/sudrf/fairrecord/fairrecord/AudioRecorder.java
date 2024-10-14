package ru.sudrf.fairrecord.fairrecord;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import ru.sudrf.fairrecord.fairrecord.controllers.SoundtrackController;
import ru.sudrf.fairrecord.fairrecord.helpers.MixerHelper;

import javax.sound.sampled.*;
import java.io.*;

/**
 * Класс для записи аудиоданных с использованием указанного миксера.
 *
 * <p>Класс {@code AudioRecorder} предоставляет методы для запуска и остановки записи аудиоданных.
 * Запись производится в указанный файл с использованием формата WAVE.
 *
 * @see MixerHelper
 * @see TargetDataLine
 * @see AudioInputStream
 */
public class AudioRecorder {

    /**
     * Поток аудиоданных для записи.
     */
    private AudioInputStream audioInputStream;

    /**
     * Файл, в который будет производиться запись.
     */
    @Setter @Getter
    private File outputFile;

    /**
     * Линия данных для записи аудио.
     */
    @Getter
    private TargetDataLine targetDataLine;

    /**
     * Миксер, используемый для записи.
     */
    @Getter
    private final Mixer deviceToRecord;

    /**
     * Поток для выполнения записи.
     */
    private Thread recordingThread;
    @Getter
    private RecorderSettings settings;
    private volatile boolean isRecording = true;

    /**
     * Создает новый экземпляр {@code AudioRecorder} с указанными параметрами.
     *
     * @param outputFile Файл, в который будет производиться запись.
     * @param deviceName Миксер, используемый для записи.
     * @param settings   Настройки записи.
     */
    public AudioRecorder(File outputFile, Mixer deviceName, RecorderSettings settings) {
        if (outputFile.exists()) {
            this.outputFile = generateUniqueFileName(outputFile);
        }else {
            this.outputFile = outputFile;
        }
        this.deviceToRecord = deviceName;
        this.settings = settings;
    }

    private File generateUniqueFileName(File originalFile) {
        File uniqueFile = originalFile;
        int count = 1;

        while (uniqueFile.exists()) {
            String parent = originalFile.getParent();
            String name = originalFile.getName();
            String baseName = name.substring(0, name.lastIndexOf('.'));
            String extension = name.substring(name.lastIndexOf('.'));
            uniqueFile = new File(parent, baseName + " (" + count + ")" + extension);
            count++;
        }

        return uniqueFile;
    }
    /**
     * Контроллер звуковой дорожки для обновления визуализации.
     * -- SETTER --
     *  Устанавливает контроллер звуковой дорожки для обновления визуализации.
     *
     * @param soundtrackController Контроллер звуковой дорожки.

     */
    @Setter
    private SoundtrackController soundtrackController;
    /**
     * Запускает процесс записи аудиоданных.
     *
     * <p>Метод выводит информацию обо всех доступных миксерах, находит поддерживаемый формат записи,
     * открывает линию данных для записи и запускает поток для записи аудиоданных в файл.
     *
     * @see MixerHelper#printAllMixersInfo()
     * @see MixerHelper#getSupportedAudioFormats(Mixer)
     * @see MixerHelper#getTargetDataLine(Mixer, AudioFormat)
     */

    public void start() {
        MixerHelper.printAllMixersInfo();
        System.out.printf("Starting AudioRecorder %s\n", deviceToRecord);
        try {
            if (deviceToRecord == null) {
                System.out.println("Device not found");
                return;
            }

            AudioFormat supportedFormat = MixerHelper.getSupportedAudioFormats(deviceToRecord);

            targetDataLine = MixerHelper.getTargetDataLine(deviceToRecord, supportedFormat);
            targetDataLine.open(supportedFormat);
            targetDataLine.start();

            audioInputStream = new AudioInputStream(targetDataLine);

            recordingThread = new Thread(() -> {
                byte[] buffer = new byte[settings.BUFFER_SIZE];
                int bytesRead;
                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                try {
                    while (isRecording) {
                        bytesRead = targetDataLine.read(buffer, 0, buffer.length);
                        if (bytesRead > 0) {
                            // Обновляем визуализацию уровня звука
                            preUpdateVolumeVisualization(buffer, bytesRead, supportedFormat);
                            // Накапливаем данные в ByteArrayOutputStream
                            byteArrayOutputStream.write(buffer, 0, bytesRead);
                        }
                    }
                    // Записываем накопленные данные в файл после завершения записи
                    byte[] audioData = byteArrayOutputStream.toByteArray();
                    AudioInputStream finalAudioInputStream = new AudioInputStream(
                            new ByteArrayInputStream(audioData), supportedFormat, audioData.length / supportedFormat.getFrameSize());
                    AudioSystem.write(finalAudioInputStream, AudioFileFormat.Type.WAVE, generateUniqueFileName(outputFile));
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    if (targetDataLine != null) {
                        targetDataLine.stop();
                        targetDataLine.close();
                    }
                    if (audioInputStream != null) {
                        try {
                            audioInputStream.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    try {
                        byteArrayOutputStream.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            });
            recordingThread.start();

        } catch (LineUnavailableException e) {
            e.printStackTrace();
        }
    }
    /**
     * Останавливает процесс записи аудиоданных.
     *
     * <p>Метод останавливает линию данных, закрывает поток аудиоданных и прерывает поток записи.
     */
    public void stop() {
        isRecording = false; // Устанавливаем флаг для завершения записи
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
        if (recordingThread != null) {
            try {
                recordingThread.join(); // Ожидаем завершения потока
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }


    /**
     * Обновляет отображение уровня звука.
     *
     * @param buffer Буфер с аудиоданными.
     * @param length Длина данных в буфере.
     */
    private void preUpdateVolumeVisualization(byte[] buffer, int bytesRead, AudioFormat format) {
        int sampleSizeInBytes = format.getSampleSizeInBits() / 8;
        int numberOfSamples = bytesRead / sampleSizeInBytes;
        double rms = calculateRMS(buffer, numberOfSamples, sampleSizeInBytes, format);
        double db = 20 * Math.log10(rms);
        if (soundtrackController != null) {
            soundtrackController.updateVolumeVisualization(db);
        }
    }

    /**
     * Вычисляет среднеквадратичное значение (RMS) для данных аудио.
     *
     * @param buffer Буфер с аудиоданными.
     * @param length Длина данных в буфере.
     * @return Среднеквадратичное значение.
     */
    private double calculateRMS(byte[] buffer, int numberOfSamples, int sampleSizeInBytes, AudioFormat format) {
        double sum = 0;
        int channels = format.getChannels();
        boolean isSigned = format.getEncoding() == AudioFormat.Encoding.PCM_SIGNED;
        boolean isBigEndian = format.isBigEndian();

        for (int i = 0; i < numberOfSamples * sampleSizeInBytes; i += sampleSizeInBytes) {
            double sampleValue = 0;

            switch (sampleSizeInBytes) {
                case 1:
                    byte byteSample = buffer[i];
                    if (!isSigned) {
                        byteSample -= 128; // Нормализация для беззнаковых 8-битных данных
                    }
                    sampleValue = byteSample / 128.0;
                    break;
                case 2:
                    short shortSample = 0;
                    if (isBigEndian) {
                        shortSample = (short) ((buffer[i] << 8) | (buffer[i + 1] & 0xFF));
                    } else {
                        shortSample = (short) ((buffer[i + 1] << 8) | (buffer[i] & 0xFF));
                    }
                    sampleValue = shortSample / 32768.0;
                    break;
                case 3:
                    int intSample = 0;
                    if (isBigEndian) {
                        intSample = (buffer[i] << 16) | ((buffer[i + 1] & 0xFF) << 8) | (buffer[i + 2] & 0xFF);
                    } else {
                        intSample = (buffer[i + 2] << 16) | ((buffer[i + 1] & 0xFF) << 8) | (buffer[i] & 0xFF);
                    }
                    sampleValue = intSample / 8388608.0;
                    break;
                case 4:
                    int intSample4 = 0;
                    if (isBigEndian) {
                        intSample4 = (buffer[i] << 24) | ((buffer[i + 1] & 0xFF) << 16) | ((buffer[i + 2] & 0xFF) << 8) | (buffer[i + 3] & 0xFF);
                    } else {
                        intSample4 = (buffer[i + 3] << 24) | ((buffer[i + 2] & 0xFF) << 16) | ((buffer[i + 1] & 0xFF) << 8) | (buffer[i] & 0xFF);
                    }
                    sampleValue = intSample4 / 2147483648.0;
                    break;
                default:
                    throw new IllegalArgumentException("Unsupported sample size: " + sampleSizeInBytes);
            }

            sum += sampleValue * sampleValue;
        }

        return Math.sqrt(sum / (numberOfSamples * channels));
    }

    /**
     * Настройки записи звука.
     * Создание класса без аргументов создаст класс-настроек по-умолчанию.
     */
    public static @Data class RecorderSettings {
        /**
         * Включение шумоподавления.
         */
        boolean noiseReduction = false;

        /**
         * Размер буфера для записи.
         */
        final int BUFFER_SIZE = 4096;
    }



}