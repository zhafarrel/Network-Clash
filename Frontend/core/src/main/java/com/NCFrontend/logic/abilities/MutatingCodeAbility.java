package com.NCFrontend.logic.abilities;

import com.NCFrontend.logic.CardAbility;
import com.NCFrontend.logic.CombatResolver;
import com.NCFrontend.screens.GameplayScreen;
import com.NCFrontend.ui.CardActor;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;

public class MutatingCodeAbility implements CardAbility {
    @Override
    public void onTurnEnd(CardActor owner, GameplayScreen screen) {
        int currentHp = CombatResolver.getHp(owner.getData());

        // Polymorphic Virus memiliki base HP 3 di GDD
        int maxHp = 3;

        if (currentHp < maxHp) {
            CombatResolver.setHp(owner.getData(), currentHp + 1);
            Gdx.app.log("Skill", "MUTATING CODE! Polymorphic Virus meregenerasi 1 HP.");

            // Efek berlendir/memulihkan diri warna hijau
            owner.addAction(Actions.sequence(
                Actions.color(Color.GREEN, 0.2f),
                Actions.scaleTo(1.05f, 1.05f, 0.2f),
                Actions.scaleTo(1.0f, 1.0f, 0.2f),
                Actions.color(Color.WHITE, 0.2f)
            ));
        }
    }
}
