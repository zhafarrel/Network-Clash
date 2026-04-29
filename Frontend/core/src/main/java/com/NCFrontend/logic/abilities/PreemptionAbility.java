package com.NCFrontend.logic.abilities;

import com.NCFrontend.logic.CardAbility;
import com.NCFrontend.screens.GameplayScreen;
import com.NCFrontend.ui.CardActor;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.utils.ObjectMap;

public class PreemptionAbility implements CardAbility {
    @Override
    public void onFloop(CardActor owner, GameplayScreen screen) {
        boolean isPlayer = screen.activeCards.containsValue(owner, true);
        ObjectMap<String, CardActor> myBoard = isPlayer ? screen.activeCards : screen.enemyActiveCards;
        ObjectMap<String, CardActor> enemyBoard = isPlayer ? screen.enemyActiveCards : screen.activeCards;

        String myLane = null;
        for (ObjectMap.Entry<String, CardActor> entry : myBoard.entries()) {
            if (entry.value == owner) myLane = entry.key;
        }

        if (myLane != null && enemyBoard.containsKey(myLane)) {
            CardActor enemy = enemyBoard.get(myLane);

            // Bungkam (Silence) musuh di depannya!
            enemy.isSilenced = true;

            // Efek visual: Kartu musuh menjadi agak gelap / ungu pertanda dibungkam
            enemy.addAction(Actions.sequence(
                Actions.color(Color.PURPLE, 0.2f),
                Actions.moveBy(0, -5, 0.1f), Actions.moveBy(0, 5, 0.1f),
                Actions.color(Color.LIGHT_GRAY, 0.2f)
            ));

            Gdx.app.log("Skill", "PREEMPTION! Interrupt Handler membungkam Floop milik " + enemy.getData().name);
        } else {
            Gdx.app.log("Skill", "PREEMPTION gagal: Tidak ada musuh di depan.");
        }
    }
}
