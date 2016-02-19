package com.mygdx.managers;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;


public class SoundManager {
    private static Sound music;
    private static Sound sfx;
    private static float masterVolume = 1.0f;
    private static OrthographicCamera camera;

    public static void setCamera(OrthographicCamera camera) {
        SoundManager.camera = camera;
    }

    public static void playMusic(String filePath, float volume) {
        playMusic(filePath, volume, 1.0f, 0.0f);
    }

    public static void playMusic(String filePath, float volume, float pitch, float pan) {
        if (music != null) {
            music.stop();
            music.dispose();
        }
        music = Gdx.audio.newSound(Gdx.files.internal(filePath));
        music.loop(volume * masterVolume, pitch, pan);
    }

    public static void playSound(String filePath, float volume) {
        playSound(filePath, volume, 1.0f, 0.0f);
    }

    public static void playSoundAtPosition(String filePath, float baseVolume, Vector3 sourcePos) {
        float boundary = camera.viewportWidth / 2;
        float xDistance = sourcePos.x - camera.position.x;
        float distance = camera.position.dst(sourcePos);
        distance = Math.min(distance, boundary);

        playSound(filePath, baseVolume * masterVolume * (1 - distance / boundary), 1.0f, xDistance / boundary);
    }

    public static void playSound(String filePath, float volume, float pitch, float pan) {
        if (sfx != null) {
            sfx.stop();
            sfx.dispose();
        }
        sfx = Gdx.audio.newSound(Gdx.files.internal(filePath));
        sfx.play(volume, pitch, pan);
    }

    public static void dispose() {
        if (music != null)
            music.dispose();
        if (sfx != null)
            sfx.dispose();
    }
}
