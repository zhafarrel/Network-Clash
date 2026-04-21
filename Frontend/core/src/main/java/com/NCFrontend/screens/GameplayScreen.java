package com.NCFrontend.screens;

import com.NCFrontend.Main;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

public class GameplayScreen implements Screen {
    private final Main game;

    // Kamera 2D dan Viewport (Agar game tidak gepeng/stretch)
    private OrthographicCamera camera;
    private Viewport viewport;

    // Resolusi virtual standar game PC
    public static final float WORLD_WIDTH = 1920f;
    public static final float WORLD_HEIGHT = 1080f;

    public GameplayScreen(Main game) {
        this.game = game;

        // Setup Kamera
        camera = new OrthographicCamera();

        // Setup Viewport: FitViewport akan menambah 'black bars' jika rasio monitor berbeda
        viewport = new FitViewport(WORLD_WIDTH, WORLD_HEIGHT, camera);

        // Pusatkan kamera ke tengah
        camera.position.set(WORLD_WIDTH / 2f, WORLD_HEIGHT / 2f, 0);
    }

    @Override
    public void show() {
        // Inisialisasi kartu dan papan nantinya diletakkan di sini
    }

    @Override
    public void render(float delta) {
        // Bersihkan layar dengan warna background Cyber (Biru Gelap)
        ScreenUtils.clear(0.05f, 0.1f, 0.2f, 1f);

        // Update kamera setiap frame
        camera.update();
        game.batch.setProjectionMatrix(camera.combined);

        // Mulai menggambar aset (Belum ada gambar yang di-load, jadi layar masih kosong)
        game.batch.begin();
        // game.batch.draw(gambarPapan, ...);
        game.batch.end();
    }

    @Override
    public void resize(int width, int height) {
        // SANGAT PENTING: Perbarui viewport saat jendela diperbesar/diperkecil
        viewport.update(width, height);
    }

    @Override
    public void pause() {}

    @Override
    public void resume() {}

    @Override
    public void hide() {}

    @Override
    public void dispose() {
        // TIDAK PERLU men-dispose game.batch di sini, karena ia milik class Main
    }
}
