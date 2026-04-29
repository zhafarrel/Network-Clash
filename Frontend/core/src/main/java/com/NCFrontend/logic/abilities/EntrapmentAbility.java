package com.NCFrontend.logic.abilities;

import com.NCFrontend.logic.CardAbility;
import com.NCFrontend.screens.GameplayScreen;
import com.NCFrontend.ui.CardActor;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.utils.ObjectMap;

public class EntrapmentAbility implements CardAbility {
    @Override
    public void onDeath(CardActor owner, GameplayScreen screen) {
        boolean isPlayer = screen.activeCards.containsValue(owner, true);
        ObjectMap<String, CardActor> myBoard = isPlayer ? screen.activeCards : screen.enemyActiveCards;
        ObjectMap<String, CardActor> enemyBoard = isPlayer ? screen.enemyActiveCards : screen.activeCards;

        String deadLane = null;
        for (ObjectMap.Entry<String, CardActor> entry : myBoard.entries()) {
            if (entry.value == owner) deadLane = entry.key;
        }

        // Karena serangan selalu satu jalur (Lane), kita bisa langsung mencari penyerangnya di jalur yang sama
        if (deadLane != null && enemyBoard.containsKey(deadLane)) {
            CardActor attacker = enemyBoard.get(deadLane);

            // STUN Sang Penyerang karena masuk ke dalam jebakan!
            attacker.isStunned = true;

            Gdx.app.log("Skill", "ENTRAPMENT! Honeypot Decoy hancur dan berhasil men-Stun " + attacker.getData().name);

            attacker.addAction(Actions.sequence(
                Actions.color(Color.YELLOW, 0.3f),
                Actions.color(Color.LIGHT_GRAY, 0.3f)
            ));
        }
    }
}
