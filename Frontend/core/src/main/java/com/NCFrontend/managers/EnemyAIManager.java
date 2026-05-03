package com.NCFrontend.managers;

import com.NCFrontend.screens.GameplayScreen;
import com.NCFrontend.ui.CardActor;
import com.NCFrontend.models.ScriptData;
import com.NCFrontend.models.PlayerData; // TAMBAHAN
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;

public class EnemyAIManager {
    private GameplayScreen screen;

    // Variabel RAM lama dihapus, diganti panggil langsung screen.enemyProfile
    private boolean isFirstTurn = true;

    public EnemyAIManager(GameplayScreen screen) {
        this.screen = screen;
    }

    public void startTurn() {
        Gdx.app.log("AI", "=== Giliran O.M.E.G.A Dimulai! ===");

        // --- SISTEM RAM: CARD WARS ADVENTURE TIME (AI) ---
        com.NCFrontend.models.PlayerData eProfile = screen.enemyProfile;

        // Pastikan kapasitas RAM AI juga minimal 5
        if (eProfile.maxRam < 5) {
            eProfile.maxRam = 5;
        }

        // REFILL PENUH: AI mendapatkan RAM maksimalnya kembali setiap turn
        eProfile.currentRam = eProfile.maxRam;

        Gdx.app.log("AI", "RAM Musuh diisi penuh: " + eProfile.currentRam + "/" + eProfile.maxRam);
        // -------------------------------------------------

        // Beri Jeda awal sebelum mulai berpikir
        screen.stage.addAction(Actions.sequence(
            Actions.delay(1.0f),
            Actions.run(new Runnable() {
                @Override
                public void run() {
                    drawAndPlayCards();
                }
            })
        ));
    }

    private void drawAndPlayCards() {
        // AI Draw Kartu
        if (isFirstTurn) {
            isFirstTurn = false;
            for (int i = 0; i < 5; i++) {
                drawEnemyCard();
            }
        } else {
            drawEnemyCard();
        }

        // --- UPGRADE KECERDASAN AI: PRIORITAS KARTU KUAT ---
        // Urutkan kartu di tangan dari RAM paling mahal ke yang paling murah
        screen.enemyHand.sort(new java.util.Comparator<CardActor>() {
            @Override
            public int compare(CardActor c1, CardActor c2) {
                return Integer.compare(c2.getData().ramCost, c1.getData().ramCost);
            }
        });

        // Posisikan ulang kartu secara visual setelah diurutkan
        updateEnemyHandPositions();
        // ----------------------------------------------------

        // 3. AI Mengevaluasi Kartu di Tangan (Mulai dari yang termahal sekarang)
        evaluateCardAtIndex(0);
    }

    private void evaluateCardAtIndex(final int index) {
        if (index >= screen.enemyHand.size) {
            finishAITurn();
            return;
        }

        CardActor card = screen.enemyHand.get(index);

        // --- CEK APAKAH RAM MUSUH CUKUP ---
        if (card.getData().ramCost <= screen.enemyProfile.currentRam) {
            String targetLane = card.getData().validLane;

            if (targetLane == null || targetLane.equalsIgnoreCase("ANY_LANE")) {
                targetLane = findEmptyLane();
            }

            if (card.getData() instanceof ScriptData) {
                playEnemyCard(card, targetLane, true, index);
                return;
            } else if (targetLane != null && !screen.enemyActiveCards.containsKey(targetLane)) {
                playEnemyCard(card, targetLane, false, index);
                return;
            }
        }

        // Jika tidak bisa dimainkan, lanjut cek kartu berikutnya
        evaluateCardAtIndex(index + 1);
    }

    private String findEmptyLane() {
        // --- OPERASI MATA AI (Bagian 1) ---
        // AI sekarang akan membaca jalur yang diracik oleh Pemain dari array boardLanes
        String[] lanes = screen.boardLanes;
        for (String lane : lanes) {
            if (lane != null && !screen.enemyActiveCards.containsKey(lane)) {
                return lane;
            }
        }
        return screen.boardLanes[0]; // Kembali ke slot pertama jika penuh (fallback)
    }

