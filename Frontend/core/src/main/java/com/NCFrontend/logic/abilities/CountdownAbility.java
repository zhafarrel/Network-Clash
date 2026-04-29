package com.NCFrontend.logic.abilities;

import com.NCFrontend.logic.CardAbility;
import com.NCFrontend.logic.CombatResolver;
import com.NCFrontend.screens.GameplayScreen;
import com.NCFrontend.ui.CardActor;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.utils.ObjectMap;

public class CountdownAbility implements CardAbility {
    private ObjectMap<CardActor, Integer> turnCounters = new ObjectMap<>();

    @Override
    public void onTurnEnd(CardActor owner, GameplayScreen screen) {
        int currentTurn = turnCounters.containsKey(owner) ? turnCounters.get(owner) : 0;
        currentTurn++;
        turnCounters.put(owner, currentTurn);

        boolean isPlayer = screen.activeCards.containsValue(owner, true);
        ObjectMap<String, CardActor> myBoard = isPlayer ? screen.activeCards : screen.enemyActiveCards;
        ObjectMap<String, CardActor> enemyBoard = isPlayer ? screen.enemyActiveCards : screen.activeCards;

        String myLane = null;
        for (ObjectMap.Entry<String, CardActor> entry : myBoard.entries()) {
            if (entry.value == owner) myLane = entry.key;
        }

        if (currentTurn >= 2 && myLane != null) {
            Gdx.app.log("Skill", "COUNTDOWN SELESAI! Logic Bomb meledak!");

            // Cek apakah ada musuh di depannya
            if (enemyBoard.containsKey(myLane)) {
                CardActor enemy = enemyBoard.get(myLane);
                int hp = CombatResolver.getHp(enemy.getData()) - 6;
                CombatResolver.setHp(enemy.getData(), hp);

                if (hp <= 0) {
                    if (enemy.getData().abilities != null) {
                        for (CardAbility ability : enemy.getData().abilities) ability.onDeath(enemy, screen);
                    }
                    enemy.addAction(Actions.sequence(Actions.scaleTo(0,0,0.3f), Actions.removeActor()));
                    enemyBoard.remove(myLane);
                }
            } else {
                // Tembus ke sistem utama jika kosong
                if (isPlayer) screen.enemyProfile.takeDamage(6);
                else screen.playerProfile.takeDamage(6);
                screen.uiManager.updateHP();
            }

            // Layar bergetar dahsyat
            screen.stage.getRoot().addAction(Actions.sequence(
                Actions.moveBy(40, -40, 0.05f, Interpolation.bounceOut), Actions.moveBy(-80, 80, 0.05f), Actions.moveTo(0, 0, 0.05f)
            ));

            // Bunuh diri (Hancur)
            if (owner.getData().abilities != null) {
                for (CardAbility ability : owner.getData().abilities) ability.onDeath(owner, screen);
            }
            owner.addAction(Actions.sequence(Actions.scaleTo(0,0,0.3f), Actions.removeActor()));
            myBoard.remove(myLane);
            turnCounters.remove(owner);
        } else {
            Gdx.app.log("Skill", "COUNTDOWN... Logic Bomb siap meledak 1 giliran lagi.");
            owner.addAction(Actions.sequence(Actions.color(Color.RED, 0.2f), Actions.color(Color.WHITE, 0.2f)));
        }
    }
}
