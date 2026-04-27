package Audio;

import javax.sound.sampled.*;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;

public class SoundManager {

    // ------------------------------------------------------------
    // Fields
    // ------------------------------------------------------------
    private final HashMap<String, Clip> tracks = new HashMap<>();
    private Clip currentTrack;
    private float volume = 1.0f;

    // ------------------------------------------------------------
    // Constructor
    // ------------------------------------------------------------
    public SoundManager() {
        // Preload your music files here
        loadTrack("playing", "/audio/music/playing_theme.wav");

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
            System.err.println("Sound effect not found: " + name);
            return;
        }

        // Set volume specifically for this clip before starting
        applyVolumeToClip(clip);

        clip.setFramePosition(0); // Always start from beginning
        clip.start();             // Play once, do NOT loop
    }

    public void stopCurrent() {
        if (currentTrack != null && currentTrack.isRunning()) {
            currentTrack.stop();
        }
    }



    private void applyVolumeToClip(Clip clip) {
        if (clip.isControlSupported(FloatControl.Type.MASTER_GAIN)) {
            FloatControl gain = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
            float dB = (float) (Math.log10(volume) * 20);
            gain.setValue(dB);
        }
    }

    public void stopAll() {
        for (Clip clip : tracks.values()) {
            if (clip.isRunning()) {
                clip.stop();
            }
        }
    }

    public void cleanup() {
        for (Clip clip : tracks.values()) {
            if (clip.isOpen()) {
                clip.stop();
                clip.close(); // <--- Frees the memory
            }
        }
        tracks.clear();
    }

    // ------------------------------------------------------------
    // Volume
    // ------------------------------------------------------------
    public void setVolume(float v) {
        this.volume = Math.max(0.0001f, Math.min(1f, v));
        // Apply to all loaded clips so the next time they play, they are at the right volume
        for (Clip clip : tracks.values()) {
            applyVolumeToClip(clip);
        }
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
