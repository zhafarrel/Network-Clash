package com.NCFrontend.logic;

import com.NCFrontend.screens.GameplayScreen;
import com.NCFrontend.ui.CardActor;

public interface CardAbility {
    // Dipanggil saat menentukan target serangan (Contoh: Vanguard, Deep Infection)
    default CardActor onTargeting(CardActor attacker, CardActor originalTarget, GameplayScreen screen) {
        return originalTarget;
    }

    // Dipanggil saat kartu menyerang
    default void onAttack(CardActor attacker, CardActor defender, GameplayScreen screen) {}

    // Dipanggil saat kartu hancur/mati (Contoh: Redundancy, Backdoor Breach)
    default void onDeath(CardActor card, GameplayScreen screen) {}

    // Dipanggil di akhir giliran (Contoh: System Restore)
    default void onTurnEnd(CardActor card, GameplayScreen screen) {}
}
