package com.NCFrontend.ai;

import com.NCFrontend.screens.GameplayScreen;
import com.NCFrontend.ui.CardActor;
import com.NCFrontend.models.ScriptData;
import com.badlogic.gdx.utils.Array;

public class BotStrategy {
    private GameplayScreen screen;

    public BotStrategy(GameplayScreen screen) {
        this.screen = screen;
    }

    // 1. Memilih kartu terbaik untuk dimainkan
    public CardActor getBestCardToPlay() {
        Array<CardActor> playableCards = new Array<>();

        // Filter kartu yang RAM-nya cukup
        for (CardActor card : screen.enemyHand) {
            if (card.getData().ramCost <= screen.enemyProfile.currentRam) {
                playableCards.add(card);
            }
        }

        if (playableCards.size == 0) return null; // Uang habis atau tangan kosong

        // PRIORITAS 1: Mainkan Kartu Script (Sihir Instan)
        for (CardActor card : playableCards) {
            if (card.getData() instanceof ScriptData) {
                return card;
            }
        }

        // PRIORITAS 2: Mainkan Malware termahal / terkuat
        playableCards.sort((c1, c2) -> Integer.compare(c2.getData().ramCost, c1.getData().ramCost));
        return playableCards.get(0);
    }

    // 2. Memilih Lane terbaik
    public String getBestLaneForCard(CardActor card) {
        String specificLane = card.getData().validLane;
        if (specificLane != null && !specificLane.equalsIgnoreCase("ANY_LANE")) {
            // CEK DULU: Apakah lane incaran musuh ini sedang dipasang di papan?
            boolean laneExists = false;
            for (String bLane : screen.boardLanes) {
                if (bLane != null && bLane.equalsIgnoreCase(specificLane)) {
                    laneExists = true;
                    break;
                }
            }
            // Jika jalurnya ada, taruh di sana!
            if (laneExists) return specificLane;
        }

        // Jika ANY_LANE atau jalurnya tidak ada di papan, cari celah kosong (Prioritas 1)
        for (String lane : screen.boardLanes) {
            if (lane != null && !screen.enemyActiveCards.containsKey(lane) && !screen.activeCards.containsKey(lane)) {
                return lane;
            }
        }

        // Cari slot musuh yang belum diisi (Prioritas 2)
        for (String lane : screen.boardLanes) {
            if (lane != null && !screen.enemyActiveCards.containsKey(lane)) {
                return lane;
            }
        }

        return screen.boardLanes[0]; // Terpaksa menumpuk di slot 0
    }

    // 3. Mencari kartu di papan yang bisa di-EXECUTE (Floop)
    public CardActor getBestCardToFloop() {
        for (CardActor card : screen.enemyActiveCards.values()) {
            boolean hasExecute = card.getData().description != null && card.getData().description.contains("(EXECUTE)");

            if (hasExecute && !card.isFlooped && !card.isSilenced) {
                return card; // Temukan 1 kartu yang siap ditekan tombol Execute-nya
            }
        }
        return null;
    }
}
