package com.NCFrontend.ai;

import com.NCFrontend.screens.GameplayScreen;
import com.NCFrontend.ui.CardActor;
import com.NCFrontend.models.ScriptData;
import com.NCFrontend.logic.CardAbility;
import com.NCFrontend.logic.CombatResolver;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;

public class BotController {
    private GameplayScreen screen;
    private BotStrategy strategy;

    // Mesin State untuk AI
    private enum BotState { PLAY_CARDS, FLOOP_CARDS, END_TURN }
    private BotState currentState;

    public BotController(GameplayScreen screen) {
        this.screen = screen;
        this.strategy = new BotStrategy(screen);
    }

    public void executeTurn() {
        currentState = BotState.PLAY_CARDS; // Mulai dengan menaruh kartu
        processNextAction();
    }

    private void processNextAction() {
        if (currentState == BotState.PLAY_CARDS) {
            CardActor cardToPlay = strategy.getBestCardToPlay();

            if (cardToPlay != null) {
                String targetLane = strategy.getBestLaneForCard(cardToPlay);
                playCard(cardToPlay, targetLane);
                return; // Berhenti agar animasi berjalan. Animasi akan memanggil processNextAction() lagi.
            } else {
                currentState = BotState.FLOOP_CARDS; // Uang habis / tangan kosong, saatnya Floop!
                processNextAction();
                return;
            }
        }

        if (currentState == BotState.FLOOP_CARDS) {
            CardActor cardToFloop = strategy.getBestCardToFloop();

            if (cardToFloop != null) {
                executeFloop(cardToFloop);
                return;
            } else {
                currentState = BotState.END_TURN; // Semua skill sudah dipakai, akhiri turn.
                processNextAction();
                return;
            }
        }

        if (currentState == BotState.END_TURN) {
            finishTurn();
        }
    }

    private void playCard(final CardActor card, final String targetLane) {
        screen.enemyProfile.currentRam -= card.getData().ramCost;
        screen.enemyHand.removeValue(card, true);
        updateEnemyHandPositions();

        card.isFaceUp = true;
        card.isOnBoard = true;

        if (card.getData() instanceof ScriptData) {
            Gdx.app.log("BotController", "O.M.E.G.A memainkan SCRIPT: " + card.getData().name);
            screen.uiManager.showNotification("O.M.E.G.A SCRIPT:\n" + card.getData().description);
            card.toFront();
            card.addAction(Actions.sequence(
                Actions.moveTo(Gdx.graphics.getWidth() / 2f - 100, Gdx.graphics.getHeight() / 2f, 0.4f),
                Actions.delay(0.8f), // Jeda agar kamu sempat baca nama Script-nya
                Actions.run(() -> {
                    // PICU EFEK SCRIPT
                    if (card.getData().abilities != null) {
                        for (CardAbility ability : card.getData().abilities) ability.onPlayScript(card, targetLane, screen);
                    }
                }),
                Actions.parallel(Actions.scaleTo(0.1f, 0.1f, 0.3f), Actions.fadeOut(0.3f)),
                Actions.run(this::processNextAction), // Looping kembali mencari aksi
                Actions.removeActor()
            ));
        } else {
            Gdx.app.log("BotController", "O.M.E.G.A meletakkan MALWARE: " + card.getData().name);

            if (screen.enemyActiveCards.containsKey(targetLane)) {
                CardActor oldCard = screen.enemyActiveCards.get(targetLane);
                oldCard.addAction(Actions.removeActor());
            }
            screen.enemyActiveCards.put(targetLane, card);

            float targetX = getXForLane(targetLane);
            float targetY = 550f;

            card.clearActions();
            card.addAction(Actions.sequence(
                Actions.parallel(
                    Actions.scaleTo(0.55f, 0.55f, 0.4f, Interpolation.pow3Out),
                    Actions.moveTo(targetX, targetY, 0.4f, Interpolation.pow3Out),
                    Actions.rotateTo(180f, 0.4f, Interpolation.pow3Out)
                ),
                Actions.delay(0.4f),
                Actions.run(() -> {
                    // ==========================================
                    // MENCEGAH CRASH NESTED ITERATOR LIBGDX
                    // ==========================================
                    com.badlogic.gdx.utils.Array<CardActor> enemiesToNotify = screen.enemyActiveCards.values().toArray();
                    for (CardActor enemy : enemiesToNotify) {
                        if (enemy.getData().abilities != null) {
                            for (CardAbility ability : enemy.getData().abilities) ability.onCardDeployed(enemy, card, targetLane, screen);
                        }
                    }

                    com.badlogic.gdx.utils.Array<CardActor> alliesToNotify = screen.activeCards.values().toArray();
                    for (CardActor ally : alliesToNotify) {
                        if (ally.getData().abilities != null) {
                            for (CardAbility ability : ally.getData().abilities) ability.onCardDeployed(ally, card, targetLane, screen);
                        }
                    }

                    processNextAction(); // Looping kembali mencari aksi
                })
            ));
        }
    }

    private void executeFloop(final CardActor card) {
        Gdx.app.log("BotController", "O.M.E.G.A menekan tombol EXECUTE (Floop) pada: " + card.getData().name);
        card.isFlooped = true;

        screen.uiManager.showNotification("O.M.E.G.A EXECUTE:\n" + card.getData().description);

        card.addAction(Actions.sequence(
            Actions.rotateTo(90f, 0.4f, Interpolation.smooth), // Putar kartu (Visual Floop musuh)
            Actions.run(() -> {
                if (card.getData().abilities != null) {
                    for (CardAbility ability : card.getData().abilities) ability.onFloop(card, screen);
                }
            }),
            Actions.delay(0.6f),
            Actions.run(this::processNextAction) // Lanjut cari kartu lain untuk di-floop
        ));
    }

    private void finishTurn() {
        Gdx.app.log("BotController", "=== Fase Main O.M.E.G.A Selesai ===");
        screen.uiManager.updatePhaseLabel("BATTLE PHASE", Color.ORANGE);

        CombatResolver.resolveBoardCombat(screen, false, () -> screen.phaseManager.startPlayerTurn());
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
                visualIndex = i; break;
            }
        }
        return 327.5f + (visualIndex * 400f);
    }
}
