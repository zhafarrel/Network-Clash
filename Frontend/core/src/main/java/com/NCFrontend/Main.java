package com.NCFrontend;

import com.NCFrontend.managers.MyAssetManager;
import com.NCFrontend.screens.GameplayScreen; // Import GameplayScreen yang baru dibuat
import com.badlogic.gdx.Game;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class Main extends Game {
    public SpriteBatch batch;
    private MyAssetManager assetManager;

    @Override
    public void create() {
        batch = new SpriteBatch();

        // 1. Inisialisasi AssetManager
        assetManager = MyAssetManager.getInstance();

        // 2. Load Assets (Jika kamu punya metode loading di AssetManager, panggil di sini)
        // Pastikan images/prog_01.png dkk sudah di-load jika menggunakan AssetManager.
        // Jika CardFactory menggunakan new Texture(Gdx.files.internal...), maka baris ini aman.

        // 3. LANGSUNG ke GameplayScreen untuk ngetes kartu dari Backend
        // Kita kirim 'this' (instance Main) agar GameplayScreen bisa mengakses batch jika perlu
        this.setScreen(new GameplayScreen());
    }

    @Override
    public void render() {
        // Tetap gunakan super.render() agar GameplayScreen.render() dipanggil
        super.render();
    }

    @Override
    public void dispose() {
        // Bersihkan resource saat game ditutup
        if (batch != null) batch.dispose();
        if (assetManager != null) assetManager.dispose();

        // Pastikan screen yang aktif juga di-dispose
        if (getScreen() != null) getScreen().dispose();
    }
}
