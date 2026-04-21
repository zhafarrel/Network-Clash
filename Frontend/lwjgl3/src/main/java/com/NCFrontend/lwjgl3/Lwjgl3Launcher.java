package com.NCFrontend.lwjgl3;

import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;
import com.NCFrontend.Main;

/** Launches the desktop (LWJGL3) application. */
public class Lwjgl3Launcher {
    public static void main(String[] args) {
        if (StartupHelper.startNewJvmIfRequired()) return; // Membantu support macOS dan Windows
        createApplication();
    }

    private static Lwjgl3Application createApplication() {
        return new Lwjgl3Application(new Main(), getDefaultConfiguration());
    }

    private static Lwjgl3ApplicationConfiguration getDefaultConfiguration() {
        Lwjgl3ApplicationConfiguration configuration = new Lwjgl3ApplicationConfiguration();

        // 1. Judul Jendela Game
        configuration.setTitle("NetworkClashFrontend");

        // 2. Pengaturan Performa
        configuration.useVsync(true); // Menghindari screen tearing
        configuration.setForegroundFPS(Lwjgl3ApplicationConfiguration.getDisplayMode().refreshRate + 1);

        // 3. Pengaturan Resolusi Layar (Full HD 1920x1080)
        configuration.setWindowedMode(1920, 1080);

        // Agar pemain tidak bisa merubah ukuran jendela secara sembarang yang bisa merusak layout UI
        configuration.setResizable(false);

        // 4. Pengaturan Ikon Jendela
        // Pastikan file-file ini ada di folder assets/ atau lwjgl3/src/main/resources/
        configuration.setWindowIcon("libgdx128.png", "libgdx64.png", "libgdx32.png", "libgdx16.png");

        return configuration;
    }
}
