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

public class MainMenuScreen extends ScreenAdapter {

    private com.badlogic.gdx.Game game;
    private Stage stage;

    // TAMBAHAN: Variabel untuk menyimpan pilihan (Default: Sysadmin)
    private String selectedFaction = "Sysadmin";

    public MainMenuScreen(com.badlogic.gdx.Game game) {
        this.game = game;
        stage = new Stage(new ScreenViewport());
        Gdx.input.setInputProcessor(stage);
        setupUI();
    }

    private void setupUI() {
        // 1 & 2. BUAT FONT & JUDUL (Tetap sama)
        BitmapFont font = new BitmapFont();
        font.getData().setScale(2f);

        Label.LabelStyle titleStyle = new Label.LabelStyle(font, Color.CYAN);
        Label titleLabel = new Label("NETWORK CLASH", titleStyle);
        titleLabel.setFontScale(3f);
        titleLabel.setPosition((Gdx.graphics.getWidth() - titleLabel.getWidth() * 3) / 2f, Gdx.graphics.getHeight() - 150);
        stage.addActor(titleLabel);

        // 3. BUAT GAYA TOMBOL
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

        // --- FITUR BARU: TOMBOL PEMILIH FRAKSI ---
        float centerX = Gdx.graphics.getWidth() / 2f;

        final TextButton sysadminBtn = new TextButton("[X] SYSADMIN", btnStyle);
        final TextButton malwareBtn = new TextButton("[ ] O.M.E.G.A", btnStyle);

        sysadminBtn.setSize(280, 60);
        malwareBtn.setSize(280, 60);
        sysadminBtn.setPosition(centerX - 300, Gdx.graphics.getHeight() / 2f + 50);
        malwareBtn.setPosition(centerX + 20, Gdx.graphics.getHeight() / 2f + 50);

        sysadminBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                selectedFaction = "Sysadmin";
                sysadminBtn.setText("[X] SYSADMIN"); // Beri tanda silang
                malwareBtn.setText("[ ] O.M.E.G.A");
            }
        });

        malwareBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                selectedFaction = "OMEGA";
                sysadminBtn.setText("[ ] SYSADMIN");
                malwareBtn.setText("[X] O.M.E.G.A"); // Beri tanda silang
            }
        });

        stage.addActor(sysadminBtn);
        stage.addActor(malwareBtn);
        // ----------------------------------------

        // 4. TOMBOL "MULAI BERMAIN"
        TextButton playBtn = new TextButton("MULAI MERETAS", btnStyle);
        playBtn.setSize(300, 80);
        playBtn.setPosition(centerX - 150, Gdx.graphics.getHeight() / 2f - 60);

        playBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                Gdx.app.log("Menu", "Berpindah ke Deck Builder sebagai: " + selectedFaction);
                game.setScreen(new DeckBuilderScreen(game, selectedFaction));
            }
        });
        stage.addActor(playBtn);

        // 5. TOMBOL "KELUAR"
        TextButton exitBtn = new TextButton("KELUAR", btnStyle);
        exitBtn.setSize(300, 80);
        exitBtn.setPosition(centerX - 150, Gdx.graphics.getHeight() / 2f - 180);

        exitBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                Gdx.app.exit();
            }
        });
        stage.addActor(exitBtn);
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0.05f, 0.1f, 0.15f, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        stage.act(delta);
        stage.draw();
    }

    @Override public void resize(int width, int height) { stage.getViewport().update(width, height, true); }
    @Override public void dispose() { stage.dispose(); }
}
