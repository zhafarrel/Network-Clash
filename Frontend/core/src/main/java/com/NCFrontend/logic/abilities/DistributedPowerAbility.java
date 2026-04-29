package com.NCFrontend.logic.abilities;

import com.NCFrontend.logic.CardAbility;
import com.NCFrontend.models.ProgramData;
import com.NCFrontend.screens.GameplayScreen;
import com.NCFrontend.ui.CardActor;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.ObjectMap;

public class DistributedPowerAbility implements CardAbility {
    @Override
    public void onTurnEnd(CardActor owner, GameplayScreen screen) {
        boolean isPlayer = screen.activeCards.containsValue(owner, true);
        ObjectMap<String, CardActor> myBoard = isPlayer ? screen.activeCards : screen.enemyActiveCards;

        int botnetCount = 0;
        // Hitung ada berapa Botnet Node di pasukan kawan
        for (CardActor ally : myBoard.values()) {
            if (ally.getData().name.equalsIgnoreCase("Botnet Node")) {
                botnetCount++;
            }
        }

        if (owner.getData() instanceof ProgramData) {
            ProgramData pData = (ProgramData) owner.getData();
            // Base ATK Botnet Node adalah 1. Total ATK = 1 + (jumlah botnet lain)
            // Karena 'botnetCount' sudah termasuk dirinya sendiri, rumusnya cukup ATK = botnetCount
            pData.atk = botnetCount;

            Gdx.app.log("Skill", "DISTRIBUTED POWER! Sinergi Botnet Node menyesuaikan ATK menjadi: " + pData.atk);
        }
    }
}
