package com.NCFrontend.logic.abilities;

import com.NCFrontend.logic.CardAbility;
import com.NCFrontend.screens.GameplayScreen;
import com.NCFrontend.ui.CardActor;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.utils.ObjectMap;

public class ClearCacheAbility implements CardAbility {
    @Override
    public void onPlayScript(CardActor owner, String targetLane, GameplayScreen screen) {
        boolean isPlayer = screen.hand.contains(owner, true) || owner.isOnBoard; // Deteksi siapa pemakainya
        ObjectMap<String, CardActor> myBoard = isPlayer ? screen.activeCards : screen.enemyActiveCards;

        Gdx.app.log("Script", "CLEAR CACHE! Membersihkan semua debuff dan stun dari kawan.");

        // Loop semua kartu kawan di papan
        for (CardActor ally : myBoard.values()) {
            if (ally.isStunned) {
                ally.isStunned = false; // Hapus Stun

                // Efek visual pembersihan (warna biru/putih berkedip)
                ally.addAction(Actions.sequence(
                    Actions.color(Color.CYAN, 0.15f),
                    Actions.moveBy(0, 10, 0.1f), Actions.moveBy(0, -10, 0.1f),
                    Actions.color(Color.WHITE, 0.15f)
                ));
            }
        }
    }
}
