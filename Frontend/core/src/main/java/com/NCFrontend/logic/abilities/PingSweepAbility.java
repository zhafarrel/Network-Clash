package com.NCFrontend.logic.abilities;

import com.NCFrontend.logic.CardAbility;
import com.NCFrontend.logic.CombatResolver;
import com.NCFrontend.screens.GameplayScreen;
import com.NCFrontend.ui.CardActor;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.utils.ObjectMap;
import com.badlogic.gdx.utils.Array;

public class PingSweepAbility implements CardAbility {
    @Override
    public void onFloop(CardActor owner, GameplayScreen screen) {
        boolean isPlayer = screen.activeCards.containsValue(owner, true);
        ObjectMap<String, CardActor> enemyBoard = isPlayer ? screen.enemyActiveCards : screen.activeCards;

        if (enemyBoard.size > 0) {
            // Kumpulkan semua musuh yang ada di papan ke dalam array
            Array<CardActor> enemies = new Array<>();
            for (CardActor enemy : enemyBoard.values()) {
                enemies.add(enemy);
            }

            // Sniper memilih musuh acak di lane mana saja
            CardActor targetEnemy = enemies.random();
            int newHp = CombatResolver.getHp(targetEnemy.getData()) - 2;
            CombatResolver.setHp(targetEnemy.getData(), newHp);

            Gdx.app.log("Skill", "PING SWEEP! Sniper menembak jitu " + targetEnemy.getData().name + " sebesar 2 Damage.");

            // Animasi target terkena peluru laser
            targetEnemy.addAction(Actions.sequence(
                Actions.color(Color.RED, 0.1f),
                Actions.moveBy(15, 0, 0.05f), Actions.moveBy(-30, 0, 0.05f), Actions.moveBy(15, 0, 0.05f),
                Actions.color(Color.WHITE, 0.1f)
            ));

            // Jika tembakan jitu ini membunuh targetnya
            if (newHp <= 0) {
                if (targetEnemy.getData().abilities != null) {
                    for (CardAbility ability : targetEnemy.getData().abilities) ability.onDeath(targetEnemy, screen);
                }
                targetEnemy.addAction(Actions.sequence(Actions.scaleTo(0,0,0.3f), Actions.removeActor()));

                // Cari dan hapus dari papan
                String targetLane = null;
                for (ObjectMap.Entry<String, CardActor> entry : enemyBoard.entries()) {
                    if (entry.value == targetEnemy) targetLane = entry.key;
                }
                if (targetLane != null) enemyBoard.remove(targetLane);
            }

        } else {
            Gdx.app.log("Skill", "PING SWEEP gagal: Area bersih, tidak ada target untuk di-snipe.");
        }
    }
}
