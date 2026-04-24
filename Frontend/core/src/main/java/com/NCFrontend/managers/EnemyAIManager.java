package com.NCFrontend.managers;

import com.NCFrontend.screens.GameplayScreen;
import com.NCFrontend.ui.CardActor;
import com.NCFrontend.models.ScriptData;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;

public class EnemyAIManager {
    private GameplayScreen screen;

    // Status AI
    public int maxRam = 0;
    public int currentRam = 0;
    public final int MAX_RAM_LIMIT = 10;
    private boolean isFirstTurn = true;

    public EnemyAIManager(GameplayScreen screen) {
        this.screen = screen;
    }

    public void startTurn() {
        Gdx.app.log("AI", "=== Giliran O.M.E.G.A Dimulai! ===");

        // 1. Tambah RAM Musuh
        if (maxRam < MAX_RAM_LIMIT) {
            maxRam++;
        }
        currentRam = maxRam;
        Gdx.app.log("AI", "RAM Musuh saat ini: " + currentRam);

        // 2. Beri Jeda awal sebelum mulai berpikir
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

        // 3. AI Mengevaluasi Kartu di Tangan dari indeks terkecil (0)
        // Kita gunakan iterasi manual dan jeda antar kartu agar tidak tumpang tindih
        evaluateCardAtIndex(0);
    }

    private void evaluateCardAtIndex(final int index) {
        // Jika sudah mengecek semua kartu di tangan, akhiri giliran
        if (index >= screen.enemyHand.size) {
            finishAITurn();
            return;
        }

        CardActor card = screen.enemyHand.get(index);

        // Cek apakah RAM cukup
        if (card.getData().cost <= currentRam) {
            String targetLane = card.getData().validLane;

            if (targetLane.equalsIgnoreCase("ANY_LANE")) {
                // Pilih lane acak yang kosong
                targetLane = findEmptyLane();
            }

            // Jika itu kartu Script, atau menemukan lane kosong
            if (card.getData() instanceof ScriptData) {
                playEnemyCard(card, targetLane, true, index);
                return; // Tunggu animasi selesai, lalu lanjut
            } else if (targetLane != null && !screen.enemyActiveCards.containsKey(targetLane)) {
                playEnemyCard(card, targetLane, false, index);
                return; // Tunggu animasi selesai, lalu lanjut
            }
        }

        // Jika tidak bisa dimainkan, lanjut cek kartu berikutnya
        evaluateCardAtIndex(index + 1);
    }

    private String findEmptyLane() {
        String[] lanes = {"Localhost", "Cloud Storage", "DMZ", "Dark Node"};
        for (String lane : lanes) {
            if (!screen.enemyActiveCards.containsKey(lane)) {
                return lane;
            }
        }
        return "Localhost"; // Fallback
    }

    private void drawEnemyCard() {
        if (screen.enemyDeck.size > 0 && screen.enemyHand.size < 7) {
            CardActor card = screen.enemyDeck.pop();
            card.setVisible(true); // Pastikan muncul
            card.isFaceUp = false; // Telungkup di tangan
            screen.enemyHand.add(card);

            updateEnemyHandPositions();
            Gdx.app.log("AI", "Menarik kartu...");
        }
    }

    private void playEnemyCard(final CardActor card, final String zoneName, boolean isScript, final int currentIndex) {
        currentRam -= card.getData().cost;
        screen.enemyHand.removeValue(card, true);
        updateEnemyHandPositions();

        card.isFaceUp = true;
        card.isOnBoard = true;

        if (isScript) {
            // Animasi Script
            card.toFront();
            card.addAction(Actions.sequence(
                Actions.moveTo(Gdx.graphics.getWidth()/2f - 100, Gdx.graphics.getHeight()/2f, 0.4f),
                Actions.delay(1.5f),
                Actions.parallel(Actions.scaleTo(0.1f, 0.1f, 0.3f), Actions.fadeOut(0.3f)),

                // --- PERBAIKAN DI SINI: RUN DULU, BARU REMOVE ---
                Actions.run(new Runnable() {
                    @Override
                    public void run() {
                        Gdx.app.log("AI", "Memainkan SCRIPT: " + card.getData().name);
                        // Lanjut cek sisa kartu di tangan
                        evaluateCardAtIndex(0);
                    }
                }),
                Actions.removeActor() // Pindahkan removeActor ke paling akhir!
            ));
        } else {
            // Animasi taruh Program/Malware
            float targetX = getXForLane(zoneName);
            float targetY = 550f; // Posisi Y meja musuh

            screen.enemyActiveCards.put(zoneName, card);

            card.clearActions();
            card.addAction(Actions.sequence(
                Actions.parallel(
                    Actions.scaleTo(0.55f, 0.55f, 0.4f, Interpolation.pow3Out),
                    Actions.moveTo(targetX, targetY, 0.4f, Interpolation.pow3Out),
                    Actions.rotateTo(180f, 0.4f, Interpolation.pow3Out)
                ),
                Actions.delay(0.5f), // Jeda setelah taruh
                Actions.run(new Runnable() {
                    @Override
                    public void run() {
                        Gdx.app.log("AI", "Menaruh " + card.getData().name + " di " + zoneName);
                        // Lanjut cek sisa kartu di tangan
                        evaluateCardAtIndex(0);
                    }
                })
            ));
        }
    }

    private void finishAITurn() {
        Gdx.app.log("AI", "=== Mengakhiri Giliran O.M.E.G.A ===");
        screen.stage.addAction(Actions.sequence(
            Actions.delay(1.0f),
            Actions.run(new Runnable() {
                @Override
                public void run() {
                    screen.phaseManager.startPlayerTurn();
                }
            })
        ));
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
        if (lane.equalsIgnoreCase("Localhost")) return 327.5f;
        if (lane.equalsIgnoreCase("Cloud Storage")) return 727.5f;
        if (lane.equalsIgnoreCase("DMZ")) return 1127.5f;
        if (lane.equalsIgnoreCase("Dark Node")) return 1527.5f;
        return 500f;
    }
}
