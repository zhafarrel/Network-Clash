package com.NCFrontend.managers;

import com.NCFrontend.screens.GameplayScreen;
import com.NCFrontend.ui.CardActor;
import com.NCFrontend.models.PlayerData; // TAMBAHAN
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Align;

public class GamePhaseManager {
    public enum GamePhase { PLAYER_DRAW, PLAYER_MAIN, ENEMY_TURN, WIN, LOSE }

    private GameplayScreen screen;
    public GamePhase currentPhase = GamePhase.PLAYER_DRAW;
    private boolean isFirstTurn = true;

    // Variabel maxRam & currentRam dihapus karena sudah diganti dengan PlayerData

    public GamePhaseManager(GameplayScreen screen) {
        this.screen = screen;
    }

    public void triggerGameOver(boolean isWin) {
        if (currentPhase == GamePhase.WIN || currentPhase == GamePhase.LOSE) return;

        if (isWin) {
            currentPhase = GamePhase.WIN;
            screen.uiManager.updatePhaseLabel("SISTEM AMAN: VICTORY!", Color.GOLD);
            Gdx.app.log("Game", "Pemain Menang!");
        } else {
            currentPhase = GamePhase.LOSE;
            screen.uiManager.updatePhaseLabel("SISTEM CRITICAL: DEFEAT!", Color.FIREBRICK);
            Gdx.app.log("Game", "Pemain Kalah!");
        }

        Gdx.input.setInputProcessor(null);

        BitmapFont bigFont = new BitmapFont();
        Label.LabelStyle bigLabelStyle = new Label.LabelStyle(bigFont, isWin ? Color.GOLD : Color.FIREBRICK);

        Label gameOverLabel = new Label(isWin ? "VICTORY!" : "DEFEAT!", bigLabelStyle);
        gameOverLabel.setFontScale(5f);
        gameOverLabel.setAlignment(Align.center);

        Table centerTable = new Table();
        centerTable.setFillParent(true);
        centerTable.add(gameOverLabel).center();

        screen.stage.addActor(centerTable);

        for (CardActor c : screen.activeCards.values()) {
            c.clearActions();
            c.addAction(Actions.parallel(Actions.fadeOut(1f), Actions.scaleTo(0, 0, 1f)));
        }

        for (CardActor c : screen.enemyActiveCards.values()) {
            c.clearActions();
            c.addAction(Actions.parallel(Actions.fadeOut(1f), Actions.scaleTo(0, 0, 1f)));
        }

        for (CardActor c : screen.hand) {
            c.clearActions();
            c.addAction(Actions.parallel(Actions.fadeOut(1f), Actions.scaleTo(0, 0, 1f)));
        }
    }

    public void startPlayerTurn() {
        if (currentPhase == GamePhase.WIN || currentPhase == GamePhase.LOSE) return;

        currentPhase = GamePhase.PLAYER_DRAW;
        screen.uiManager.updatePhaseLabel("GILIRAN: SYSADMIN", Color.CYAN);

        // --- UPDATE RAM MENGGUNAKAN MODEL BARU ---
        PlayerData pProfile = screen.playerProfile;
        if (pProfile.maxRam < 10) {
            pProfile.maxRam++;
        }
        pProfile.currentRam = pProfile.maxRam;
        screen.uiManager.updateRamLabel(pProfile.currentRam, pProfile.maxRam);
        // -----------------------------------------

        if (isFirstTurn) {
            isFirstTurn = false;
            float delay = 0.3f;
            for (int i = 0; i < 6; i++) {
                screen.stage.addAction(Actions.delay(i * delay, Actions.run(() -> screen.drawCard())));
            }
            screen.stage.addAction(Actions.delay(6 * delay, Actions.run(() -> currentPhase = GamePhase.PLAYER_MAIN)));
        } else {
            screen.drawCard();

            for (com.badlogic.gdx.utils.ObjectMap.Entry<String, CardActor> entry : screen.activeCards) {
                CardActor c = entry.value;

                if (c.isFlooped) {
                    c.isFlooped = false;
                    c.addAction(Actions.rotateTo(0, 0.4f, Interpolation.smooth));
                }
            }
            currentPhase = GamePhase.PLAYER_MAIN;
        }
    }

    public void endPlayerTurn() {
        if (currentPhase == GamePhase.WIN || currentPhase == GamePhase.LOSE) return;

        screen.uiManager.updatePhaseLabel("BATTLE PHASE", Color.ORANGE);

        com.NCFrontend.logic.CombatResolver.resolveBoardCombat(screen, true, new Runnable() {
            @Override
            public void run() {
                if (currentPhase == GamePhase.WIN || currentPhase == GamePhase.LOSE) return;

                currentPhase = GamePhase.ENEMY_TURN;
                screen.uiManager.updatePhaseLabel("GILIRAN: O.M.E.G.A", Color.RED);
                if (screen.enemyAI != null) {
                    screen.enemyAI.startTurn();
                }
            }
        });
    }

    public void useRam(int amount) {
        // --- MEMOTONG RAM DARI MODEL BARU ---
        if (screen.playerProfile.currentRam >= amount) {
            screen.playerProfile.currentRam -= amount;
            screen.uiManager.updateRamLabel(screen.playerProfile.currentRam, screen.playerProfile.maxRam);
        }
    }
}
