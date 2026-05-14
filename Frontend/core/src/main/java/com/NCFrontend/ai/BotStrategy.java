package com.NCFrontend.ai;

import com.NCFrontend.screens.GameplayScreen;
import com.NCFrontend.ui.CardActor;
import com.NCFrontend.models.ScriptData;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.Array;

public class BotStrategy {
    private GameplayScreen screen;

    public BotStrategy(GameplayScreen screen) {
        this.screen = screen;
    }

    public CardActor getBestCardToPlay() {
        Array<CardActor> playableCards = new Array<>();

        for (CardActor card : screen.enemyHand) {
            // PERLINDUNGAN EKSTRA: Pastikan card dan data tidak null
            if (card != null && card.getData() != null && card.getData().ramCost <= screen.enemyProfile.currentRam) {
                playableCards.add(card);
            }
        }

        if (playableCards.size == 0) {
            Gdx.app.log("BotStrategy", "Tidak ada kartu yang bisa dimainkan (RAM habis/Tangan Kosong)");
            return null;
        }

        for (CardActor card : playableCards) {
            if (card.getData() instanceof ScriptData) {
                Gdx.app.log("BotStrategy", "Memilih Kartu Script: " + card.getData().name);
                return card;
            }
        }

        // PERLINDUNGAN EKSTRA: Mencegah NullPointerException saat AI mengurutkan kartu
        playableCards.sort((c1, c2) -> {
            int cost1 = (c1 != null && c1.getData() != null) ? c1.getData().ramCost : 0;
            int cost2 = (c2 != null && c2.getData() != null) ? c2.getData().ramCost : 0;
            return Integer.compare(cost2, cost1);
        });

        Gdx.app.log("BotStrategy", "Memilih Kartu Malware: " + playableCards.get(0).getData().name);
        return playableCards.get(0);
    }

    public String getBestLaneForCard(CardActor card) {
        String specificLane = card.getData().validLane;
        if (specificLane != null && !specificLane.equalsIgnoreCase("ANY_LANE")) {
            for (String bLane : screen.boardLanes) {
                if (bLane != null && bLane.equalsIgnoreCase(specificLane)) {
                    return bLane; // Return EXACT case from boardLanes
                }
            }
        }

        for (String lane : screen.boardLanes) {
            if (lane != null && !screen.enemyActiveCards.containsKey(lane) && !screen.activeCards.containsKey(lane)) {
                return lane;
            }
        }

        for (String lane : screen.boardLanes) {
            if (lane != null && !screen.enemyActiveCards.containsKey(lane)) {
                return lane;
            }
        }

        return screen.boardLanes[0];
    }

    public CardActor getBestCardToFloop() {
        for (CardActor card : screen.enemyActiveCards.values()) {
            boolean hasExecute = card.getData().description != null && card.getData().description.toUpperCase().contains("EXECUTE");

            if (hasExecute && !card.isFlooped && !card.isSilenced) {
                Gdx.app.log("BotStrategy", "Menemukan kartu untuk di-Floop: " + card.getData().name);
                return card;
            }
        }
        return null;
    }
}
