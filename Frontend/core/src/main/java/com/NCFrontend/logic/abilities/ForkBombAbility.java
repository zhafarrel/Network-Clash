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
        Gdx.app.log("Script", "FORK BOMB DIMAINKAN!");
        boolean isAnyKilled = false;

        // 1. CEK KEPEMILIKAN: Apakah kartu ini dimainkan oleh Pemain atau AI Musuh?
        boolean isPlayerCasting = screen.phaseManager.currentPhase == com.NCFrontend.managers.GamePhaseManager.GamePhase.PLAYER_MAIN;

        // 2. TENTUKAN TARGET PAPAN SECARA DINAMIS
        // Jika pemain yang mainkan, targetnya enemyActiveCards. Jika AI yang mainkan, targetnya activeCards.
        ObjectMap<String, CardActor> targetBoard = isPlayerCasting ? screen.enemyActiveCards : screen.activeCards;

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

        // 3. BERIKAN REWARD RAM KE PIHAK YANG BENAR
        if (isAnyKilled) {
            if (isPlayerCasting) {
                screen.playerProfile.currentRam = Math.min(screen.playerProfile.currentRam + 1, screen.playerProfile.maxRam);
                Gdx.app.log("Script", "FORK BOMB membunuh target! PEMAIN mendapat kembali +1 RAM.");
            } else {
                screen.enemyProfile.currentRam = Math.min(screen.enemyProfile.currentRam + 1, screen.enemyProfile.maxRam);
                Gdx.app.log("Script", "FORK BOMB membunuh target! MUSUH mendapat kembali +1 RAM.");
            }
        }
    }
}
