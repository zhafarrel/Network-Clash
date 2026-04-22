package com.NCFrontend.screens;

import com.NCFrontend.logic.CardFactory;
import com.NCFrontend.network.ApiClient;
import com.NCFrontend.ui.CardActor;
import com.NCFrontend.models.BaseCard;
import com.NCFrontend.models.ScriptData;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.DragAndDrop;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;

public class GameplayScreen extends ScreenAdapter {

    public enum GamePhase { PLAYER_DRAW, PLAYER_MAIN, ENEMY_TURN }

    private Stage stage;
    private DragAndDrop dragAndDrop;
    private Array<CardActor> deck = new Array<>();
    private Array<CardActor> hand = new Array<>();
    private Label deckCountLabel;
    private Label phaseLabel;
    private final int MAX_HAND_SIZE = 7;
    private GamePhase currentPhase = GamePhase.PLAYER_DRAW;
    private boolean isFirstTurn = true;

    public GameplayScreen() {
        stage = new Stage(new ScreenViewport());
        dragAndDrop = new DragAndDrop();
        dragAndDrop.setTapSquareSize(10);
        Gdx.input.setInputProcessor(stage);
    }

    @Override
    public void show() {
        try {
            Texture bgTex = new Texture(Gdx.files.internal("images/pseudo_3d_lanes.png"));
            Image background = new Image(bgTex);
            background.setFillParent(true);
            stage.addActor(background);
        } catch (Exception e) {}

        createDropZone("Localhost", 300, 200, 175, 230);
        createDropZone("Cloud Storage", 700, 200, 175, 230);
        createDropZone("DMZ", 1100, 200, 175, 230);
        createDropZone("Dark Node", 1500, 200, 175, 230);

        setupUI();

        ApiClient.fetchAllCards(new ApiClient.FetchCardsCallback() {
            @Override
            public void onSuccess(String jsonResponse) {
                Gson gson = new Gson();
                JsonArray cardArray = gson.fromJson(jsonResponse, JsonArray.class);
                for (JsonElement element : cardArray) {
                    CardActor visualCard = CardFactory.createVisualCard(element.toString());
                    visualCard.isFaceUp = false;
                    visualCard.setScale(0.5f);
                    visualCard.setPosition(50, 350);
                    stage.addActor(visualCard);
                    deck.add(visualCard);
                    setupHoverEffect(visualCard);

                    dragAndDrop.addSource(new DragAndDrop.Source(visualCard) {
                        @Override
                        public DragAndDrop.Payload dragStart(InputEvent event, float x, float y, int pointer) {
                            if (currentPhase != GamePhase.PLAYER_MAIN || !hand.contains(visualCard, true)) return null;
                            DragAndDrop.Payload payload = new DragAndDrop.Payload();
                            payload.setObject(visualCard);
                            payload.setDragActor(visualCard);
                            visualCard.setScale(1.0f);
                            visualCard.toFront();
                            return payload;
                        }
                        @Override
                        public void dragStop(InputEvent event, float x, float y, int pointer, DragAndDrop.Payload payload, DragAndDrop.Target target) {
                            if (target == null) {
                                Vector2 origin = (Vector2) visualCard.getUserObject();
                                visualCard.clearActions();
                                visualCard.addAction(Actions.parallel(
                                    Actions.scaleTo(1.0f, 1.0f, 0.2f),
                                    Actions.moveTo(origin.x, origin.y, 0.2f, Interpolation.pow3Out)
                                ));
                                refreshHandZIndex();
                            }
                        }
                    });
                }
                deck.shuffle();
                startPlayerTurn();
            }
            @Override public void onError(Throwable t) {}
        });
    }

