import java.applet.Applet;
import java.applet.AudioClip;
import java.net.URL;
import java.util.Hashtable;
import java.util.Vector;

public class SoundLib {
    Hashtable<String, AudioClip> sounds;
    Vector<AudioClip> loopingClips;

    public SoundLib() {
        sounds = new Hashtable<>();
        loopingClips = new Vector<>();
    }

    public void loadSound(String name, String path) {
        if (sounds.containsKey(name)) {
            return;
        }
        URL sound_url = getClass().getClassLoader().getResource(path);
        sounds.put(name, Applet.newAudioClip(sound_url));
    }

    public void playSound(String name) {
        AudioClip audio = sounds.get(name);
        audio.play();
    }

    public void loopSound(String name) {
        AudioClip audio = sounds.get(name);
        audio.play();
    }

    public void stopLoopingSound() {
        for (AudioClip c : loopingClips) {
            c.stop();
        }
    }
}
