package com.NCFrontend.logic;

import com.NCFrontend.screens.GameplayScreen;
import com.NCFrontend.ui.CardActor;

public interface CardAbility {
    // owner: Kartu yang memiliki skill ini
    // attacker: Kartu yang sedang menyerang
    // originalTarget: Target asli sebelum diubah oleh skill
    default CardActor onTargeting(CardActor owner, CardActor attacker, CardActor originalTarget, GameplayScreen screen) {
        return originalTarget;
    }

    // Dipanggil saat kartu menyerang
    default void onAttack(CardActor owner, CardActor defender, GameplayScreen screen) {}

    // Dipanggil saat kartu hancur/mati (Contoh: Redundancy, Backdoor Breach)
    default void onDeath(CardActor owner, GameplayScreen screen) {}

    // Dipanggil di akhir giliran (Contoh: System Restore)
    default void onTurnEnd(CardActor owner, GameplayScreen screen) {}

    // Dipanggil saat tombol EXECUTE ditekan
    default void onFloop(CardActor owner, GameplayScreen screen) {}

    default void onPlayScript(CardActor owner, String targetLane, GameplayScreen screen) {}

    default void onAnyCardDeath(CardActor owner, CardActor deadCard, GameplayScreen screen) {}

    default void onCardDeployed(CardActor owner, CardActor deployedCard, String lane, GameplayScreen screen) {}
}
