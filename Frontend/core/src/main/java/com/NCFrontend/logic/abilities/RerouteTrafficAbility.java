package com.NCFrontend.logic.abilities;

import com.NCFrontend.logic.CardAbility;
import com.NCFrontend.screens.GameplayScreen;
import com.NCFrontend.ui.CardActor;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.utils.ObjectMap;

public class RerouteTrafficAbility implements CardAbility {
    @Override
    public void onFloop(CardActor owner, GameplayScreen screen) {
        boolean isPlayer = screen.activeCards.containsValue(owner, true);
        ObjectMap<String, CardActor> board = isPlayer ? screen.activeCards : screen.enemyActiveCards;

        // --- PERBAIKAN: BACA DARI JALUR DINAMIS FISIK ---
        String[] lanes = screen.boardLanes;

        int myIdx = -1;
        for (int i = 0; i < lanes.length; i++) {
            // Tambahkan pengecekan null agar aman
            if (lanes[i] != null && board.get(lanes[i]) == owner) {
                myIdx = i;
                break;
            }
        }

        // Cek apakah ada teman di sebelah kiri atau kanan secara FISIK
        int targetIdx = -1;
        if (myIdx > 0 && lanes[myIdx - 1] != null && board.containsKey(lanes[myIdx - 1])) {
            targetIdx = myIdx - 1; // Prioritas tukar dengan kiri
        } else if (myIdx < lanes.length - 1 && lanes[myIdx + 1] != null && board.containsKey(lanes[myIdx + 1])) {
            targetIdx = myIdx + 1; // Kalau kiri kosong, tukar dengan kanan
        }

        if (targetIdx != -1) {
            String myLane = lanes[myIdx];
            String targetLane = lanes[targetIdx];
            CardActor neighbor = board.get(targetLane);

            // 1. Tukar Posisi di Data (ObjectMap)
            board.put(myLane, neighbor);
            board.put(targetLane, owner);

            // 2. Tukar Posisi Visual di Layar secara halus
            float myOrigX = owner.getX();
            float neighborOrigX = neighbor.getX();

            owner.clearActions();
            neighbor.clearActions();

            // Angkat kartu sedikit saat bertukar agar terlihat elegan
            owner.toFront();
            owner.addAction(Actions.sequence(
                Actions.moveBy(0, 30, 0.1f),
                Actions.moveTo(neighborOrigX, owner.getY() + 30, 0.4f, Interpolation.pow2Out),
                Actions.moveBy(0, -30, 0.1f)
            ));

            neighbor.addAction(Actions.sequence(
                Actions.moveBy(0, -30, 0.1f),
                Actions.moveTo(myOrigX, neighbor.getY() - 30, 0.4f, Interpolation.pow2Out),
                Actions.moveBy(0, 30, 0.1f)
            ));

            Gdx.app.log("Skill", "REROUTE TRAFFIC! Bertukar posisi dengan " + neighbor.getData().name);
            screen.uiManager.showNotification("REROUTE TRAFFIC:\nBertukar dengan " + neighbor.getData().name);

        } else {
            Gdx.app.log("Skill", "REROUTE TRAFFIC gagal: Tidak ada kawan di sebelah.");
            screen.uiManager.showNotification("REROUTE GAGAL:\nTidak ada target di sebelah!");
        }
    }
}
