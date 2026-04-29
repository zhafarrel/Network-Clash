package com.NCFrontend.logic.abilities;

import com.NCFrontend.logic.CardAbility;
import com.NCFrontend.screens.GameplayScreen;
import com.NCFrontend.ui.CardActor;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.utils.ObjectMap;

public class DeepPacketInspectionAbility implements CardAbility {
    @Override
    public void onPlayScript(CardActor owner, String targetLane, GameplayScreen screen) {
        boolean isPlayer = screen.hand.contains(owner, true) || owner.isOnBoard;
        ObjectMap<String, CardActor> myBoard = isPlayer ? screen.activeCards : screen.enemyActiveCards;

        Gdx.app.log("Script", "DEEP PACKET INSPECTION! Menukarkan tempo serangan untuk Card Draw besar-besaran.");

        // 1. Pengorbanan Tempo: Kunci (Stun) semua sekutu di papan
        for (CardActor ally : myBoard.values()) {
            ally.isStunned = true;
            ally.addAction(Actions.sequence(
                Actions.color(Color.YELLOW, 0.2f), Actions.color(Color.LIGHT_GRAY, 0.3f)
            ));
        }

        // 2. Keuntungan: Draw 3 Kartu berturut-turut!
        if (isPlayer) {
            screen.drawCard();
            screen.drawCard();
            screen.drawCard();
        } else {
            // Logika sederhana jika AI musuh yang memakainya
            for(int i=0; i<3; i++) {
                if(screen.enemyDeck.size > 0 && screen.enemyHand.size < 7) {
                    CardActor card = screen.enemyDeck.pop();
                    card.isFaceUp = false;
                    screen.enemyHand.add(card);
                }
            }
        }
    }
}
