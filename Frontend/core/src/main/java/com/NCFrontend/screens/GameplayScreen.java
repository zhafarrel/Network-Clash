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
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.utils.DragAndDrop;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;

public class GameplayScreen extends ScreenAdapter {
    private Stage stage;
    private DragAndDrop dragAndDrop;

    // SISTEM DECK & HAND
    private Array<CardActor> deck = new Array<>();
    private Array<CardActor> hand = new Array<>();
    private Label deckCountLabel;
    private final int MAX_HAND_SIZE = 7;

    public GameplayScreen() {
        stage = new Stage(new ScreenViewport()); // Resolusi mengikuti layar
        dragAndDrop = new DragAndDrop();
        dragAndDrop.setTapSquareSize(10);
        Gdx.input.setInputProcessor(stage);
    }

    @Override
    public void show() {
        // --- 1. SETUP BACKGROUND ---
        try {
            Texture bgTex = new Texture(Gdx.files.internal("images/pseudo_3d_lanes.png"));
            Image background = new Image(bgTex);
            background.setFillParent(true);
            stage.addActor(background);
        } catch (Exception e) {
            Gdx.app.error("Game", "Gambar background tidak ditemukan!");
        }

        // --- 2. SETUP AREA DROP ---
        createDropZone("Localhost", 300, 200, 175, 230);
        createDropZone("Cloud Storage", 700, 200, 175, 230);
        createDropZone("Deep Web", 1100, 200, 175, 230);
        createDropZone("Proxy Server", 1500, 200, 175, 230);

        // --- 3. SETUP LABEL ANGKA DECK (Di kiri layar) ---
        Label.LabelStyle style = new Label.LabelStyle(new BitmapFont(), Color.YELLOW);
        deckCountLabel = new Label("0", style);
        deckCountLabel.setFontScale(2.5f);
        // Posisi X=80, Y=420 (Pas di tengah-tengah tumpukan kartu fisik nanti)
        deckCountLabel.setPosition(80, 420);
        stage.addActor(deckCountLabel);

        // --- 4. AMBIL DATA KARTU DARI BACKEND ---
        ApiClient.fetchAllCards(new ApiClient.FetchCardsCallback() {
            @Override
            public void onSuccess(String jsonResponse) {
                Gson gson = new Gson();
                JsonArray cardArray = gson.fromJson(jsonResponse, JsonArray.class);

                for (JsonElement element : cardArray) {
                    CardActor visualCard = CardFactory.createVisualCard(element.toString());

                    // --- KARTU DIMASUKKAN KE TUMPUKAN DECK ---
                    visualCard.isFaceUp = false; // Menghadap ke belakang
                    visualCard.setScale(0.5f);   // Ukuran kecil untuk tumpukan
                    visualCard.setPosition(50, 350); // Posisi Deck di kiri layar

                    stage.addActor(visualCard); // Masuk ke stage sebagai fisik
                    deck.add(visualCard);

                    // Daftarkan sebagai SOURCE yang bisa ditarik
                    dragAndDrop.addSource(new DragAndDrop.Source(visualCard) {
                        @Override
                        public DragAndDrop.Payload dragStart(InputEvent event, float x, float y, int pointer) {
                            // PENTING: Jangan bisa di-drag kalau masih di dalam deck!
                            if (!hand.contains(visualCard, true)) return null;

                            DragAndDrop.Payload payload = new DragAndDrop.Payload();
                            payload.setObject(visualCard);
                            payload.setDragActor(visualCard);

                            visualCard.setScale(1.0f); // Ukuran normal saat ditarik
                            visualCard.toFront();
                            return payload;
                        }

                        @Override
                        public void dragStop(InputEvent event, float x, float y, int pointer, DragAndDrop.Payload payload, DragAndDrop.Target target) {
                            if (target == null) {
                                Vector2 origin = (Vector2) visualCard.getUserObject();
                                visualCard.addAction(Actions.scaleTo(1.0f, 1.0f, 0.4f));
                                visualCard.addAction(Actions.moveTo(origin.x, origin.y, 0.4f, Interpolation.pow3Out));
                            }
                        }
                    });
                }

                // Acak deck
                deck.shuffle();

                // Tarik 5 kartu pertama
                for (int i = 0; i < 5; i++) {
                    drawCard();
                }
            }

            @Override
            public void onError(Throwable t) {
                Gdx.app.error("Game", "Koneksi Backend Gagal!");
            }
        });
    }

    /**
     * Menarik kartu paling atas dari tumpukan deck ke tangan
     */
    private void drawCard() {
        if (deck.size > 0 && hand.size < MAX_HAND_SIZE) {
            CardActor card = deck.pop(); // Ambil dari Array deck

            card.isFaceUp = true; // Buka kartunya agar stat muncul!
            hand.add(card);
            card.toFront();

            updateHandPositions(); // Animasi bergerak ke posisi tangan

            // Update Label dan pastikan selalu di atas tumpukan
            deckCountLabel.setText(String.valueOf(deck.size));
            deckCountLabel.toFront();
        }
    }

    /**
     * Merapikan posisi kartu yang ada di tangan ke tengah layar
     */
    private void updateHandPositions() {
        float screenWidth = stage.getViewport().getWorldWidth();
        float cardWidth = 210;
        float spacing = 20;

        float totalWidth = (hand.size * cardWidth) + ((hand.size - 1) * spacing);
        float startX = (screenWidth - totalWidth) / 2f;

        for (int i = 0; i < hand.size; i++) {
            CardActor card = hand.get(i);
            float targetX = startX + (i * (cardWidth + spacing));
            float targetY = 20;

            card.setUserObject(new Vector2(targetX, targetY));

            // Animasi membesar ke ukuran normal dan bergeser ke target
            card.addAction(Actions.parallel(
                Actions.scaleTo(1.0f, 1.0f, 0.4f),
                Actions.moveTo(targetX, targetY, 0.4f, Interpolation.pow3Out)
            ));
        }
    }

    private void createDropZone(String zoneName, float x, float y, float width, float height) {
        Pixmap pixmap = new Pixmap((int) width, (int) height, Pixmap.Format.RGBA8888);
        pixmap.setColor(new Color(0, 1, 0, 0.3f));
        pixmap.fill();

        Texture dropTex = new Texture(pixmap);
        pixmap.dispose();

        Image dropZone = new Image(dropTex);
        dropZone.setPosition(x, y);
        stage.addActor(dropZone);

        dragAndDrop.addTarget(new DragAndDrop.Target(dropZone) {
            @Override
            public boolean drag(DragAndDrop.Source source, DragAndDrop.Payload payload, float x, float y, int pointer) {
                getActor().setColor(new Color(0, 1, 1, 0.6f));
                return true;
            }

            @Override
            public void reset(DragAndDrop.Source source, DragAndDrop.Payload payload) {
                getActor().setColor(Color.WHITE);
            }

            @Override
            public void drop(DragAndDrop.Source source, DragAndDrop.Payload payload, float x, float y, int pointer) {
                CardActor card = (CardActor) payload.getObject();

                // 1. Hapus dari tangan, geser kartu yang sisa
                hand.removeValue(card, true);
                updateHandPositions();

                // 2. Kalkulasi slot
                float slotX = getActor().getX();
                float slotY = getActor().getY();
                float slotW = getActor().getWidth();
                float slotH = getActor().getHeight();

                card.setScale(0.55f);

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
        Gdx.gl.glClearColor(0, 0, 0, 1f);
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
