package com.NCFrontend.logic.abilities;

import com.NCFrontend.logic.CardAbility;
import com.NCFrontend.logic.CombatResolver;
import com.NCFrontend.screens.GameplayScreen;
import com.NCFrontend.ui.CardActor;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.ObjectMap;

public class FloodNetworkAbility implements CardAbility {
    @Override
    public void onFloop(CardActor owner, GameplayScreen screen) {
        boolean isPlayer = screen.activeCards.containsValue(owner, true);
        ObjectMap<String, CardActor> targetBoard = isPlayer ? screen.enemyActiveCards : screen.activeCards;

        Gdx.app.log("Skill", "FLOOD NETWORK AKTIF! DDoS Swarm meratakan damage.");

        // Loop ke semua kartu di meja lawan
        for (CardActor target : targetBoard.values()) {
            int currentHp = CombatResolver.getHp(target.getData());
            CombatResolver.setHp(target.getData(), currentHp - 1);

            // Animasi bergetar kecil saat terkena ombak DDoS
            target.addAction(com.badlogic.gdx.scenes.scene2d.actions.Actions.sequence(
                com.badlogic.gdx.scenes.scene2d.actions.Actions.moveBy(5, 0, 0.05f),
                com.badlogic.gdx.scenes.scene2d.actions.Actions.moveBy(-10, 0, 0.05f),
                com.badlogic.gdx.scenes.scene2d.actions.Actions.moveBy(5, 0, 0.05f)
            ));

        }
    }
}
