package com.NCFrontend.logic.abilities;

import com.NCFrontend.logic.CardAbility;
import com.NCFrontend.logic.CombatResolver;
import com.NCFrontend.models.ProgramData;
import com.NCFrontend.screens.GameplayScreen;
import com.NCFrontend.ui.CardActor;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;

public class SystemPatchAbility implements CardAbility {
    @Override
    public void onPlayScript(CardActor owner, String targetLane, GameplayScreen screen) {
        // Karena ini kartu Sysadmin, kita cari teman di papan kita sendiri
        if (screen.activeCards.containsKey(targetLane)) {
            CardActor target = screen.activeCards.get(targetLane);

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
