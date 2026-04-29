package com.NCFrontend.logic.abilities;

import com.NCFrontend.logic.CardAbility;
import com.NCFrontend.models.ProgramData;
import com.NCFrontend.models.ScriptData;
import com.NCFrontend.screens.GameplayScreen;
import com.NCFrontend.ui.CardActor;
import com.badlogic.gdx.Gdx;

public class DataInterceptAbility implements CardAbility {
    @Override
    public void onFloop(CardActor owner, GameplayScreen screen) {
        boolean isPlayer = screen.activeCards.containsValue(owner, true);

        // Cek deck musuh (atau deck pemain jika musuh yang memakai)
        com.badlogic.gdx.utils.Array<CardActor> targetDeck = isPlayer ? screen.enemyDeck : screen.deck;

        if (targetDeck.size > 0) {
            CardActor topCard = targetDeck.peek(); // Intip kartu paling atas tanpa mengambilnya

            Gdx.app.log("Skill", "DATA INTERCEPT! " + owner.getData().name + " mengintip kartu teratas: " + topCard.getData().name);

            // Jika kartu teratas adalah Script, dapatkan +1 ATK
            if (topCard.getData() instanceof ScriptData) {
                if (owner.getData() instanceof ProgramData) {
                    ((ProgramData) owner.getData()).atk += 1;
                    Gdx.app.log("Skill", "Kartu adalah Script! " + owner.getData().name + " mendapat permanen +1 ATK.");

                    // Efek visual membesar sedikit
                    owner.addAction(com.badlogic.gdx.scenes.scene2d.actions.Actions.sequence(
                        com.badlogic.gdx.scenes.scene2d.actions.Actions.scaleTo(1.1f, 1.1f, 0.1f),
                        com.badlogic.gdx.scenes.scene2d.actions.Actions.scaleTo(1.0f, 1.0f, 0.1f)
                    ));
                }
            }
        } else {
            Gdx.app.log("Skill", "DATA INTERCEPT gagal, deck target sudah kosong.");
        }
    }
}
