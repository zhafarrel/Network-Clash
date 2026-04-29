package com.NCFrontend.logic.abilities;

import com.NCFrontend.logic.CardAbility;
import com.NCFrontend.logic.CombatResolver;
import com.NCFrontend.screens.GameplayScreen;
import com.NCFrontend.ui.CardActor;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;

public class TrafficFilterAbility implements CardAbility {
    @Override
    public CardActor onTargeting(CardActor owner, CardActor attacker, CardActor originalTarget, GameplayScreen screen) {
        if (originalTarget == owner && attacker != owner) {
            int currentHp = CombatResolver.getHp(owner.getData());
            CombatResolver.setHp(owner.getData(), currentHp + 1); // Pre-Heal sebagai tameng

            Gdx.app.log("Skill", "TRAFFIC FILTER! Reverse Proxy Shield menyerap 1 Damage dari serangan.");

            owner.addAction(Actions.sequence(
                Actions.color(Color.CYAN, 0.1f), Actions.color(Color.WHITE, 0.1f)
            ));
        }
        return originalTarget;
    }
}