    private void setupHoverEffect(CardActor card) {
        card.addListener(new InputListener() {
            @Override
            public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
                if (pointer == -1 && hand.contains(card, true) && currentPhase == GamePhase.PLAYER_MAIN) {
                    card.toFront();
                    card.clearActions();
                    Vector2 origin = (Vector2) card.getUserObject();
                    card.addAction(Actions.parallel(
                        Actions.scaleTo(1.25f, 1.25f, 0.15f, Interpolation.smooth),
                        Actions.moveTo(origin.x, origin.y + 40, 0.15f, Interpolation.smooth)
                    ));
                }
            }
            @Override
            public void exit(InputEvent event, float x, float y, int pointer, Actor toActor) {
                if (pointer == -1 && hand.contains(card, true)) {
                    card.clearActions();
                    Vector2 origin = (Vector2) card.getUserObject();
                    card.addAction(Actions.parallel(
                        Actions.scaleTo(1.0f, 1.0f, 0.15f, Interpolation.smooth),
                        Actions.moveTo(origin.x, origin.y, 0.15f, Interpolation.smooth)
                    ));
                    refreshHandZIndex();
                }
            }
        });
    }

    private void startPlayerTurn() {
        currentPhase = GamePhase.PLAYER_DRAW;
        phaseLabel.setText("GILIRAN: SYSADMIN");
        phaseLabel.setColor(Color.CYAN);
        if (isFirstTurn) {
            isFirstTurn = false;
            float delay = 0.3f;
            for (int i = 0; i < 6; i++) {
                stage.addAction(Actions.delay(i * delay, Actions.run(new Runnable() {
                    @Override public void run() { drawCard(); }
                })));
            }
            stage.addAction(Actions.delay(6 * delay, Actions.run(new Runnable() {
                @Override public void run() { currentPhase = GamePhase.PLAYER_MAIN; }
            })));
        } else {
            drawCard();
            currentPhase = GamePhase.PLAYER_MAIN;
        }
    }

    private void endPlayerTurn() {
        currentPhase = GamePhase.ENEMY_TURN;
        phaseLabel.setText("GILIRAN: O.M.E.G.A");
        phaseLabel.setColor(Color.RED);
        stage.addAction(Actions.delay(2.0f, Actions.run(new Runnable() {
            @Override public void run() { startPlayerTurn(); }
        })));
    }

    private void setupUI() {
        float screenW = stage.getViewport().getWorldWidth();
        float screenH = stage.getViewport().getWorldHeight();
        deckCountLabel = new Label("0", new Label.LabelStyle(new BitmapFont(), Color.YELLOW));
        deckCountLabel.setFontScale(2.5f);
        deckCountLabel.setPosition(80, 420);
        stage.addActor(deckCountLabel);

        phaseLabel = new Label("MEMUAT...", new Label.LabelStyle(new BitmapFont(), Color.WHITE));
        phaseLabel.setFontScale(2.0f);
        phaseLabel.setPosition(screenW / 2f - 100, screenH - 50);
        stage.addActor(phaseLabel);

        Pixmap btnPix = new Pixmap(180, 50, Pixmap.Format.RGBA8888);
        btnPix.setColor(new Color(0.8f, 0.2f, 0.2f, 1f));
        btnPix.fill();
        Image endTurnBtn = new Image(new Texture(btnPix));
        btnPix.dispose();
        endTurnBtn.setPosition(screenW - 200, 20);
        stage.addActor(endTurnBtn);
        endTurnBtn.addListener(new ClickListener() {
            @Override public void clicked(InputEvent event, float x, float y) {
                if (currentPhase == GamePhase.PLAYER_MAIN) endPlayerTurn();
            }
        });
    }

    private void drawCard() {
        if (deck.size > 0 && hand.size < MAX_HAND_SIZE) {
            CardActor card = deck.pop();
            card.isFaceUp = true;
            hand.add(card);
            updateHandPositions();

            // --- PERBAIKAN DI SINI ---
            // Update teks jumlah kartu
            deckCountLabel.setText(String.valueOf(deck.size));

            // Paksa label angka untuk selalu pindah ke lapisan paling depan
            // agar tidak tertutup tumpukan kartu fisik di deck
            deckCountLabel.toFront();
        }
    }

    private void updateHandPositions() {
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

    private void refreshHandZIndex() {
        for (CardActor c : hand) {
            c.toFront();
        }
    }

    private void createDropZone(String zoneName, float x, float y, float width, float height) {
        Pixmap pixmap = new Pixmap((int) width, (int) height, Pixmap.Format.RGBA8888);
        pixmap.setColor(new Color(0, 1, 0, 0.3f));
        pixmap.fill();
        Image dropZone = new Image(new Texture(pixmap));
        pixmap.dispose();
        dropZone.setPosition(x, y);
        stage.addActor(dropZone);

        dragAndDrop.addTarget(new DragAndDrop.Target(dropZone) {
            @Override
            public boolean drag(DragAndDrop.Source source, DragAndDrop.Payload payload, float x, float y, int pointer) {
                CardActor card = (CardActor) payload.getObject();
                BaseCard cardData = card.getData();
                String cardLane = cardData.validLane;
                if (cardLane != null && (zoneName.equalsIgnoreCase(cardLane) || cardLane.equalsIgnoreCase("ANY_LANE") || cardData instanceof ScriptData)) {
                    getActor().setColor(new Color(0, 1, 1, 0.6f));
                    return true;
                }
                getActor().setColor(new Color(1, 0, 0, 0.6f));
                return false;
            }
            @Override public void reset(DragAndDrop.Source source, DragAndDrop.Payload payload) { getActor().setColor(Color.WHITE); }
            @Override
            public void drop(DragAndDrop.Source source, DragAndDrop.Payload payload, float x, float y, int pointer) {
                CardActor card = (CardActor) payload.getObject();
                hand.removeValue(card, true);
                updateHandPositions();

                if (card.getData() instanceof ScriptData) {
                    card.addAction(Actions.sequence(Actions.parallel(Actions.scaleTo(0.1f, 0.1f, 0.3f), Actions.fadeOut(0.3f)), Actions.removeActor()));
                } else {
                    card.clearActions();
                    card.setScale(0.55f);
                    float scaledW = card.getWidth() * 0.55f;
                    float scaledH = card.getHeight() * 0.55f;
                    card.setPosition(getActor().getX() + (getActor().getWidth()/2f) - (scaledW/2f), getActor().getY() + (getActor().getHeight()/2f) - (scaledH/2f));
                }
            }
        });
    }

    @Override public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        stage.act(delta);
        stage.draw();
    }
    @Override public void resize(int width, int height) { stage.getViewport().update(width, height, true); }
    @Override public void dispose() { stage.dispose(); }
}
