package com.NCFrontend.logic.abilities;

import com.NCFrontend.logic.CardAbility;
import com.NCFrontend.screens.GameplayScreen;
import com.NCFrontend.ui.CardActor;
import com.NCFrontend.models.ProgramData;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;

public class PersistenceAbility implements CardAbility {
    @Override
    public void onDeath(CardActor owner, GameplayScreen screen) {
        boolean isPlayer = screen.activeCards.containsValue(owner, true) || screen.hand.contains(owner, true);
        com.badlogic.gdx.utils.Array<CardActor> targetHand = isPlayer ? screen.hand : screen.enemyHand;

        // Pastikan tangan belum penuh (misal maksimal 7 kartu)
        if (targetHand.size < 7 && owner.getData() instanceof ProgramData) {
            Gdx.app.log("Skill", "PERSISTENCE AKTIF! APT menolak mati dan kembali ke tangan!");

            // Naikkan biaya RAM sesuai aturan GDD
            owner.getData().ramCost += 1;

            // Buat entitas kartu baru untuk dimasukkan ke tangan
            Texture tex;
            try {
                tex = new Texture(Gdx.files.internal("images/" + owner.getData().id + ".png"));
            } catch (Exception e) {
                tex = new Texture(Gdx.files.internal("libgdx.png"));
            }

            CardActor revivedAPT = new CardActor(owner.getData(), tex);
            revivedAPT.isFaceUp = isPlayer; // Terbuka jika milik pemain, tertutup jika milik musuh
            revivedAPT.isOnBoard = false;

            // Masukkan kembali ke tangan
            targetHand.add(revivedAPT);
            screen.stage.addActor(revivedAPT);

            if (isPlayer) {
                screen.interactionHandler.registerCard(revivedAPT);
                screen.updateHandPositions();
            } else {
                // Sembunyikan posisi kartu di tangan musuh
                revivedAPT.setPosition(Gdx.graphics.getWidth() / 2f, Gdx.graphics.getHeight() + 100);
            }
        }
    }
}
