package com.NCFrontend.managers;

import com.NCFrontend.screens.GameplayScreen;
import com.NCFrontend.ui.CardActor;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class UIManager {
    private GameplayScreen screen;
    private Label deckCountLabel;
    private Label phaseLabel;
    private Label ramLabel;
    public boolean isDialogOpen = false;
    private Label playerHpLabel;
    private Label enemyHpLabel;

    // --- TEKSTUR GLOBAL AGAR TIDAK MEMORY LEAK ---
    private Texture sharedDimTex;
    private Texture sharedBoxTex;

    public UIManager(GameplayScreen screen) {
        this.screen = screen;
    }

    public void setupUI() {
        float screenW = screen.stage.getViewport().getWorldWidth();
        float screenH = screen.stage.getViewport().getWorldHeight();

        // 1. BUAT TEKSTUR GLOBAL SEKALI SAJA UNTUK SELAMANYA
        Pixmap dimPix = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        dimPix.setColor(new Color(0, 0, 0, 0.85f));
        dimPix.fill();
        sharedDimTex = new Texture(dimPix);
        dimPix.dispose();

        Pixmap boxPix = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        boxPix.setColor(new Color(0.2f, 0.2f, 0.2f, 1f));
        boxPix.fill();
        sharedBoxTex = new Texture(boxPix);
        boxPix.dispose();

        // --- UI KESEHATAN PEMAIN (HP) - KOTAK KIRI ---
        playerHpLabel = new Label("HP: " + screen.playerProfile.hp, new Label.LabelStyle(new BitmapFont(), Color.GREEN));
        playerHpLabel.setFontScale(1.5f);
        playerHpLabel.setSize(200, 40);
        playerHpLabel.setAlignment(com.badlogic.gdx.utils.Align.center);
        playerHpLabel.setPosition(screenW / 2f - 355, 315);
        screen.stage.addActor(playerHpLabel);

        // --- RENDER LABEL RAM - KOTAK TENGAH ---
        ramLabel = new Label("RAM: " + screen.playerProfile.currentRam + " / " + screen.playerProfile.maxRam, new Label.LabelStyle(new BitmapFont(), Color.CYAN));
        ramLabel.setFontScale(1.5f);
        ramLabel.setSize(250, 40);
        ramLabel.setAlignment(com.badlogic.gdx.utils.Align.center);
        ramLabel.setPosition(screenW / 2f - 125, 315);
        screen.stage.addActor(ramLabel);

        // --- UI KESEHATAN MUSUH (HP) - KOTAK KANAN ---
        enemyHpLabel = new Label("HP: " + screen.enemyProfile.hp, new Label.LabelStyle(new BitmapFont(), Color.RED));
        enemyHpLabel.setFontScale(1.5f);
        enemyHpLabel.setSize(200, 40);
        enemyHpLabel.setAlignment(com.badlogic.gdx.utils.Align.center);
        enemyHpLabel.setPosition(screenW / 2f + 185, 315);
        screen.stage.addActor(enemyHpLabel);

        // --- TOMBOL END TURN ---
        Image endTurnBtn = new Image(sharedDimTex); // Pinjam tekstur dim agar tidak buat baru
        endTurnBtn.getColor().a = 0f; // Transparan
        endTurnBtn.setSize(160, 75);
        endTurnBtn.setPosition(1445, 265);
        screen.stage.addActor(endTurnBtn);

        endTurnBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (screen.phaseManager.currentPhase == GamePhaseManager.GamePhase.PLAYER_MAIN) {
                    screen.phaseManager.endPlayerTurn();
                }
            }
        });
    }

    public void updateDeckCount(int count) {
        if (deckCountLabel != null) {
            deckCountLabel.setText(String.valueOf(count));
            deckCountLabel.toFront();
        }
    }

    public void updatePhaseLabel(String text, Color color) {
        if (phaseLabel != null) {
            phaseLabel.setText(text);
            phaseLabel.setColor(color);
        }
    }

    public void updateRamLabel(int current, int max) {
        if (ramLabel != null) {
            ramLabel.setText("RAM: " + current + " / " + max);
        }
    }

    public void updateHP() {
        if (playerHpLabel != null) playerHpLabel.setText("HP: " + screen.playerProfile.hp);
        if (enemyHpLabel != null) enemyHpLabel.setText("HP: " + screen.enemyProfile.hp);
    }

    public void showCardDetail(CardActor originalCard) {
        if (isDialogOpen) return;
        this.isDialogOpen = true;

        Image dimBackground = new Image(sharedDimTex); // PAKE TEKSTUR GLOBAL
        dimBackground.setSize(com.badlogic.gdx.Gdx.graphics.getWidth(), com.badlogic.gdx.Gdx.graphics.getHeight());

        CardActor popupCard = new CardActor(originalCard.getData(), originalCard.illustrationTexture);
        float zoomFactor = 1.8f;
        popupCard.setScale(zoomFactor);

        float popupWidth = popupCard.getWidth() * zoomFactor;
        float popupHeight = popupCard.getHeight() * zoomFactor;

        popupCard.setPosition(
            (com.badlogic.gdx.Gdx.graphics.getWidth() - popupCard.getWidth()) / 2f,
            (com.badlogic.gdx.Gdx.graphics.getHeight() - popupCard.getHeight()) / 2f
        );

        popupCard.isOnBoard = false;
        popupCard.isFaceUp = true;

        Label.LabelStyle labelStyle = new Label.LabelStyle(new BitmapFont(), Color.LIGHT_GRAY);
        Label closeLabel = new Label("[ KLIK DIMANA SAJA UNTUK TUTUP ]", labelStyle);

        closeLabel.setPosition(
            (com.badlogic.gdx.Gdx.graphics.getWidth() - closeLabel.getWidth()) / 2f,
            (com.badlogic.gdx.Gdx.graphics.getHeight() - popupHeight) / 2f - 40
        );

        final Group popupGroup = new Group();
        popupGroup.addActor(dimBackground);
        popupGroup.addActor(popupCard);
        popupGroup.addActor(closeLabel);

        dimBackground.addListener(new InputListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                popupGroup.remove();
                isDialogOpen = false;
                return true;
            }
        });

        screen.stage.addActor(popupGroup);
    }

    public void showReplaceDialog(CardActor oldCard, CardActor newCard, String zoneName, Actor dropZone) {
        isDialogOpen = true;

        Image overlay = new Image(sharedDimTex); // PAKE TEKSTUR GLOBAL
        overlay.setSize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

        Image box = new Image(sharedBoxTex); // PAKE TEKSTUR GLOBAL
        box.setSize(400, 200);
        box.setPosition((Gdx.graphics.getWidth() - 400) / 2f, (Gdx.graphics.getHeight() - 200) / 2f);

        Label text = new Label("Lane Penuh!\nTimpa " + oldCard.getData().name + " dengan\n" + newCard.getData().name + "?",
            new Label.LabelStyle(new BitmapFont(), Color.WHITE));
        text.setPosition(box.getX() + 30, box.getY() + 110);

        // Pakai tekstur abu-abu untuk tombol agar tidak repot buat baru
        Image btnYes = new Image(sharedBoxTex);
        btnYes.setColor(Color.OLIVE);
        btnYes.setSize(120, 40);
        btnYes.setPosition(box.getX() + 40, box.getY() + 30);
        Label lblYes = new Label("YA", new Label.LabelStyle(new BitmapFont(), Color.WHITE));
        lblYes.setPosition(btnYes.getX() + 50, btnYes.getY() + 10);

        Image btnNo = new Image(sharedBoxTex);
        btnNo.setColor(Color.MAROON);
        btnNo.setSize(120, 40);
        btnNo.setPosition(box.getX() + 240, box.getY() + 30);
        Label lblNo = new Label("TIDAK", new Label.LabelStyle(new BitmapFont(), Color.WHITE));
        lblNo.setPosition(btnNo.getX() + 35, btnNo.getY() + 10);

        Group dialogGroup = new Group();
        dialogGroup.addActor(overlay); dialogGroup.addActor(box); dialogGroup.addActor(text);
        dialogGroup.addActor(btnYes); dialogGroup.addActor(lblYes); dialogGroup.addActor(btnNo); dialogGroup.addActor(lblNo);
        screen.stage.addActor(dialogGroup);

        btnYes.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                dialogGroup.remove();
                isDialogOpen = false;
                screen.phaseManager.useRam(newCard.getData().ramCost);
                screen.hand.removeValue(newCard, true);
                screen.updateHandPositions();
                oldCard.addAction(Actions.sequence(
                    Actions.parallel(Actions.scaleTo(0.1f, 0.1f, 0.3f), Actions.fadeOut(0.3f)),
                    Actions.removeActor()
                ));
                screen.placeCardInSlot(newCard, zoneName, dropZone);
            }
        });

        btnNo.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                dialogGroup.remove();
                isDialogOpen = false;
                Vector2 origin = (Vector2) newCard.getUserObject();
                newCard.clearActions();
                newCard.addAction(Actions.parallel(
                    Actions.scaleTo(1.0f, 1.0f, 0.3f),
                    Actions.moveTo(origin.x, origin.y, 0.3f, Interpolation.pow3Out),
                    Actions.rotateTo(0, 0.3f)
                ));
                screen.refreshHandZIndex();
            }
        });
    }

    public void showNotification(String message) {
        com.badlogic.gdx.scenes.scene2d.Actor oldNotif = screen.stage.getRoot().findActor("NOTIFICATION_POPUP");
        if (oldNotif != null) {
            oldNotif.addAction(com.badlogic.gdx.scenes.scene2d.actions.Actions.removeActor());
        }

        com.badlogic.gdx.graphics.g2d.BitmapFont font = new com.badlogic.gdx.graphics.g2d.BitmapFont();
        font.getData().setScale(1.2f);
        com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle style = new com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle(font, com.badlogic.gdx.graphics.Color.CYAN);
        com.badlogic.gdx.scenes.scene2d.ui.Label label = new com.badlogic.gdx.scenes.scene2d.ui.Label(message, style);
        label.setAlignment(com.badlogic.gdx.utils.Align.center);
        label.setWrap(true);

        com.badlogic.gdx.scenes.scene2d.ui.Table table = new com.badlogic.gdx.scenes.scene2d.ui.Table();
        table.setName("NOTIFICATION_POPUP");

        // PAKE TEKSTUR GLOBAL
        table.setBackground(new TextureRegionDrawable(new TextureRegion(sharedDimTex)));

        table.add(label).width(600).pad(15).padLeft(30).padRight(30);
        table.pack();

        table.setPosition(
            (com.badlogic.gdx.Gdx.graphics.getWidth() - table.getWidth()) / 2f,
            com.badlogic.gdx.Gdx.graphics.getHeight() - table.getHeight() - 50
        );

        table.getColor().a = 0f;
        table.addAction(com.badlogic.gdx.scenes.scene2d.actions.Actions.sequence(
            com.badlogic.gdx.scenes.scene2d.actions.Actions.fadeIn(0.3f),
            com.badlogic.gdx.scenes.scene2d.actions.Actions.delay(4.5f),
            com.badlogic.gdx.scenes.scene2d.actions.Actions.fadeOut(0.5f),
            com.badlogic.gdx.scenes.scene2d.actions.Actions.removeActor()
        ));

        screen.stage.addActor(table);
    }
}
