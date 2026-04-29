package com.NCFrontend.logic.abilities;

import com.NCFrontend.logic.CardAbility;
import com.NCFrontend.logic.CombatResolver;
import com.NCFrontend.screens.GameplayScreen;
import com.NCFrontend.ui.CardActor;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.ObjectMap;

public class SystemRestoreAbility implements CardAbility {
    @Override
    public void onTurnEnd(CardActor owner, GameplayScreen screen) {
        String[] lanes = {"Localhost", "Cloud Storage", "DMZ", "Dark Node"};
        int ownerIdx = -1;
        boolean isPlayer = screen.activeCards.containsValue(owner, true);
        ObjectMap<String, CardActor> board = isPlayer ? screen.activeCards : screen.enemyActiveCards;

        for (int i = 0; i < lanes.length; i++) {
            if (board.get(lanes[i]) == owner) ownerIdx = i;
        }

        if (ownerIdx > 0 && board.containsKey(lanes[ownerIdx - 1])) {
            healCard(board.get(lanes[ownerIdx - 1]));
        }
        if (ownerIdx < lanes.length - 1 && board.containsKey(lanes[ownerIdx + 1])) {
            healCard(board.get(lanes[ownerIdx + 1]));
        }
    }

    private void healCard(CardActor target) {
        int hp = CombatResolver.getHp(target.getData());
        CombatResolver.setHp(target.getData(), hp + 1);
        // Baris updateStatLabels sudah dihapus karena CardActor me-render secara otomatis setiap frame
        Gdx.app.log("Skill", "SYSTEM RESTORE! " + target.getData().name + " dipulihkan 1 HP.");
    }
}
