package com.NCFrontend.screens;

import com.NCFrontend.logic.CardFactory;
import com.NCFrontend.managers.EnemyAIManager;
import com.NCFrontend.network.ApiClient;
import com.NCFrontend.ui.CardActor;
import com.NCFrontend.managers.GamePhaseManager;
import com.NCFrontend.managers.UIManager;
import com.NCFrontend.managers.CardInteractionHandler;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;

public class GameplayScreen extends ScreenAdapter {

    public Stage stage;

    // --- PENYIMPANAN STATE/MEMORI DATA PEMAIN ---
    public Array<CardActor> deck = new Array<>();
    public Array<CardActor> hand = new Array<>();
    public ObjectMap<String, CardActor> activeCards = new ObjectMap<>();
    public final int MAX_HAND_SIZE = 7;
    public CardActor dummyDeckVisual;

    // --- PENYIMPANAN STATE/MEMORI DATA MUSUH (AI) ---
    public Array<CardActor> enemyDeck = new Array<>();
    public Array<CardActor> enemyHand = new Array<>();
    public ObjectMap<String, CardActor> enemyActiveCards = new ObjectMap<>();

    // --- PARA MANAGER ---
    public GamePhaseManager phaseManager;
    public UIManager uiManager;
    public CardInteractionHandler interactionHandler;
    public EnemyAIManager enemyAI;

    public GameplayScreen() {
        stage = new Stage(new ScreenViewport());
        Gdx.input.setInputProcessor(stage);

        // Inisialisasi Manager
        phaseManager = new GamePhaseManager(this);
        uiManager = new UIManager(this);
        interactionHandler = new CardInteractionHandler(this);
        enemyAI = new EnemyAIManager(this); // Inisialisasi AI Musuh
    }

    @Override
    public void show() {
        try {
            Texture bgTex = new Texture(Gdx.files.internal("images/pseudo_3d_lanes.png"));
            Image background = new Image(bgTex);
            background.setFillParent(true);
            stage.addActor(background);
        } catch (Exception e) {}

        // Bangun Layout UI & Zone Drop
        interactionHandler.setupZonesAndButtons();
        uiManager.setupUI();

        // Tarik Data Kartu dari Backend
        ApiClient.fetchAllCards(new ApiClient.FetchCardsCallback() {
            @Override
            public void onSuccess(String jsonResponse) {
                Gson gson = new Gson();
                JsonArray cardArray = gson.fromJson(jsonResponse, JsonArray.class);

                // --- 1. BUAT KARTU PAJANGAN DECK PEMAIN ---
                if (cardArray.size() > 0) {
                    dummyDeckVisual = CardFactory.createVisualCard(cardArray.get(0).toString());
                    dummyDeckVisual.isFaceUp = false; // Telungkup
                    dummyDeckVisual.setScale(0.5f);
                    dummyDeckVisual.setPosition(50, 350); // Posisi Deck Pemain
                    stage.addActor(dummyDeckVisual);
                }

                // --- 2. LOAD KARTU & PISAHKAN BERDASARKAN FACTION ---
                for (JsonElement element : cardArray) {
                    CardActor visualCard = CardFactory.createVisualCard(element.toString());
                    visualCard.setVisible(false); // Sembunyikan aslinya
                    visualCard.isFaceUp = false;
                    visualCard.setScale(0.5f);

                    // Pengecekan Faction (OMEGA vs SYSADMIN)
                    if (visualCard.getData().faction.equalsIgnoreCase("OMEGA")) {
                        visualCard.setPosition(1500, 800); // Taruh deck musuh di luar layar atas
                        enemyDeck.add(visualCard);
                    } else {
                        visualCard.setPosition(50, 350); // Taruh di deck pemain
                        deck.add(visualCard);
                        interactionHandler.registerCard(visualCard); // Hanya daftarkan kartu pemain untuk di-drag
                    }

                    stage.addActor(visualCard);
                }

                // Acak deck musuh secara normal
                enemyDeck.shuffle();

                // Gunakan algoritma penghalus tangan awal untuk Pemain (Mulligan)
                smoothOpeningHand();

                // Mulai Game
                phaseManager.startPlayerTurn();
            }
            @Override public void onError(Throwable t) {
                Gdx.app.error("Game", "Koneksi Backend Gagal!");
            }
        });
    }

    // =====================================
    // HELPER METHODS (DIPANGGIL OLEH MANAGER LAIN)
    // =====================================

