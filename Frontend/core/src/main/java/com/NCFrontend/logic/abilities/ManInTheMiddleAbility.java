package com.NCFrontend.logic.abilities;

import com.NCFrontend.logic.CardAbility;
import com.NCFrontend.screens.GameplayScreen;
import com.NCFrontend.ui.CardActor;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.utils.ObjectMap;

public class ManInTheMiddleAbility implements CardAbility {
    @Override
    public void onPlayScript(CardActor owner, String targetLane, GameplayScreen screen) {
        boolean isPlayer = screen.hand.contains(owner, true);
        ObjectMap<String, CardActor> targetBoard = isPlayer ? screen.enemyActiveCards : screen.activeCards;

        if (targetBoard.containsKey(targetLane)) {
            CardActor target = targetBoard.get(targetLane);

            target.isStunned = true; // Beri status STUN

            Gdx.app.log("Script", "MAN-IN-THE-MIDDLE! Mengunci " + target.getData().name);

            // Efek visual Stun (berwarna kuning/abu-abu)
            target.addAction(Actions.sequence(
                Actions.color(Color.YELLOW, 0.2f),
                Actions.moveBy(5, 0, 0.05f), Actions.moveBy(-10, 0, 0.05f), Actions.moveBy(5, 0, 0.05f),
                Actions.color(Color.LIGHT_GRAY, 0.5f)
            ));
        } else {
            Gdx.app.log("Script", "MAN-IN-THE-MIDDLE Gagal: Tidak ada target di " + targetLane);
        }
    }
}
