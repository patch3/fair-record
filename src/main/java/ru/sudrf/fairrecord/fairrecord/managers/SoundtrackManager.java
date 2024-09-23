package ru.sudrf.fairrecord.fairrecord.managers;

import lombok.Getter;
import lombok.NonNull;
import ru.sudrf.fairrecord.fairrecord.controllers.SoundtrackController;

import java.util.ArrayList;
import java.util.List;

public class SoundtrackManager {
    private List<SoundtrackController> soundtracks = new ArrayList<SoundtrackController>();
    @Getter
    private int count = 0;

    public List<SoundtrackController> getSoundtracks() {
        return List.copyOf(soundtracks);
    }

    public void addSoundtrack(@NonNull SoundtrackController soundtrack) {
        count++;
        soundtracks.add(soundtrack);
    }
    public void removeSoundtrack(@NonNull SoundtrackController soundtrack) {
        count--;
        soundtracks.remove(soundtrack);
    }
}
