package com.NCFrontend.logic.abilities;

import com.NCFrontend.logic.CardAbility;
import com.NCFrontend.screens.GameplayScreen;
import com.NCFrontend.ui.CardActor;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.utils.ObjectMap;

public class ThreadSyncAbility implements CardAbility {
    @Override
    public void onFloop(CardActor owner, GameplayScreen screen) {
        boolean isPlayer = screen.activeCards.containsValue(owner, true);
        ObjectMap<String, CardActor> myBoard = isPlayer ? screen.activeCards : screen.enemyActiveCards;
        ObjectMap<String, CardActor> enemyBoard = isPlayer ? screen.enemyActiveCards : screen.activeCards;

        String myLane = null;
        for (ObjectMap.Entry<String, CardActor> entry : myBoard.entries()) {
            if (entry.value == owner) myLane = entry.key;
        }

        // Cari musuh di Lane yang sama persis
        if (myLane != null && enemyBoard.containsKey(myLane)) {
            CardActor enemy = enemyBoard.get(myLane);

            // Kunci musuh tersebut!
            enemy.isStunned = true;

            // Efek visual kartu musuh diwarnai kuning/abu-abu sebentar sebagai tanda terkunci
            enemy.addAction(Actions.sequence(
                Actions.color(Color.YELLOW, 0.2f),
                Actions.moveBy(5, 0, 0.05f), Actions.moveBy(-10, 0, 0.05f), Actions.moveBy(5, 0, 0.05f),
                Actions.color(Color.LIGHT_GRAY, 0.5f) // Tetap abu-abu sampai gilirannya tiba
            ));

            Gdx.app.log("Skill", "THREAD SYNC! Mutex Lock berhasil mengunci " + enemy.getData().name);
        } else {
            Gdx.app.log("Skill", "THREAD SYNC gagal: Tidak ada ancaman di depan.");
        }
    }
}
