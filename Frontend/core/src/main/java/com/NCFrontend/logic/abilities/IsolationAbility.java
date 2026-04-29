package com.NCFrontend.logic.abilities;

import com.NCFrontend.logic.CardAbility;
import com.NCFrontend.screens.GameplayScreen;
import com.NCFrontend.ui.CardActor;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;

public class IsolationAbility implements CardAbility {
    @Override
    public CardActor onTargeting(CardActor owner, CardActor attacker, CardActor originalTarget, GameplayScreen screen) {
        // Jika kartu Sandbox ini menjadi target serangan musuh
        if (originalTarget == owner && attacker != owner) {
            attacker.isSilenced = true; // Lumpuhkan semua skill pasif/aktifnya!

            Gdx.app.log("Skill", "ISOLATION! Sandbox Environment menetralisir kemampuan " + attacker.getData().name);

            attacker.addAction(Actions.sequence(
                Actions.color(Color.PURPLE, 0.2f), Actions.color(Color.WHITE, 0.2f)
            ));
        }
        return originalTarget;
    }
}
