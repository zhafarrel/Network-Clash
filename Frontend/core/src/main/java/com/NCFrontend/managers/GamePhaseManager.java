package com.NCFrontend.managers;

import com.NCFrontend.screens.GameplayScreen;
import com.NCFrontend.ui.CardActor;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;

public class GamePhaseManager {
    public enum GamePhase { PLAYER_DRAW, PLAYER_MAIN, ENEMY_TURN }

    private GameplayScreen screen;
    public GamePhase currentPhase = GamePhase.PLAYER_DRAW;
    private boolean isFirstTurn = true;

    // --- SISTEM RAM ---
    public int maxRam = 0;
    public int currentRam = 0;
    public final int MAX_RAM_LIMIT = 10;

    public GamePhaseManager(GameplayScreen screen) {
        this.screen = screen;
    }

    public void startPlayerTurn() {
        currentPhase = GamePhase.PLAYER_DRAW;
        screen.uiManager.updatePhaseLabel("GILIRAN: SYSADMIN", Color.CYAN);

        // --- TAMBAH & REFRESH RAM SETIAP AWAL GILIRAN ---
        if (maxRam < MAX_RAM_LIMIT) {
            maxRam++;
        }
        currentRam = maxRam;
        screen.uiManager.updateRamLabel(currentRam, maxRam);

        if (isFirstTurn) {
            isFirstTurn = false;
            float delay = 0.3f;
            for (int i = 0; i < 6; i++) {
                screen.stage.addAction(Actions.delay(i * delay, Actions.run(() -> screen.drawCard())));
            }
            screen.stage.addAction(Actions.delay(6 * delay, Actions.run(() -> currentPhase = GamePhase.PLAYER_MAIN)));
        } else {
            screen.drawCard();

            // Bangunkan semua kartu yang tertidur (landscape) di arena
            for (com.badlogic.gdx.utils.ObjectMap.Entry<String, CardActor> entry : screen.activeCards) {
                CardActor c = entry.value;

                if (c.isFlooped) {
                    c.isFlooped = false;
                    // Putar kembali ke posisi berdiri
                    // (Kotak tombol oranye akan OTOMATIS muncul lagi berkat kode di CardActor.java)
                    c.addAction(Actions.rotateTo(0, 0.4f, Interpolation.smooth));
                }
            }
            currentPhase = GamePhase.PLAYER_MAIN;
        }
    }

    public void endPlayerTurn() {
        currentPhase = GamePhase.ENEMY_TURN;
        screen.uiManager.updatePhaseLabel("GILIRAN: O.M.E.G.A", Color.RED);

        // Panggil otak AI Musuh!
        if (screen.enemyAI != null) {
            screen.enemyAI.startTurn();
        }
    }

    // --- METHOD UNTUK MEMAKAI RAM ---
    public void useRam(int amount) {
        if (currentRam >= amount) {
            currentRam -= amount;
            screen.uiManager.updateRamLabel(currentRam, maxRam);
        }
    }
}
