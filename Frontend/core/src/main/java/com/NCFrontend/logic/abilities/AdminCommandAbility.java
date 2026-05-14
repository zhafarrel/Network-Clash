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

public class AdminCommandAbility implements CardAbility {
    private ProgramData buffedTargetData = null;

    @Override
    public void onFloop(CardActor owner, GameplayScreen screen) {
        boolean isPlayer = screen.activeCards.containsValue(owner, true);
        ObjectMap<String, CardActor> board = isPlayer ? screen.activeCards : screen.enemyActiveCards;

        CardActor targetAlly = null;
        for (CardActor ally : board.values()) {
            if (ally != owner) {
                targetAlly = ally;
                break; // Auto-target sekutu pertama yang ditemukan
            }
        }

        if (targetAlly != null && targetAlly.getData() instanceof ProgramData) {
            buffedTargetData = (ProgramData) targetAlly.getData();
            buffedTargetData.atk += 2;
            CombatResolver.setHp(buffedTargetData, CombatResolver.getHp(buffedTargetData) + 1);

            Gdx.app.log("Skill", "ADMIN COMMAND! Sysadmin Avatar memberikan Root Access kepada " + targetAlly.getData().name);

            targetAlly.addAction(Actions.sequence(
                Actions.color(Color.GOLD, 0.2f), Actions.color(Color.WHITE, 0.2f)
            ));
        } else {
            Gdx.app.log("Skill", "ADMIN COMMAND Gagal: Tidak ada bawahan untuk diberi perintah.");
        }
    }

    @Override
    public void onTurnEnd(CardActor owner, GameplayScreen screen) {
        if (buffedTargetData != null) {
            buffedTargetData.atk -= 2;
            // HP +1 is temporary, so if HP > 0 we reduce it, but we should just let it be a heal if we reduce max HP, but here we just reduce current HP.
            // Wait, "mendapatkan +2 ATK dan +1 HP hingga akhir giliran". Usually temp HP means max HP goes down, but we can just do:
            int newHp = CombatResolver.getHp(buffedTargetData) - 1;
            if (newHp > 0) {
                CombatResolver.setHp(buffedTargetData, newHp);
            }
            buffedTargetData = null;
            Gdx.app.log("Skill", "ADMIN COMMAND: Efek Root Access berakhir.");
        }
    }
}
