package com.NCFrontend.logic.abilities;

import com.NCFrontend.logic.CardAbility;
import com.NCFrontend.screens.GameplayScreen;
import com.NCFrontend.ui.CardActor;
import com.badlogic.gdx.Gdx;

public class CorruptBootAbility implements CardAbility {
    @Override
    public void onFloop(CardActor owner, GameplayScreen screen) {
        boolean isPlayer = screen.activeCards.containsValue(owner, true);

        // Jika pemain yang mengaktifkan, musuh yang kena damage, dan sebaliknya
        if (isPlayer) {
            screen.enemyProfile.takeDamage(1);
            Gdx.app.log("Skill", "CORRUPT BOOT! 1 Damage langsung ke O.M.E.G.A");
        } else {
            screen.playerProfile.takeDamage(1);
            Gdx.app.log("Skill", "CORRUPT BOOT! 1 Damage langsung ke Sysadmin");
        }

        // Segarkan UI Darah
        screen.uiManager.updateHP();
    }
}
