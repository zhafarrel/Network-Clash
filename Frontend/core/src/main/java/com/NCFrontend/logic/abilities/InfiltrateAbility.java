package com.NCFrontend.logic.abilities;

import com.NCFrontend.logic.CardAbility;
import com.NCFrontend.screens.GameplayScreen;
import com.NCFrontend.ui.CardActor;
import com.badlogic.gdx.Gdx;

public class InfiltrateAbility implements CardAbility {
    @Override
    public CardActor onTargeting(CardActor owner, CardActor attacker, CardActor originalTarget, GameplayScreen screen) {
        // Jika kartu ini yang sedang maju menyerang, abaikan target (jadikan null)
        if (attacker == owner) {
            Gdx.app.log("Skill", "INFILTRATE AKTIF! Spyware Wasp menyelinap langsung ke sistem utama.");
            return null; // Null memaksa CombatResolver menembak langsung ke HP Pemain/Musuh
        }
        return originalTarget;
    }
}
