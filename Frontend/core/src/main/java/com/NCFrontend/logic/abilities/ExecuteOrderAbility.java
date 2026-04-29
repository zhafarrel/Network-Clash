package com.NCFrontend.logic.abilities;

import com.NCFrontend.logic.CardAbility;
import com.NCFrontend.models.ProgramData;
import com.NCFrontend.screens.GameplayScreen;
import com.NCFrontend.ui.CardActor;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.ObjectMap;

public class ExecuteOrderAbility implements CardAbility {
    @Override
    public void onFloop(CardActor owner, GameplayScreen screen) {
        boolean isPlayer = screen.activeCards.containsValue(owner, true);
        ObjectMap<String, CardActor> allyBoard = isPlayer ? screen.activeCards : screen.enemyActiveCards;

        Gdx.app.log("Skill", "EXECUTE ORDER! C2 Server memperkuat semua sekutu.");

        for (CardActor ally : allyBoard.values()) {
            if (ally.getData() instanceof ProgramData) {
                ProgramData pData = (ProgramData) ally.getData();
                pData.atk += 1; // Beri permanen +1 ATK

                // Tambahkan efek membesar sedikit sebagai tanda buff
                ally.addAction(com.badlogic.gdx.scenes.scene2d.actions.Actions.sequence(
                    com.badlogic.gdx.scenes.scene2d.actions.Actions.scaleTo(1.1f, 1.1f, 0.1f),
                    com.badlogic.gdx.scenes.scene2d.actions.Actions.scaleTo(1.0f, 1.0f, 0.1f)
                ));
            }
        }
    }
}
