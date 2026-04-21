package com.NCFrontend.screens;

import com.NCFrontend.Main;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.utils.ScreenUtils;

public class MainMenuScreen implements Screen {
    private final Main game;

    public MainMenuScreen(Main game) {
        this.game = game;
    }

    @Override
    public void show() {
        // Dieksekusi saat layar ini muncul
    }

    @Override
    public void render(float delta) {
        // Bersihkan layar dengan warna gelap (Hitam/Abu Tua)
        ScreenUtils.clear(0.1f, 0.1f, 0.15f, 1f);

        // Nanti gambar tombol UI diletakkan di sini

        // CONTOH SEMENTARA: Jika layar di-klik, pindah ke layar Gameplay
        if (Gdx.input.justTouched()) {
            game.setScreen(new GameplayScreen(game));
            dispose(); // Hancurkan memori menu setelah pindah
        }
    }

    @Override
    public void resize(int width, int height) {}

    @Override
    public void pause() {}

    @Override
    public void resume() {}

    @Override
    public void hide() {}

    @Override
    public void dispose() {}
}
