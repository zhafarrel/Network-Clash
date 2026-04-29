package com.NCFrontend.logic.abilities;

import com.NCFrontend.logic.CardAbility;
import com.NCFrontend.screens.GameplayScreen;
import com.NCFrontend.ui.CardActor;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.utils.ObjectMap;

public class BackupServerAbility implements CardAbility {
    @Override
    public void onAnyCardDeath(CardActor owner, CardActor deadCard, GameplayScreen screen) {
        boolean isOwnerPlayer = screen.activeCards.containsValue(owner, true);
        boolean isDeadCardPlayer = screen.activeCards.containsValue(deadCard, true);

        // Hanya peduli kalau yang mati adalah kawan
        if (isOwnerPlayer == isDeadCardPlayer) {
            String[] lanes = {"Localhost", "Cloud Storage", "DMZ", "Dark Node"};
            ObjectMap<String, CardActor> board = isOwnerPlayer ? screen.activeCards : screen.enemyActiveCards;

            int ownerIdx = -1, deadIdx = -1;

            for (int i = 0; i < lanes.length; i++) {
                if (board.get(lanes[i]) == owner) ownerIdx = i;
                if (board.get(lanes[i]) == deadCard) deadIdx = i;
            }

            // Cek apakah mereka bersebelahan sebelum yang satu mati
            if (Math.abs(ownerIdx - deadIdx) == 1) {
                Gdx.app.log("Skill", "REDUNDANCY AKTIF! Kawan di sebelah hancur, memulihkan 2 HP sistem.");

                if (isOwnerPlayer) {
                    // Karena ini heal sistem utama, tambahkan HP ke profile, bukan kartu
                    int newHp = Math.min(screen.playerProfile.hp + 2, 20); // Asumsi max HP 20
                    screen.playerProfile.hp = newHp;
                } else {
                    int newHp = Math.min(screen.enemyProfile.hp + 2, 20);
                    screen.enemyProfile.hp = newHp;
                }

                screen.uiManager.updateHP();

                // Efek visual kartu bersinar hijau
                owner.addAction(Actions.sequence(
                    Actions.color(Color.GREEN, 0.2f),
                    Actions.color(Color.WHITE, 0.2f)
                ));
            }
        }
    }
}
