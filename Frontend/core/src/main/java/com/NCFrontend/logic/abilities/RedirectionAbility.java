package com.NCFrontend.logic.abilities;

import com.NCFrontend.logic.CardAbility;
import com.NCFrontend.models.ProgramData;
import com.NCFrontend.screens.GameplayScreen;
import com.NCFrontend.ui.CardActor;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;

public class RedirectionAbility implements CardAbility {
    @Override
    public CardActor onTargeting(CardActor owner, CardActor attacker, CardActor originalTarget, GameplayScreen screen) {
        // Jika kartu ini adalah target serangan
        if (originalTarget == owner && attacker != owner) {
            if (attacker.getData() instanceof ProgramData) {
                ProgramData atkData = (ProgramData) attacker.getData();

                // Turunkan ATK penyerang
                if (atkData.atk > 0) {
                    atkData.atk -= 1;
                    Gdx.app.log("Skill", "REDIRECTION AKTIF! Mobile Proxy melemahkan senjata " + attacker.getData().name + " menjadi " + atkData.atk + " ATK.");

                    attacker.addAction(Actions.sequence(
                        Actions.color(Color.PURPLE, 0.2f), Actions.color(Color.WHITE, 0.2f)
                    ));
                }
            }
        }
        return originalTarget;
    }
}
