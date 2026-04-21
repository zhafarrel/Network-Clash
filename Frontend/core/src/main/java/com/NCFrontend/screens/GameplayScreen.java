package com.NCFrontend.screens;

import com.NCFrontend.logic.CardFactory;
import com.NCFrontend.network.ApiClient;
import com.NCFrontend.ui.CardActor;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.utils.DragAndDrop;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;

public class GameplayScreen extends ScreenAdapter {
    private Stage stage;
    private DragAndDrop dragAndDrop;

    public GameplayScreen() {
        stage = new Stage(new ScreenViewport()); // Resolusi mengikuti layar (misal 1920x1080)
        dragAndDrop = new DragAndDrop();
        dragAndDrop.setTapSquareSize(10); // Jarak toleransi klik vs drag
        Gdx.input.setInputProcessor(stage);
    }

    @Override
    public void show() {
        // --- 1. SETUP BACKGROUND BATTLEFIELD Penuh ---
        try {
            // Gunakan gambar meja tempur utuhmu di sini
            Texture bgTex = new Texture(Gdx.files.internal("images/pseudo_3d_lanes.png"));
            Image background = new Image(bgTex);
            background.setFillParent(true); // Gambar akan memenuhi seluruh layar
            stage.addActor(background);
        } catch (Exception e) {
            Gdx.app.error("Game", "Gambar background tidak ditemukan!");
        }

        // --- 2. SETUP AREA DROP (HITBOX SLOT) ---
        // Parameter: (Nama, Posisi X, Posisi Y, Lebar, Tinggi)
        // SILAKAN UBAH nilai X dan Y di bawah ini agar kotaknya pas menimpa gambar slot di background-mu!
        createDropZone("Localhost", 300, 200, 175, 230);
        createDropZone("Cloud Storage", 700, 200, 175, 230); // Gap ditambah (+275 dari slot 1)
        createDropZone("Deep Web", 1100, 200, 175, 230);     // Gap ditambah (+275 dari slot 2)
        createDropZone("Proxy Server", 1500, 200, 175, 230);

        // --- 3. AMBIL DATA KARTU DARI BACKEND ---
        ApiClient.fetchAllCards(new ApiClient.FetchCardsCallback() {
            @Override
            public void onSuccess(String jsonResponse) {
                Gson gson = new Gson();
                JsonArray cardArray = gson.fromJson(jsonResponse, JsonArray.class);

                float xPos = 150; // Mulai dari pojok kiri bawah untuk "Tangan"
                float yPos = 20;  // Mepet bawah

                for (JsonElement element : cardArray) {
                    CardActor visualCard = CardFactory.createVisualCard(element.toString());
                    visualCard.setPosition(xPos, yPos);
                    stage.addActor(visualCard);

                    // Simpan posisi awal di tangan (untuk fitur kembali)
                    visualCard.setUserObject(new Vector2(xPos, yPos));

                    // Daftarkan sebagai SOURCE yang bisa ditarik
                    dragAndDrop.addSource(new DragAndDrop.Source(visualCard) {
                        @Override
                        public DragAndDrop.Payload dragStart(InputEvent event, float x, float y, int pointer) {
                            DragAndDrop.Payload payload = new DragAndDrop.Payload();
                            payload.setObject(visualCard);
                            payload.setDragActor(visualCard);

                            visualCard.setScale(1.0f); // Ukuran normal di tangan
                            visualCard.toFront();      // Paling depan saat ditarik
                            return payload;
                        }

                        @Override
                        public void dragStop(InputEvent event, float x, float y, int pointer, DragAndDrop.Payload payload, DragAndDrop.Target target) {
                            // Jika tidak kena Target Slot manapun, kembali ke tangan
                            if (target == null) {
                                Vector2 origin = (Vector2) visualCard.getUserObject();
                                visualCard.addAction(Actions.moveTo(origin.x, origin.y, 0.4f, Interpolation.pow3Out));
                            }
                        }
                    });

                    xPos += 220; // Jarak susunan kartu di tangan
                }
            }

            @Override
            public void onError(Throwable t) {
                Gdx.app.error("Game", "Koneksi Backend Gagal!");
            }
        });
    }

    /**
     * Membuat area kotak transparan yang berfungsi sebagai penerima (Target) drop kartu.
     */
    private void createDropZone(String zoneName, float x, float y, float width, float height) {
        // Membuat kotak warna hijau semi-transparan (HANYA UNTUK DEBUGGING MENCARI POSISI PAS)
        // Jika posisinya sudah pas dengan gambar aslimu, ubah parameter Color menjadi Color.CLEAR
        Pixmap pixmap = new Pixmap((int) width, (int) height, Pixmap.Format.RGBA8888);
        pixmap.setColor(new Color(0, 1, 0, 0.3f)); // Hijau transparan
        pixmap.fill();

        Texture dropTex = new Texture(pixmap);
        pixmap.dispose();

        Image dropZone = new Image(dropTex);
        dropZone.setPosition(x, y);
        stage.addActor(dropZone);

        // Daftarkan kotak ini sebagai Target
        dragAndDrop.addTarget(new DragAndDrop.Target(dropZone) {
            @Override
            public boolean drag(DragAndDrop.Source source, DragAndDrop.Payload payload, float x, float y, int pointer) {
                // Efek visual kotak menyala saat kartu melintas di atasnya
                getActor().setColor(new Color(0, 1, 1, 0.6f)); // Cyan agak tebal
                return true;
            }

            @Override
            public void reset(DragAndDrop.Source source, DragAndDrop.Payload payload) {
                // Kembalikan ke warna awal saat kartu menjauh
                getActor().setColor(Color.WHITE);
            }

            @Override
            public void drop(DragAndDrop.Source source, DragAndDrop.Payload payload, float x, float y, int pointer) {
                CardActor card = (CardActor) payload.getObject();

                // 1. Ambil koordinat slot
                float slotX = getActor().getX();
                float slotY = getActor().getY();
                float slotW = getActor().getWidth();
                float slotH = getActor().getHeight();

                // 2. Set skala kartu jadi lebih kecil (misal 0.6 atau 0.5 agar muat di kotak kecil)
                card.setScale(0.55f);

                // 3. Posisikan kartu tepat di tengah slot
                // Kita hitung lebar kartu setelah di-scale agar centering-nya akurat
                float scaledWidth = card.getWidth() * card.getScaleX();
                float scaledHeight = card.getHeight() * card.getScaleY();

                card.setPosition(
                    slotX + (slotW / 2f) - (scaledWidth / 2f),
                    slotY + (slotH / 2f) - (scaledHeight / 2f)
                );

                Gdx.app.log("DragDrop", "Kartu masuk ke slot: " + zoneName);
            }
        });
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 1f); // Warna dasar hitam
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
