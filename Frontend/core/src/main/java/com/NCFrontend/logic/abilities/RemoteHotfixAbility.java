package com.NCFrontend.logic.abilities;

import com.NCFrontend.logic.CardAbility;
import com.NCFrontend.logic.CombatResolver;
import com.NCFrontend.screens.GameplayScreen;
import com.NCFrontend.ui.CardActor;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.utils.ObjectMap;

public class RemoteHotfixAbility implements CardAbility {
    @Override
    public void onCardDeployed(CardActor owner, CardActor deployedCard, String lane, GameplayScreen screen) {
        // Pastikan efek ini hanya menyala saat dirinya sendiri yang diturunkan
        if (owner == deployedCard) {
            boolean isPlayer = screen.activeCards.containsValue(owner, true);
            ObjectMap<String, CardActor> board = isPlayer ? screen.activeCards : screen.enemyActiveCards;

            // Cari teman yang paling butuh disembuhkan
            CardActor lowestHpAlly = null;
            int minHp = 999;
            for (CardActor ally : board.values()) {
                if (ally != owner) {
                    int hp = CombatResolver.getHp(ally.getData());
                    if (hp < minHp) {
                        minHp = hp;
                        lowestHpAlly = ally;
                    }
                }
            }

            if (lowestHpAlly != null) {
                CombatResolver.setHp(lowestHpAlly.getData(), minHp + 2);
                Gdx.app.log("Skill", "REMOTE HOTFIX! Memulihkan 2 HP milik " + lowestHpAlly.getData().name);

                lowestHpAlly.addAction(Actions.sequence(
                    Actions.color(Color.GREEN, 0.2f), Actions.color(Color.WHITE, 0.2f)
                ));
            }
        }
    }
}