    private void drawEnemyCard() {
        if (screen.enemyDeck.size > 0 && screen.enemyHand.size < 7) {
            CardActor card = screen.enemyDeck.pop();
            card.setVisible(true);
            card.isFaceUp = false;
            screen.enemyHand.add(card);

            screen.interactionHandler.setupEnemyInspect(card);

            updateEnemyHandPositions();
            Gdx.app.log("AI", "Menarik kartu...");
        }
    }

    private void playEnemyCard(final CardActor card, final String zoneName, boolean isScript, final int currentIndex) {
        // --- POTONG RAM MUSUH SETIAP KALI MAIN KARTU ---
        screen.enemyProfile.currentRam -= card.getData().ramCost;

        screen.enemyHand.removeValue(card, true);
        updateEnemyHandPositions();

        card.isFaceUp = true;
        card.isOnBoard = true;

        if (isScript) {
            card.toFront();
            card.addAction(Actions.sequence(
                Actions.moveTo(Gdx.graphics.getWidth()/2f - 100, Gdx.graphics.getHeight()/2f, 0.4f),
                Actions.delay(1.5f),
                Actions.parallel(Actions.scaleTo(0.1f, 0.1f, 0.3f), Actions.fadeOut(0.3f)),
                Actions.run(new Runnable() {
                    @Override
                    public void run() {
                        Gdx.app.log("AI", "Memainkan SCRIPT: " + card.getData().name);
                        // Lanjut cek sisa kartu di tangan (kembali ke indeks 0 setelah tangan bergeser)
                        evaluateCardAtIndex(0);
                    }
                }),
                Actions.removeActor()
            ));
        } else {
            float targetX = getXForLane(zoneName);
            float targetY = 550f;

            screen.enemyActiveCards.put(zoneName, card);

            card.clearActions();
            card.addAction(Actions.sequence(
                Actions.parallel(
                    Actions.scaleTo(0.55f, 0.55f, 0.4f, Interpolation.pow3Out),
                    Actions.moveTo(targetX, targetY, 0.4f, Interpolation.pow3Out),
                    Actions.rotateTo(180f, 0.4f, Interpolation.pow3Out)
                ),
                Actions.delay(0.5f),
                Actions.run(new Runnable() {
                    @Override
                    public void run() {
                        Gdx.app.log("AI", "Menaruh " + card.getData().name + " di " + zoneName);
                        // Lanjut cek sisa kartu di tangan (kembali ke indeks 0 setelah tangan bergeser)
                        evaluateCardAtIndex(0);
                    }
                })
            ));
        }
    }

    private void finishAITurn() {
        Gdx.app.log("AI", "=== Mengakhiri Fase Main O.M.E.G.A ===");
        screen.uiManager.updatePhaseLabel("BATTLE PHASE", Color.ORANGE);

        com.NCFrontend.logic.CombatResolver.resolveBoardCombat(screen, false, new Runnable() {
            @Override
            public void run() {
                screen.phaseManager.startPlayerTurn();
            }
        });
    }

    private void updateEnemyHandPositions() {
        float screenWidth = Gdx.graphics.getWidth();
        float cardWidth = 210;
        float spacing = -90;
        float totalWidth = (screen.enemyHand.size * cardWidth) + ((screen.enemyHand.size - 1) * spacing);
        float startX = (screenWidth - totalWidth) / 2f;

        for (int i = 0; i < screen.enemyHand.size; i++) {
            CardActor card = screen.enemyHand.get(i);
            float targetX = startX + (i * (cardWidth + spacing));
            float targetY = Gdx.graphics.getHeight() - 100;

            card.clearActions();
            card.addAction(Actions.parallel(
                Actions.moveTo(targetX, targetY, 0.3f, Interpolation.pow3Out),
                Actions.scaleTo(0.4f, 0.4f, 0.3f)
            ));
        }
    }

    private float getXForLane(String lane) {
        int visualIndex = 0;
        for (int i = 0; i < screen.boardLanes.length; i++) {
            if (lane.equalsIgnoreCase(screen.boardLanes[i])) {
                visualIndex = i;
                break;
            }
        }

        // Menentukan koordinat X berdasarkan indeks (0 = Paling Kiri, 3 = Paling Kanan)
        return 327.5f + (visualIndex * 400f);
    }
}
