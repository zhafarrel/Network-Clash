package com.NCFrontend.logic.abilities;

import com.NCFrontend.logic.CardAbility;
import com.NCFrontend.screens.GameplayScreen;
import com.NCFrontend.ui.CardActor;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;

public class DataBreachAbility implements CardAbility {
    @Override
    public void onAnyCardDeath(CardActor owner, CardActor deadCard, GameplayScreen screen) {
        boolean isOwnerPlayer = screen.activeCards.containsValue(owner, true);
        boolean isDeadCardPlayer = screen.activeCards.containsValue(deadCard, true);

        // Jika yang mati adalah pihak lawan, SQLi Payload akan mengekstrak datanya menjadi RAM
        if (isOwnerPlayer != isDeadCardPlayer) {
            Gdx.app.log("Skill", "DATA BREACH! Musuh hancur, SQLi Payload mencuri data untuk +1 RAM.");

            if (isOwnerPlayer) {
                screen.playerProfile.currentRam = Math.min(screen.playerProfile.currentRam + 1, screen.playerProfile.maxRam);
                screen.uiManager.updateRamLabel(screen.playerProfile.currentRam, screen.playerProfile.maxRam);
            } else {
                screen.enemyProfile.currentRam = Math.min(screen.enemyProfile.currentRam + 1, screen.enemyProfile.maxRam);
            }

            // Efek visual sedotan hijau
            owner.addAction(Actions.sequence(
                Actions.color(Color.GREEN, 0.1f),
                Actions.scaleTo(1.1f, 1.1f, 0.1f), Actions.scaleTo(1.0f, 1.0f, 0.1f),
                Actions.color(Color.WHITE, 0.1f)
            ));
        }
    }
}
