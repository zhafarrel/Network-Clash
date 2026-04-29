package com.NCFrontend.logic.abilities;

import com.NCFrontend.logic.CardAbility;
import com.NCFrontend.screens.GameplayScreen;
import com.NCFrontend.ui.CardActor;
import com.badlogic.gdx.Gdx;

public class OverclockAbility implements CardAbility {
    @Override
    public void onFloop(CardActor owner, GameplayScreen screen) {
        boolean isPlayer = screen.activeCards.containsValue(owner, true);

        if (isPlayer) {
            screen.playerProfile.currentRam += 1;
            screen.uiManager.updateRamLabel(screen.playerProfile.currentRam, screen.playerProfile.maxRam);
            Gdx.app.log("Skill", "OVERCLOCK AKTIF! +1 RAM sementara untuk Sysadmin.");
        } else {
            screen.enemyProfile.currentRam += 1;
            Gdx.app.log("Skill", "OVERCLOCK AKTIF! +1 RAM sementara untuk O.M.E.G.A.");
        }
    }
}
