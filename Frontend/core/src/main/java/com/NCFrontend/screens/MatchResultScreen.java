package com.NCFrontend.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton.TextButtonStyle;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

public class MatchResultScreen extends ScreenAdapter {

    private com.badlogic.gdx.Game game;
    private Stage stage;
    private boolean isVictory; // Penentu apakah pemain menang atau kalah

    public MatchResultScreen(com.badlogic.gdx.Game game, boolean isVictory) {
        this.game = game;
        this.isVictory = isVictory;
        stage = new Stage(new ScreenViewport());
        Gdx.input.setInputProcessor(stage);

        setupUI();
    }

    private void setupUI() {
        BitmapFont font = new BitmapFont();
        font.getData().setScale(2f);

        // 1. ATUR TEKS DAN WARNA BERDASARKAN HASIL (MENANG/KALAH)
        String resultText = isVictory ? "SYSTEM RESTORED" : "SYSTEM COMPROMISED";
        Color titleColor = isVictory ? Color.CYAN : Color.RED;

        Label.LabelStyle titleStyle = new Label.LabelStyle(font, titleColor);
        Label titleLabel = new Label(resultText, titleStyle);
        titleLabel.setFontScale(3.5f);
        titleLabel.setPosition(
            (Gdx.graphics.getWidth() - titleLabel.getWidth() * 3.5f) / 2f,
            Gdx.graphics.getHeight() - 250
        );
        stage.addActor(titleLabel);

        // 2. PESAN TAMBAHAN
        String subText = isVictory ? "Ancaman O.M.E.G.A berhasil dinetralkan." : "Jaringanmu telah diambil alih oleh O.M.E.G.A.";
        Label.LabelStyle subStyle = new Label.LabelStyle(font, Color.WHITE);
        Label subLabel = new Label(subText, subStyle);
        subLabel.setPosition(
            (Gdx.graphics.getWidth() - subLabel.getWidth()) / 2f,
            Gdx.graphics.getHeight() - 320
        );
        stage.addActor(subLabel);

        // 3. STYLE TOMBOL
        TextButtonStyle btnStyle = new TextButtonStyle();
        btnStyle.font = font;
        btnStyle.fontColor = Color.WHITE;

        Pixmap pixmap = new Pixmap(300, 80, Pixmap.Format.RGBA8888);
        pixmap.setColor(Color.DARK_GRAY);
        pixmap.fill();
        btnStyle.up = new TextureRegionDrawable(new Texture(pixmap));

        pixmap.setColor(Color.GRAY);
        pixmap.fill();
        btnStyle.down = new TextureRegionDrawable(new Texture(pixmap));
        pixmap.dispose();

        // 4. TOMBOL "MAIN LAGI"
        TextButton replayBtn = new TextButton("MAIN LAGI", btnStyle);
        replayBtn.setSize(300, 80);
        replayBtn.setPosition((Gdx.graphics.getWidth() - 300) / 2f, Gdx.graphics.getHeight() / 2f - 50);
        replayBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                // Buat ulang meja pertempuran yang baru!
                game.setScreen(new GameplayScreen("Sysadmin"));
            }
        });
        stage.addActor(replayBtn);

        // 5. TOMBOL "KEMBALI KE MENU"
        TextButton menuBtn = new TextButton("KEMBALI KE MENU", btnStyle);
        menuBtn.setSize(300, 80);
        menuBtn.setPosition((Gdx.graphics.getWidth() - 300) / 2f, Gdx.graphics.getHeight() / 2f - 150);
        menuBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                // Kembali ke layar judul
                game.setScreen(new MainMenuScreen(game));
            }
        });
        stage.addActor(menuBtn);
    }

    @Override
    public void render(float delta) {
        // Jika menang, background biru gelap. Jika kalah, merah gelap.
        if (isVictory) {
            Gdx.gl.glClearColor(0.05f, 0.1f, 0.15f, 1f);
        } else {
            Gdx.gl.glClearColor(0.15f, 0.02f, 0.02f, 1f);
        }

        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        stage.act(delta);
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
    }

    @Override
    public void dispose() {
        stage.dispose();
    }
}
