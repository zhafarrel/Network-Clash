package com.NCFrontend.logic.abilities;

import com.NCFrontend.logic.CardAbility;
import com.NCFrontend.screens.GameplayScreen;
import com.NCFrontend.ui.CardActor;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.utils.ObjectMap;

public class HttpFilterAbility implements CardAbility {
    @Override
    public void onCardDeployed(CardActor owner, CardActor deployedCard, String lane, GameplayScreen screen) {
        boolean isOwnerPlayer = screen.activeCards.containsValue(owner, true);
        boolean isDeployedPlayer = screen.activeCards.containsValue(deployedCard, true);

        ObjectMap<String, CardActor> myBoard = isOwnerPlayer ? screen.activeCards : screen.enemyActiveCards;
        String myLane = null;
        for (ObjectMap.Entry<String, CardActor> entry : myBoard.entries()) {
            if (entry.value == owner) myLane = entry.key;
        }

        // Jika musuh menaruh kartu tepat di Lane tempat Web App Firewall berada
        if (isOwnerPlayer != isDeployedPlayer && lane.equals(myLane)) {
            Gdx.app.log("Skill", "HTTP FILTER! Web App Firewall menagih pajak +1 RAM.");

            if (isDeployedPlayer) {
                screen.playerProfile.currentRam = Math.max(0, screen.playerProfile.currentRam - 1);
                screen.uiManager.updateRamLabel(screen.playerProfile.currentRam, screen.playerProfile.maxRam);
            } else {
                screen.enemyProfile.currentRam = Math.max(0, screen.enemyProfile.currentRam - 1);
            }

            owner.addAction(Actions.sequence(
                Actions.color(Color.CYAN, 0.2f), Actions.color(Color.WHITE, 0.2f)
            ));
        }
    }
}
