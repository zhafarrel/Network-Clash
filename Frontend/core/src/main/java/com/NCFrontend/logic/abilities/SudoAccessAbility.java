package com.NCFrontend.logic.abilities;

import com.NCFrontend.logic.CardAbility;
import com.NCFrontend.models.ProgramData;
import com.NCFrontend.screens.GameplayScreen;
import com.NCFrontend.ui.CardActor;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;

public class SudoAccessAbility implements CardAbility {
    @Override
    public void onAnyCardDeath(CardActor owner, CardActor deadCard, GameplayScreen screen) {
        // Cek apakah kartu yang mati adalah milik Sysadmin (Pemain)
        boolean isDeadCardPlayer = screen.activeCards.containsValue(deadCard, true) || screen.hand.contains(deadCard, true);

        if (isDeadCardPlayer) {
            if (owner.getData() instanceof ProgramData) {
                ProgramData pData = (ProgramData) owner.getData();
                pData.atk += 1; // Snowballing +1 ATK

                Gdx.app.log("Skill", "SUDO ACCESS! Program Sysadmin gugur, Privilege Escalator mendapat +1 ATK.");

                // Efek visual membesar dan bersinar merah sebagai tanda mengamuk
                owner.addAction(Actions.sequence(
                    Actions.color(Color.RED, 0.1f),
                    Actions.scaleTo(1.2f, 1.2f, 0.1f),
                    Actions.scaleTo(1.0f, 1.0f, 0.1f),
                    Actions.color(Color.WHITE, 0.1f)
                ));
            }
        }
    }
}
