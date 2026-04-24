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
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;

public class UIManager {
    private GameplayScreen screen;
    private Label deckCountLabel;
    private Label phaseLabel;
    private Label ramLabel; // TAMBAHAN LABEL RAM
    public boolean isDialogOpen = false;

    public UIManager(GameplayScreen screen) {
        this.screen = screen;
    }

    public void setupUI() {
        float screenW = screen.stage.getViewport().getWorldWidth();
        float screenH = screen.stage.getViewport().getWorldHeight();

        deckCountLabel = new Label("0", new Label.LabelStyle(new BitmapFont(), Color.YELLOW));
        deckCountLabel.setFontScale(2.5f);
        deckCountLabel.setPosition(100, 420);
        screen.stage.addActor(deckCountLabel);

        phaseLabel = new Label("MEMUAT...", new Label.LabelStyle(new BitmapFont(), Color.WHITE));
        phaseLabel.setFontScale(2.0f);
        phaseLabel.setPosition(screenW / 2f - 100, screenH - 50);
        screen.stage.addActor(phaseLabel);

        // --- RENDER LABEL RAM (Kanan Atas) ---
        ramLabel = new Label("RAM: 0/0", new Label.LabelStyle(new BitmapFont(), Color.GREEN));
        ramLabel.setFontScale(1.8f);
        ramLabel.setPosition(screenW - 200, screenH - 120);
        screen.stage.addActor(ramLabel);

        Pixmap btnPix = new Pixmap(180, 50, Pixmap.Format.RGBA8888);
        btnPix.setColor(new Color(0.8f, 0.2f, 0.2f, 1f));
        btnPix.fill();
        Image endTurnBtn = new Image(new Texture(btnPix));
        btnPix.dispose();
        endTurnBtn.setPosition(screenW - 200, 20);
        screen.stage.addActor(endTurnBtn);

        Label btnText = new Label("AKHIRI GILIRAN", new Label.LabelStyle(new BitmapFont(), Color.WHITE));
        btnText.setPosition(screenW - 180, 35);
        screen.stage.addActor(btnText);

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

    public void showReplaceDialog(CardActor oldCard, CardActor newCard, String zoneName, Actor dropZone) {
        isDialogOpen = true;

        Pixmap overlayPix = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        overlayPix.setColor(new Color(0, 0, 0, 0.7f)); overlayPix.fill();
        Image overlay = new Image(new Texture(overlayPix)); overlayPix.dispose();
        overlay.setSize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

        Pixmap boxPix = new Pixmap(400, 200, Pixmap.Format.RGBA8888);
        boxPix.setColor(new Color(0.2f, 0.2f, 0.2f, 1f)); boxPix.fill();
        Image box = new Image(new Texture(boxPix)); boxPix.dispose();
        box.setPosition((Gdx.graphics.getWidth() - 400) / 2f, (Gdx.graphics.getHeight() - 200) / 2f);

        Label text = new Label("Lane Penuh!\nTimpa " + oldCard.getData().name + " dengan\n" + newCard.getData().name + "?",
            new Label.LabelStyle(new BitmapFont(), Color.WHITE));
        text.setPosition(box.getX() + 30, box.getY() + 110);

        Pixmap btnYesPix = new Pixmap(120, 40, Pixmap.Format.RGBA8888);
        btnYesPix.setColor(Color.OLIVE); btnYesPix.fill();
        Image btnYes = new Image(new Texture(btnYesPix)); btnYesPix.dispose();
        btnYes.setPosition(box.getX() + 40, box.getY() + 30);
        Label lblYes = new Label("YA", new Label.LabelStyle(new BitmapFont(), Color.WHITE));
        lblYes.setPosition(btnYes.getX() + 50, btnYes.getY() + 10);

        Pixmap btnNoPix = new Pixmap(120, 40, Pixmap.Format.RGBA8888);
        btnNoPix.setColor(Color.MAROON); btnNoPix.fill();
        Image btnNo = new Image(new Texture(btnNoPix)); btnNoPix.dispose();
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

                // --- POTONG RAM KARENA PEMAIN SETUJU ---
                screen.phaseManager.useRam(newCard.getData().cost);

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
}
