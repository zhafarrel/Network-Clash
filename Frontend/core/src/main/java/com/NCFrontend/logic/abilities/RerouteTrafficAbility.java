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
        ObjectMap<String, CardActor> myBoard = isPlayer ? screen.activeCards : screen.enemyActiveCards;

        // 1. CARI POSISI (INDEX) DARI LOAD BALANCER
        int ownerIndex = -1;
        String ownerLane = null;
        for (int i = 0; i < screen.boardLanes.length; i++) {
            String laneName = screen.boardLanes[i];
            if (laneName != null && myBoard.get(laneName) == owner) {
                ownerIndex = i;
                ownerLane = laneName;
                break;
            }
        }

        if (ownerIndex == -1) {
            Gdx.app.log("Skill", "REROUTE TRAFFIC gagal: Load Balancer tidak ditemukan di papan.");
            return;
        }

        // 2. CARI SEKUTU DI JALUR BERSEBELAHAN (KIRI ATAU KANAN)
        int targetIndex = -1;
        String targetLane = null;
        CardActor targetAlly = null;

        // Prioritas cek Kiri dulu
        if (ownerIndex > 0) {
            String leftLane = screen.boardLanes[ownerIndex - 1];
            if (leftLane != null && myBoard.containsKey(leftLane)) {
                targetLane = leftLane;
                targetIndex = ownerIndex - 1;
                targetAlly = myBoard.get(leftLane);
            }
        }

        // Jika di kiri kosong/tidak ada teman, cek Kanan
        if (targetAlly == null && ownerIndex < screen.boardLanes.length - 1) {
            String rightLane = screen.boardLanes[ownerIndex + 1];
            if (rightLane != null && myBoard.containsKey(rightLane)) {
                targetLane = rightLane;
                targetIndex = ownerIndex + 1;
                targetAlly = myBoard.get(rightLane);
            }
        }

        // 3. JIKA KETEMU TEMAN, LAKUKAN SWAP!
        if (targetAlly != null) {
            Gdx.app.log("Skill", "REROUTE TRAFFIC! Bertukar posisi dengan " + targetAlly.getData().name);

            // A. Tukar posisi kepemilikan di otak Game (ObjectMap)
            myBoard.put(ownerLane, targetAlly);
            myBoard.put(targetLane, owner);

            // B. Tukar posisi visual menggunakan Animasi (Tanpa merusak Stage)
            float ownerX = owner.getX();
            float ownerY = owner.getY();
            float targetX = targetAlly.getX();
            float targetY = targetAlly.getY();

            // Bawa keduanya ke depan agar tidak tertutup kartu lain saat animasi lompat
            owner.toFront();
            targetAlly.toFront();

            // Animasi Load Balancer melompat ke atas, lalu geser ke slot baru
            owner.addAction(Actions.sequence(
                Actions.moveBy(0, 80, 0.15f, Interpolation.smooth),
                Actions.moveTo(targetX, targetY + 80, 0.3f, Interpolation.smooth),
                Actions.moveTo(targetX, targetY, 0.15f, Interpolation.bounceOut)
            ));

            // Animasi Target mundur ke belakang, lalu geser ke slot lama
            targetAlly.addAction(Actions.sequence(
                Actions.moveBy(0, -80, 0.15f, Interpolation.smooth),
                Actions.moveTo(ownerX, ownerY - 80, 0.3f, Interpolation.smooth),
                Actions.moveTo(ownerX, ownerY, 0.15f, Interpolation.bounceOut)
            ));

        } else {
            Gdx.app.log("Skill", "REROUTE TRAFFIC batal: Tidak ada sekutu di jalur sebelah untuk ditukar.");
        }
    }
}
