package com.NCFrontend;

import com.NCFrontend.managers.MyAssetManager;
import com.NCFrontend.screens.MainMenuScreen;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class Main extends Game {
    // SpriteBatch di-public agar bisa diakses oleh layar lain untuk menggambar
    public SpriteBatch batch;
    private MyAssetManager assetManager;

    @Override
    public void create() {
        batch = new SpriteBatch();

        // Panggil Singleton AssetManager
        assetManager = MyAssetManager.getInstance();

        // Pindah ke layar Main Menu pertama kali dibuka
        this.setScreen(new MainMenuScreen(this));
    }

    @Override
    public void render() {
        // SANGAT PENTING: Ini yang membuat method render() di dalam Screen bisa berjalan!
        super.render();
    }

    @Override
    public void dispose() {
        batch.dispose();
        if (assetManager != null) {
            assetManager.dispose();
        }
    }
}
