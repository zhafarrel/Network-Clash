package com.NCFrontend.logic.abilities;

import com.NCFrontend.logic.CardAbility;
import com.NCFrontend.logic.CombatResolver;
import com.NCFrontend.models.ProgramData;
import com.NCFrontend.screens.GameplayScreen;
import com.NCFrontend.ui.CardActor;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.utils.ObjectMap;

public class SystemPatchAbility implements CardAbility {
    @Override
    public void onPlayScript(CardActor owner, String targetLane, GameplayScreen screen) {
        boolean isPlayer = screen.phaseManager.currentPhase == com.NCFrontend.managers.GamePhaseManager.GamePhase.PLAYER_MAIN;
        ObjectMap<String, CardActor> myBoard = isPlayer ? screen.activeCards : screen.enemyActiveCards;

        if (myBoard.containsKey(targetLane)) {
            CardActor target = myBoard.get(targetLane);

            if (target.getData() instanceof ProgramData) {
                ProgramData pData = (ProgramData) target.getData();

                // Beri +1 ATK
                pData.atk += 1;

                // Beri +2 HP
                int currentHp = CombatResolver.getHp(pData);
                CombatResolver.setHp(pData, currentHp + 2);

                Gdx.app.log("Script", "SYSTEM PATCH! " + target.getData().name + " mendapat +2 HP & +1 ATK.");

                // Efek visual Healing/Buff
                target.addAction(Actions.sequence(
                    Actions.color(Color.GREEN, 0.1f),
                    Actions.scaleTo(1.2f, 1.2f, 0.1f),
                    Actions.scaleTo(1.0f, 1.0f, 0.1f),
                    Actions.color(Color.WHITE, 0.2f)
                ));
            }
        } else {
            Gdx.app.log("Script", "SYSTEM PATCH Gagal: Tidak ada kawan di " + targetLane);
        }
    }
}