    public void drawCard() {
        if (deck.size > 0 && hand.size < MAX_HAND_SIZE) {
            CardActor card = deck.pop();

            card.setVisible(true); // Munculkan kartu yang ditarik ke tangan!
            card.isFaceUp = true;
            hand.add(card);
            updateHandPositions();
            uiManager.updateDeckCount(deck.size);

            // JIKA DECK HABIS, HILANGKAN PAJANGAN
            if (deck.size == 0 && dummyDeckVisual != null) {
                dummyDeckVisual.setVisible(false);
            }
        }
    }

    public void updateHandPositions() {
        float screenWidth = Gdx.graphics.getWidth();
        float cardWidth = 210;
        float spacing = -60;
        float totalWidth = (hand.size * cardWidth) + ((hand.size - 1) * spacing);
        float startX = (screenWidth - totalWidth) / 2f;

        for (int i = 0; i < hand.size; i++) {
            CardActor card = hand.get(i);
            float targetX = startX + (i * (cardWidth + spacing));
            float targetY = 20;
            card.setUserObject(new Vector2(targetX, targetY));
            card.clearActions();
            card.addAction(Actions.moveTo(targetX, targetY, 0.3f, Interpolation.pow3Out));
            card.addAction(Actions.scaleTo(1.0f, 1.0f, 0.3f));
        }
        refreshHandZIndex();
    }

    public void refreshHandZIndex() {
        for (CardActor c : hand) {
            c.toFront();
        }
    }

    public void placeCardInSlot(CardActor card, String zoneName, Actor dropZone) {
        card.clearActions();
        card.setScale(0.55f);
        float scaledW = card.getWidth() * 0.55f;
        float scaledH = card.getHeight() * 0.55f;

        card.setPosition(
            dropZone.getX() + (dropZone.getWidth() / 2f) - (scaledW / 2f),
            dropZone.getY() + (dropZone.getHeight() / 2f) - (scaledH / 2f)
        );

        activeCards.put(zoneName, card);

        // (Catatan: Logika memunculkan tombol Execute sekarang diatur otomatis
        // oleh isOnBoard di dalam method draw() milik CardActor.java)
    }

    // =====================================
    // ALGORITMA PENGHALUS TANGAN AWAL (MULLIGAN)
    // =====================================
    private void smoothOpeningHand() {
        Array<CardActor> cost1Cards = new Array<>();
        Array<CardActor> cost2Cards = new Array<>();
        Array<CardActor> expensiveCards = new Array<>(); // Cost 3 ke atas

        // 1. Pisahkan kartu berdasarkan RAM Cost
        for (CardActor card : deck) {
            int cost = card.getData().cost;
            if (cost <= 1) { // Menangkap cost 0 (jika ada script gratis) dan 1
                cost1Cards.add(card);
            } else if (cost == 2) {
                cost2Cards.add(card);
            } else {
                expensiveCards.add(card);
            }
        }

        // Acak masing-masing kelompok
        cost1Cards.shuffle();
        cost2Cards.shuffle();
        expensiveCards.shuffle();

        Array<CardActor> newDeck = new Array<>();

        // 2. Susun "Top Deck" (Kartu yang akan ditarik pertama kali)
        for (int i = 0; i < 2; i++) {
            if (cost1Cards.size > 0) newDeck.add(cost1Cards.pop());
        }
        for (int i = 0; i < 2; i++) {
            if (cost2Cards.size > 0) newDeck.add(cost2Cards.pop());
        }

        // Penuhi slot jika kartu 1 atau 2 kurang
        while (newDeck.size < 4 && (cost1Cards.size > 0 || cost2Cards.size > 0)) {
            if (cost1Cards.size > 0) newDeck.add(cost1Cards.pop());
            else if (cost2Cards.size > 0) newDeck.add(cost2Cards.pop());
        }

        // 3. Masukkan sisa kartu menjadi satu tumpukan lalu acak
        Array<CardActor> remainingCards = new Array<>();
        remainingCards.addAll(cost1Cards);
        remainingCards.addAll(cost2Cards);
        remainingCards.addAll(expensiveCards);
        remainingCards.shuffle();

        // 4. Gabungkan sisa kartu ke bawah "Top Deck"
        newDeck.addAll(remainingCards);

        // Membalik urutan newDeck agar "Top Deck" berada di akhir array
        // (karena Array.pop() mengambil dari indeks terakhir)
        newDeck.reverse();

        // 5. Timpa deck lama dengan deck yang sudah disempurnakan
        deck.clear();
        deck.addAll(newDeck);

        Gdx.app.log("Mulligan", "Deck Pemain berhasil diurutkan untuk tangan awal yang mulus!");
    }

    @Override public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        stage.act(delta);
        stage.draw();
    }

    @Override public void resize(int width, int height) { stage.getViewport().update(width, height, true); }

    @Override public void dispose() {
        stage.dispose();
    }
}
