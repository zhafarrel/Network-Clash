package com.NCFrontend.logic.abilities;

import com.NCFrontend.logic.CardAbility;
import com.NCFrontend.screens.GameplayScreen;
import com.NCFrontend.ui.CardActor;
import com.badlogic.gdx.Gdx;

public class VanguardAbility implements CardAbility {
    @Override
    public CardActor onTargeting(CardActor owner, CardActor attacker, CardActor originalTarget, GameplayScreen screen) {
        if (originalTarget != null) return originalTarget;

        for (CardAbility atkSkill : attacker.getData().abilities) {
            if (atkSkill instanceof DeepInfectionAbility) return originalTarget;
        }

        String[] lanes = {"Localhost", "Cloud Storage", "DMZ", "Dark Node"};
        int ownerIdx = -1, attackerIdx = -1;

        boolean isOwnerPlayer = screen.activeCards.containsValue(owner, true);
        boolean isAttackerPlayer = screen.activeCards.containsValue(attacker, true);

        if (isOwnerPlayer == isAttackerPlayer) return originalTarget;

        for (int i = 0; i < lanes.length; i++) {
            if ((isOwnerPlayer ? screen.activeCards.get(lanes[i]) : screen.enemyActiveCards.get(lanes[i])) == owner) ownerIdx = i;
            if ((isAttackerPlayer ? screen.activeCards.get(lanes[i]) : screen.enemyActiveCards.get(lanes[i])) == attacker) attackerIdx = i;
        }

        if (Math.abs(ownerIdx - attackerIdx) == 1) {
            Gdx.app.log("Skill", "VANGUARD AKTIF! Serangan ditarik oleh " + owner.getData().name);
            return owner;
        }

        return originalTarget;
    }
}
