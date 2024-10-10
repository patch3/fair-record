package ru.sudrf.fairrecord.fairrecord.managers;

import lombok.Getter;
import lombok.NonNull;
import ru.sudrf.fairrecord.fairrecord.controllers.MainController;
import ru.sudrf.fairrecord.fairrecord.controllers.SoundtrackController;

import java.util.ArrayList;
import java.util.List;

/**
 * Менеджер для управления списком звуковых дорожек.
 *
 * <p>Класс {@code SoundtrackManager} предоставляет методы для добавления, удаления и получения списка
 * звуковых дорожек, управляемых через {@link SoundtrackController}. Также отслеживает количество
 * звуковых дорожек в списке.
 *
 * @see SoundtrackController
 * @see MainController
 */
public class SoundtrackManager {
    /**
     * Список управляемых звуковых дорожек.
     */
    private List<SoundtrackController> soundtracks = new ArrayList<>();

    /**
     * Количество звуковых дорожек в списке.
     */
    @Getter
    private int count = 0;

    /**
     * Возвращает неизменяемую копию списка звуковых дорожек.
     *
     * @return Неизменяемая копия списка звуковых дорожек.
     */
    public List<SoundtrackController> getSoundtracks() {
        return List.copyOf(soundtracks);
    }

    /**
     * Добавляет новую звуковую дорожку в список.
     *
     * @param soundtrack Звуковая дорожка для добавления. Не может быть {@code null}.
     * @throws NullPointerException Если переданный параметр {@code soundtrack} равен {@code null}.
     */
    public void addSoundtrack(@NonNull SoundtrackController soundtrack) {
        count++;
        soundtracks.add(soundtrack);
    }

    /**
     * Удаляет звуковую дорожку из списка.
     *
     * <p>После удаления звуковая дорожка останавливается, и она удаляется из пользовательского интерфейса
     * через {@link MainController#deleteSoundtrack(SoundtrackController)}.
     *
     * @param soundtrack Звуковая дорожка для удаления. Не может быть {@code null}.
     * @throws NullPointerException Если переданный параметр {@code soundtrack} равен {@code null}.
     */
    public void removeSoundtrack(@NonNull SoundtrackController soundtrack) {
        count--;
        soundtracks.remove(soundtrack);
        soundtrack.clickStop(null);
        MainController.deleteSoundtrack(soundtrack);
    }
}