package Audio;

import javax.sound.sampled.*;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;

public class MusicManager {

    // ------------------------------------------------------------
    // Fields
    // ------------------------------------------------------------
    private final HashMap<String, Clip> tracks = new HashMap<>();
    private Clip currentTrack;
    private float volume = 1.0f;

    // ------------------------------------------------------------
    // Constructor
    // ------------------------------------------------------------
    public MusicManager() {
        // Preload your music files here
        loadTrack("menu", "/audio/music/menu_theme.wav");
        loadTrack("level1", "/audio/music/level1_theme.wav");
        loadTrack("boss", "/audio/music/boss_theme.wav");
    }

    // ------------------------------------------------------------
    // Loading
    // ------------------------------------------------------------
    private void loadTrack(String name, String path) {
        try {
            URL url = getClass().getResource(path);
            if (url == null) {
                System.err.println("Music file not found: " + path);
                return;
            }

            AudioInputStream ais = AudioSystem.getAudioInputStream(url);
            Clip clip = AudioSystem.getClip();
            clip.open(ais);

            tracks.put(name, clip);

        } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
            e.printStackTrace();
        }
    }

    // ------------------------------------------------------------
    // Playback
    // ------------------------------------------------------------
    public void play(String name) {
        Clip clip = tracks.get(name);
        if (clip == null) {
            System.err.println("Music track not found: " + name);
            return;
        }

        stopCurrent();

        currentTrack = clip;
        setVolume(volume);

        clip.setFramePosition(0);
        clip.loop(Clip.LOOP_CONTINUOUSLY);
        clip.start();
    }

    public void stopCurrent() {
        if (currentTrack != null && currentTrack.isRunning()) {
            currentTrack.stop();
        }
    }

    public void stopAll() {
        for (Clip clip : tracks.values()) {
            if (clip.isRunning()) {
                clip.stop();
            }
        }
    }

    // ------------------------------------------------------------
    // Volume
    // ------------------------------------------------------------
    public void setVolume(float v) {
        volume = Math.max(0f, Math.min(1f, v));

        if (currentTrack == null) return;

        FloatControl gain = (FloatControl) currentTrack.getControl(FloatControl.Type.MASTER_GAIN);

        float min = gain.getMinimum();
        float max = gain.getMaximum();
        float gainValue = min + (max - min) * volume;

        gain.setValue(gainValue);
    }

    public float getVolume() {
        return volume;
    }

    // ------------------------------------------------------------
    // Utility
    // ------------------------------------------------------------
    public void mute() {
        setVolume(0f);
    }

    public void unmute() {
        setVolume(1f);
    }
}
