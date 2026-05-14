package com.NCFrontend.screens;

import com.NCFrontend.logic.CardFactory;
import com.NCFrontend.managers.EnemyAIManager;
import com.NCFrontend.network.ApiClient;
import com.NCFrontend.ui.CardActor;
import com.NCFrontend.managers.GamePhaseManager;
import com.NCFrontend.managers.UIManager;
import com.NCFrontend.managers.CardInteractionHandler;
import com.NCFrontend.models.PlayerData;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
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
    public Array<CardActor> deck = new Array<>();
    public Array<CardActor> hand = new Array<>();
    public ObjectMap<String, CardActor> activeCards = new ObjectMap<>();
    public final int MAX_HAND_SIZE = 7;
    public CardActor dummyDeckVisual;

    public String[] boardLanes = new String[4];
    public int placedLanesCount = 0;
    public float startCamX;
    public float startCamY;

    // VARIABEL BARU UNTUK NAMA FILE BOARD
    public String boardFileName;

    public PlayerData playerProfile;
    public Array<CardActor> enemyDeck = new Array<>();
    public Array<CardActor> enemyHand = new Array<>();
    public ObjectMap<String, CardActor> enemyActiveCards = new ObjectMap<>();
    public PlayerData enemyProfile;

    public GamePhaseManager phaseManager;
    public UIManager uiManager;
    public CardInteractionHandler interactionHandler;
    public EnemyAIManager enemyAI;

    private OrthographicCamera camera;
    private Group boardGroup;
    private float centerX;
    private float centerY;

    public GameplayScreen(String chosenFaction) {
        stage = new Stage(new ScreenViewport());
        Gdx.input.setInputProcessor(stage);

        String enemyFaction = chosenFaction.equals("Sysadmin") ? "OMEGA" : "Sysadmin";
        playerProfile = new PlayerData("Pemain 1", chosenFaction, 50);
        enemyProfile = new PlayerData("AI Musuh", enemyFaction, 50);

        // LOGIKA PENENTUAN GAMBAR BOARD BERDASARKAN FAKSI
        if (chosenFaction.equalsIgnoreCase("Sysadmin")) {
            this.boardFileName = "images/board_sysadmin.png";
        } else if (chosenFaction.equalsIgnoreCase("OMEGA")) {
            this.boardFileName = "images/board_omega.png";
        } else {
            this.boardFileName = "images/board.png";
        }

        phaseManager = new GamePhaseManager(this);
        uiManager = new UIManager(this);
        interactionHandler = new CardInteractionHandler(this);
        enemyAI = new EnemyAIManager(this);
    }

    @Override
    public void show() {
        com.badlogic.gdx.Gdx.input.setInputProcessor(stage);

        camera = (OrthographicCamera) stage.getCamera();
        startCamX = camera.position.x;
        startCamY = camera.position.y;
        centerX = startCamX;
        centerY = startCamY;

        boardGroup = new Group();
        boardGroup.setSize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        stage.addActor(boardGroup);

        try {
            Texture bgTex = new Texture(Gdx.files.internal(this.boardFileName));
            Image background = new Image(bgTex);
            background.setSize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
            boardGroup.addActor(background);

        } catch (Exception e) {
            Gdx.app.error("GameplayScreen", "GAGAL MEMUAT: " + boardFileName + ". Menggunakan gambar cadangan!", e);
            try {
                Texture fallbackTex = new Texture(Gdx.files.internal("images/board.png"));
                Image fallbackBg = new Image(fallbackTex);
                fallbackBg.setSize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
                boardGroup.addActor(fallbackBg);
            } catch (Exception ex) {}
        }

        boardGroup.setPosition(-Gdx.graphics.getWidth(), 0);
        interactionHandler.setupZonesAndButtons();
        uiManager.setupUI();

        ApiClient.fetchAllCards(new ApiClient.FetchCardsCallback() {
            @Override
            public void onSuccess(String jsonResponse) {
                Gson gson = new Gson();
                JsonArray cardArray = null;

                try {
                    com.google.gson.JsonObject root = com.google.gson.JsonParser.parseString(jsonResponse).getAsJsonObject();
                    if (root.has("data")) cardArray = root.getAsJsonArray("data");
                } catch (Exception e) {
                    try { cardArray = gson.fromJson(jsonResponse, JsonArray.class); } catch (Exception ex) {}
                }

                if (cardArray != null && cardArray.size() > 0) {
                    dummyDeckVisual = CardFactory.createVisualCard(cardArray.get(0).toString());
                    dummyDeckVisual.isFaceUp = false;
                    dummyDeckVisual.setScale(0.5f);
                    dummyDeckVisual.setPosition(50, 350);
                    stage.addActor(dummyDeckVisual);

                    for (JsonElement element : cardArray) {
                        CardActor visualCard = CardFactory.createVisualCard(element.toString());
                        visualCard.setVisible(false);
                        visualCard.isFaceUp = false;
                        visualCard.setScale(0.5f);

                        if (visualCard.getData().faction.equalsIgnoreCase(playerProfile.faction)) {
                            visualCard.setPosition(50, 350);
                            deck.add(visualCard);
                            interactionHandler.registerCard(visualCard);
                        } else {
                            visualCard.setPosition(1500, 800);
                            enemyDeck.add(visualCard);
                        }
                        stage.addActor(visualCard);
                    }

                    enemyDeck.shuffle();
                    smoothOpeningHand();

                    // JALANKAN ANIMASI PAPAN MASUK
                    playCinematicIntro();
                }
            }
            @Override public void onError(Throwable t) { Gdx.app.error("Game", "Koneksi Backend Gagal!"); }
        });
    }

    // METHOD ANIMASI BARU (Tanpa gerakan kamera)
    private void playCinematicIntro() {
        boardGroup.addAction(Actions.sequence(
            Actions.moveTo(0, 0, 1.2f, Interpolation.pow3Out),
            Actions.run(() -> {
                phaseManager.currentPhase = GamePhaseManager.GamePhase.LANE_SETUP;
                spawnLaneCards();
            })
        ));
    }

    public void drawCard() {
        if (deck.size > 0 && hand.size < MAX_HAND_SIZE) {
            CardActor card = deck.pop();
            card.setVisible(true);
            card.isFaceUp = true;
            hand.add(card);
            updateHandPositions();
            uiManager.updateDeckCount(deck.size);
        }
    }

    public void updateHandPositions() {
        float screenWidth = Gdx.graphics.getWidth();
        float handScale = 1.0f;
        float cardWidth = 210 * handScale;
        float spacing = -40;
        float totalWidth = (hand.size * cardWidth) + ((hand.size - 1) * spacing);
        float startX = (screenWidth - totalWidth) / 2f;

        for (int i = 0; i < hand.size; i++) {
            CardActor card = hand.get(i);
            float targetX = startX + (i * (cardWidth + spacing));
            float targetY = -10;
            card.setUserObject(new Vector2(targetX, targetY));
            card.clearActions();
            card.addAction(Actions.moveTo(targetX, targetY, 0.3f, Interpolation.pow3Out));
            card.addAction(Actions.scaleTo(handScale, handScale, 0.3f));
        }
        refreshHandZIndex();
    }

    public void refreshHandZIndex() { for (CardActor c : hand) c.toFront(); }

    public void placeCardInSlot(CardActor card, String zoneName, Actor dropZone) {
        card.clearActions();
        card.setScale(0.55f);

        // Sudah diperbaiki agar senter menggunakan getWidth() dan getHeight() asli
        card.setPosition(
            dropZone.getX() + (dropZone.getWidth() / 2f) - (card.getWidth() / 2f),
            dropZone.getY() + (dropZone.getHeight() / 2f) - (card.getHeight() / 2f)
        );

        activeCards.put(zoneName, card);
    }

    public void spawnLaneCards() {
        String[] laneNames = {"Localhost", "Cloud Storage", "DMZ", "Dark Node"};
        for (String laneName : laneNames) {
            com.NCFrontend.models.ProgramData laneData = new com.NCFrontend.models.ProgramData(laneName, 0, 0, 0, "LANE", "");
            String prefix = laneName.replace(" ", "");
            String suffix = playerProfile.faction.equalsIgnoreCase("OMEGA") ? "_Malw.png" : "_Prog.png";
            Texture tex = new Texture(Gdx.files.internal("images/" + prefix + suffix));
            CardActor laneCard = new CardActor(laneData, tex);
            laneCard.isFaceUp = true;
            hand.add(laneCard);
            stage.addActor(laneCard);
            interactionHandler.registerCard(laneCard);
        }
        updateHandPositions();
    }

    private void smoothOpeningHand() {
        Array<CardActor> cost1 = new Array<>(), cost2 = new Array<>(), expensive = new Array<>();
        for (CardActor c : deck) {
            int cost = c.getData().ramCost;
            if (cost <= 1) cost1.add(c); else if (cost == 2) cost2.add(c); else expensive.add(c);
        }
        cost1.shuffle(); cost2.shuffle(); expensive.shuffle();
        Array<CardActor> newDeck = new Array<>();
        for (int i=0; i<2; i++) { if(cost1.size>0) newDeck.add(cost1.pop()); if(cost2.size>0) newDeck.add(cost2.pop()); }
        newDeck.addAll(cost1); newDeck.addAll(cost2); newDeck.addAll(expensive);
        newDeck.reverse(); deck.clear(); deck.addAll(newDeck);
    }

    public void checkGameOver() {
        com.badlogic.gdx.Game game = (com.badlogic.gdx.Game) Gdx.app.getApplicationListener();
        if (playerProfile.hp <= 0) game.setScreen(new MatchResultScreen(game, false));
        else if (enemyProfile.hp <= 0) game.setScreen(new MatchResultScreen(game, true));
    }

    @Override public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        stage.act(delta); stage.draw(); checkGameOver();
    }
    @Override public void resize(int width, int height) { stage.getViewport().update(width, height, true); }
    @Override public void dispose() { stage.dispose(); }
}
