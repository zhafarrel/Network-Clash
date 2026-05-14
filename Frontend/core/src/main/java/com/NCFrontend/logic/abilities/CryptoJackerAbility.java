package com.NCFrontend.logic.abilities;

import com.NCFrontend.logic.CardAbility;
import com.NCFrontend.screens.GameplayScreen;
import com.NCFrontend.ui.CardActor;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;

public class CryptoJackerAbility implements CardAbility {
    @Override
    public void onTurnEnd(CardActor owner, GameplayScreen screen) {
        boolean isPlayer = screen.activeCards.containsValue(owner, true);

        // Curi 1 RAM dari musuh ke pemilik kartu
        if (isPlayer) {
            if (screen.enemyProfile.currentRam > 0) {
                screen.enemyProfile.currentRam -= 1;
                screen.playerProfile.currentRam = Math.min(screen.playerProfile.currentRam + 1, screen.playerProfile.maxRam);
                Gdx.app.log("Skill", "RESOURCE THEFT! Crypto-Jacker mencuri 1 RAM dari O.M.E.G.A.");
                owner.addAction(Actions.sequence(Actions.color(Color.GOLD, 0.2f), Actions.color(Color.WHITE, 0.2f)));
            }
        } else {
            if (screen.playerProfile.currentRam > 0) {
                screen.playerProfile.currentRam -= 1;
                screen.enemyProfile.currentRam = Math.min(screen.enemyProfile.currentRam + 1, screen.enemyProfile.maxRam);
                Gdx.app.log("Skill", "RESOURCE THEFT! Crypto-Jacker mencuri 1 RAM dari Sysadmin.");
                owner.addAction(Actions.sequence(Actions.color(Color.GOLD, 0.2f), Actions.color(Color.WHITE, 0.2f)));
            }
        }
        screen.uiManager.updateRamLabel(screen.playerProfile.currentRam, screen.playerProfile.maxRam);
    }
}
