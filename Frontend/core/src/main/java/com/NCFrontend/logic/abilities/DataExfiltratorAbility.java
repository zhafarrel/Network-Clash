package com.NCFrontend.logic.abilities;

import com.NCFrontend.logic.CardAbility;
import com.NCFrontend.models.ScriptData;
import com.NCFrontend.screens.GameplayScreen;
import com.NCFrontend.ui.CardActor;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;

public class DataExfiltratorAbility implements CardAbility {
    @Override
    public void onFloop(CardActor owner, GameplayScreen screen) {
        boolean isPlayer = screen.activeCards.containsValue(owner, true);
        com.badlogic.gdx.utils.Array<CardActor> targetHand = isPlayer ? screen.enemyHand : screen.hand;

        // Saring hanya kartu Script dari tangan musuh
        com.badlogic.gdx.utils.Array<CardActor> scriptsInHand = new com.badlogic.gdx.utils.Array<>();
        for (CardActor c : targetHand) {
            if (c.getData() instanceof ScriptData) scriptsInHand.add(c);
        }

        if (scriptsInHand.size > 0) {
            CardActor discardedCard = scriptsInHand.random(); // Pilih Script acak
            targetHand.removeValue(discardedCard, true);

            discardedCard.addAction(Actions.sequence(
                Actions.parallel(Actions.moveBy(0, -100, 0.4f), Actions.fadeOut(0.4f), Actions.rotateBy(90, 0.4f)),
                Actions.removeActor()
            ));

            if (!isPlayer) screen.updateHandPositions(); // Rapikan tangan pemain
            Gdx.app.log("Skill", "BANDWIDTH THEFT! Berhasil mencuri Script musuh: " + discardedCard.getData().name);
        } else {
            Gdx.app.log("Skill", "BANDWIDTH THEFT Gagal: Tangan musuh tidak memiliki kartu Script.");
        }
    }
}
