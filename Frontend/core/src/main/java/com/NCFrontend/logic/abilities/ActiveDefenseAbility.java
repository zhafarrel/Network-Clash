package com.NCFrontend.logic.abilities;

import com.NCFrontend.logic.CardAbility;
import com.NCFrontend.logic.CombatResolver;
import com.NCFrontend.screens.GameplayScreen;
import com.NCFrontend.ui.CardActor;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;

public class ActiveDefenseAbility implements CardAbility {
    @Override
    public void onCardDeployed(CardActor owner, CardActor deployedCard, String lane, GameplayScreen screen) {
        boolean isOwnerPlayer = screen.activeCards.containsValue(owner, true);
        boolean isDeployedPlayer = screen.activeCards.containsValue(deployedCard, true);

        // Jika musuh meletakkan kartu di DARK NODE
        if (isOwnerPlayer != isDeployedPlayer && lane.equalsIgnoreCase("Dark Node")) {
            int hp = CombatResolver.getHp(deployedCard.getData()) - 1;
            CombatResolver.setHp(deployedCard.getData(), hp);

            Gdx.app.log("Skill", "ACTIVE DEFENSE! Threat Hunter menembak Malware di Dark Node.");

            deployedCard.addAction(Actions.sequence(
                Actions.color(Color.RED, 0.1f),
                Actions.moveBy(5,0,0.05f), Actions.moveBy(-10,0,0.05f),
                Actions.color(Color.WHITE, 0.1f)
            ));
        }
    }
}
