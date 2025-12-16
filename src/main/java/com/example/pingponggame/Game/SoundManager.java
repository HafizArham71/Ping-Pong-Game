package com.example.pingponggame.Game;

import javafx.application.Platform;
import javafx.scene.media.AudioClip;

import java.net.URL;

public final class SoundManager {

    private SoundManager() {}

    public static void play(String resourcePath) {

        Runnable playTask = () -> {
            URL url = SoundManager.class.getResource(resourcePath);
            if (url == null) {
                System.err.println("Sound not found: " + resourcePath);
                return;
            }

            AudioClip clip = new AudioClip(url.toExternalForm());
            clip.play();
        };

        // If already on JavaFX thread, play directly
        if (Platform.isFxApplicationThread()) {
            playTask.run();
        } else {
            Platform.runLater(playTask);
        }
    }
}
