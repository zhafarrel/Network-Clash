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
        try {
            if (currentState == BotState.PLAY_CARDS) {
                CardActor cardToPlay = strategy.getBestCardToPlay();

                if (cardToPlay != null) {
                    String targetLane = strategy.getBestLaneForCard(cardToPlay);

                    // --- PERBAIKAN ANTI-FREEZE / CRASH ---
                    // Jika tidak ada lane kosong (null), beralih ke Floop
                    if (targetLane == null || targetLane.trim().isEmpty()) {
                        Gdx.app.log("AI", "Lane Penuh! AI beralih ke Floop.");
                        currentState = BotState.FLOOP_CARDS;
                        processNextAction();
                        return;
                    }

                    playCard(cardToPlay, targetLane);
                    return;
                } else {
                    currentState = BotState.FLOOP_CARDS;
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
                    currentState = BotState.END_TURN;
                    processNextAction();
                    return;
                }
            }

            if (currentState == BotState.END_TURN) {
                finishTurn();
            }
        } catch (Exception e) {
            Gdx.app.log("BotController", "FATAL ERROR in processNextAction: " + e.getMessage());
            e.printStackTrace();
            // Failsafe: Paksa akhiri giliran jika error parah
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
            if (card.getStage() == null) screen.stage.addActor(card);
            card.toFront();
            card.addAction(Actions.sequence(
                Actions.moveTo(Gdx.graphics.getWidth() / 2f - 100, Gdx.graphics.getHeight() / 2f, 0.4f),
                Actions.delay(0.8f),
                Actions.run(() -> {
                    try {
                        if (card.getData().abilities != null) {
                            for (CardAbility ability : card.getData().abilities) ability.onPlayScript(card, targetLane, screen);
                        }
                    } catch (Exception e) {
                        Gdx.app.log("BotController", "Error in Script Ability: " + e.getMessage());
                    }
                }),
                Actions.parallel(Actions.scaleTo(0.1f, 0.1f, 0.3f), Actions.fadeOut(0.3f)),
                Actions.run(this::processNextAction),
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
            float targetY = 700f; // Sesuai kode terakhirmu

            if (card.getStage() == null) screen.stage.addActor(card);
            card.clearActions();
            card.addAction(Actions.sequence(
                Actions.parallel(
                    Actions.scaleTo(0.55f, 0.55f, 0.4f, Interpolation.pow3Out),
                    Actions.moveTo(targetX, targetY, 0.4f, Interpolation.pow3Out),
                    Actions.rotateTo(180f, 0.4f, Interpolation.pow3Out)
                ),
                Actions.delay(0.4f),
                Actions.run(() -> {
                    try {
                        com.badlogic.gdx.utils.Array<CardActor> enemiesToNotify = new com.badlogic.gdx.utils.Array<>();
                        for (CardActor c : screen.enemyActiveCards.values()) enemiesToNotify.add(c);
                        for (CardActor enemy : enemiesToNotify) {
                            if (enemy.getData().abilities != null) {
                                for (CardAbility ability : enemy.getData().abilities) ability.onCardDeployed(enemy, card, targetLane, screen);
                            }
                        }

                        com.badlogic.gdx.utils.Array<CardActor> alliesToNotify = new com.badlogic.gdx.utils.Array<>();
                        for (CardActor c : screen.activeCards.values()) alliesToNotify.add(c);
                        for (CardActor ally : alliesToNotify) {
                            if (ally.getData().abilities != null) {
                                for (CardAbility ability : ally.getData().abilities) ability.onCardDeployed(ally, card, targetLane, screen);
                            }
                        }
                    } catch (Exception e) {
                        Gdx.app.log("BotController", "Error notifying deployed abilities: " + e.getMessage());
                    } finally {
                        processNextAction();
                    }
                })
            ));
        }
    }

    private void executeFloop(final CardActor card) {
        Gdx.app.log("BotController", "O.M.E.G.A menekan tombol EXECUTE (Floop) pada: " + card.getData().name);
        card.isFlooped = true;

        screen.uiManager.showNotification("O.M.E.G.A EXECUTE:\n" + card.getData().description);

        card.addAction(Actions.sequence(
            Actions.rotateTo(90f, 0.4f, Interpolation.smooth),
            Actions.run(() -> {
                try {
                    if (card.getData().abilities != null) {
                        for (CardAbility ability : card.getData().abilities) ability.onFloop(card, screen);
                    }
                } catch (Exception e) {
                    Gdx.app.log("BotController", "Error in Floop Ability: " + e.getMessage());
                }
            }),
            Actions.delay(0.6f),
            Actions.run(this::processNextAction)
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
        if (lane == null) return 575f; // Perlindungan anti-crash

        int visualIndex = 0;
        for (int i = 0; i < screen.boardLanes.length; i++) {
            if (lane.equalsIgnoreCase(screen.boardLanes[i])) {
                visualIndex = i; break;
            }
        }
        if (visualIndex == 0) return 620f;
        if (visualIndex == 1) return 785f;
        if (visualIndex == 2) return 970f;
        if (visualIndex == 3) return 1125f;

        return 575f;
    }
}
