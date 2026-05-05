package com.NCFrontend.managers;

import com.NCFrontend.screens.GameplayScreen;
import com.NCFrontend.ai.BotController;
import com.NCFrontend.ui.CardActor;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;

public class EnemyAIManager {
    private GameplayScreen screen;
    private BotController botController;
    private boolean isFirstTurn = true;

    public EnemyAIManager(GameplayScreen screen) {
        this.screen = screen;
        this.botController = new BotController(screen);
    }

    public void startTurn() {
        Gdx.app.log("AI", "=== Giliran O.M.E.G.A Dimulai! ===");

        com.NCFrontend.models.PlayerData eProfile = screen.enemyProfile;
        if (eProfile.maxRam < 5) eProfile.maxRam = 5;
        eProfile.currentRam = eProfile.maxRam;

        Gdx.app.log("AI", "RAM Musuh diisi penuh: " + eProfile.currentRam + "/" + eProfile.maxRam);

        screen.stage.addAction(Actions.sequence(
            Actions.delay(1.0f),
            Actions.run(() -> {
                // Draw Kartu
                if (isFirstTurn) {
                    isFirstTurn = false;
                    for (int i = 0; i < 5; i++) drawEnemyCard();
                } else {
                    drawEnemyCard();
                }

                // Serahkan seluruh komando pertempuran ke BotController!
                botController.executeTurn();
            })
        ));
    }

    private void drawEnemyCard() {
        if (screen.enemyDeck.size > 0 && screen.enemyHand.size < 7) {
            CardActor card = screen.enemyDeck.pop();
            card.setVisible(true);
            card.isFaceUp = false;
            screen.enemyHand.add(card);
            screen.interactionHandler.setupEnemyInspect(card);
            Gdx.app.log("AI", "Menarik kartu...");
        }
    }
}
