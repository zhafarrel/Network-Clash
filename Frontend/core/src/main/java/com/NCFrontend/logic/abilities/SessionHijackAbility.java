package com.NCFrontend.logic.abilities;

import com.NCFrontend.logic.CardAbility;
import com.NCFrontend.screens.GameplayScreen;
import com.NCFrontend.ui.CardActor;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;

public class SessionHijackAbility implements CardAbility {
    @Override
    public void onFloop(CardActor owner, GameplayScreen screen) {
        boolean isPlayer = screen.activeCards.containsValue(owner, true);

        // Cari posisi Lane owner
        String[] lanes = {"Localhost", "Cloud Storage", "DMZ", "Dark Node"};
        String myLane = null;

        com.badlogic.gdx.utils.ObjectMap<String, CardActor> myBoard = isPlayer ? screen.activeCards : screen.enemyActiveCards;
        com.badlogic.gdx.utils.ObjectMap<String, CardActor> enemyBoard = isPlayer ? screen.enemyActiveCards : screen.activeCards;

        for (String lane : lanes) {
            if (myBoard.get(lane) == owner) {
                myLane = lane;
                break;
            }
        }

        if (myLane != null && enemyBoard.containsKey(myLane)) {
            CardActor targetEnemy = enemyBoard.get(myLane);
            Gdx.app.log("Skill", "SESSION HIJACK! Membajak " + targetEnemy.getData().name);

            // Berikan damage ke bosnya sendiri
            if (isPlayer) {
                screen.enemyProfile.takeDamage(1);
            } else {
                screen.playerProfile.takeDamage(1);
            }

            screen.uiManager.updateHP();

            // Efek visual kartu musuh dipaksa menembak ke belakang
            targetEnemy.addAction(com.badlogic.gdx.scenes.scene2d.actions.Actions.sequence(
                com.badlogic.gdx.scenes.scene2d.actions.Actions.color(Color.PURPLE, 0.1f),
                com.badlogic.gdx.scenes.scene2d.actions.Actions.moveBy(0, isPlayer ? 20 : -20, 0.1f), // Tersentak mundur
                com.badlogic.gdx.scenes.scene2d.actions.Actions.moveBy(0, isPlayer ? -20 : 20, 0.1f),
                com.badlogic.gdx.scenes.scene2d.actions.Actions.color(Color.WHITE, 0.1f)
            ));
        } else {
            Gdx.app.log("Skill", "SESSION HIJACK gagal: Tidak ada musuh di depan.");
        }
    }
}
