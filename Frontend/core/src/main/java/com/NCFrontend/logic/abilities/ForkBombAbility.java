package com.NCFrontend.logic.abilities;

import com.NCFrontend.logic.CardAbility;
import com.NCFrontend.logic.CombatResolver;
import com.NCFrontend.screens.GameplayScreen;
import com.NCFrontend.ui.CardActor;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.utils.ObjectMap;

public class ForkBombAbility implements CardAbility {
    @Override
    public void onPlayScript(CardActor owner, String targetLane, GameplayScreen screen) {
        Gdx.app.log("Script", "FORK BOMB! Menyapu bersih papan Sysadmin dengan 1 Damage.");
        boolean isAnyKilled = false;

        // Targetnya selalu papan Sysadmin (Pemain)
        ObjectMap<String, CardActor> targetBoard = screen.activeCards;

        // Salin ke array agar aman saat menghapus kartu yang mati dari ObjectMap
        com.badlogic.gdx.utils.Array<CardActor> targets = new com.badlogic.gdx.utils.Array<>();
        for (CardActor c : targetBoard.values()) targets.add(c);

        for (CardActor target : targets) {
            int hp = CombatResolver.getHp(target.getData()) - 1;
            CombatResolver.setHp(target.getData(), hp);

            // Efek bergetar merah
            target.addAction(Actions.sequence(
                Actions.color(Color.RED, 0.1f), Actions.moveBy(5,0,0.05f), Actions.moveBy(-10,0,0.05f), Actions.color(Color.WHITE, 0.1f)
            ));

            // Jika ada yang mati akibat Fork Bomb
            if (hp <= 0) {
                isAnyKilled = true;
                if (target.getData().abilities != null) {
                    for (CardAbility ability : target.getData().abilities) ability.onDeath(target, screen);
                }
                target.addAction(Actions.sequence(Actions.scaleTo(0,0,0.3f), Actions.removeActor()));

                String laneToRemove = null;
                for (ObjectMap.Entry<String, CardActor> entry : targetBoard.entries()) {
                    if (entry.value == target) laneToRemove = entry.key;
                }
                if (laneToRemove != null) targetBoard.remove(laneToRemove);
            }
        }

        // Reward +1 RAM jika ada yang tewas
        if (isAnyKilled) {
            screen.enemyProfile.currentRam = Math.min(screen.enemyProfile.currentRam + 1, screen.enemyProfile.maxRam);
            Gdx.app.log("Script", "FORK BOMB membunuh target! O.M.E.G.A mendapat kembali +1 RAM.");
        }
    }
}
