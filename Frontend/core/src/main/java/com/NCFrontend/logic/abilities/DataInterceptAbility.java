package com.NCFrontend.logic.abilities;

import com.NCFrontend.logic.CardAbility;
import com.NCFrontend.models.ProgramData;
import com.NCFrontend.models.ScriptData;
import com.NCFrontend.screens.GameplayScreen;
import com.NCFrontend.ui.CardActor;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;

public class DataInterceptAbility implements CardAbility {
    @Override
    public void onFloop(CardActor owner, GameplayScreen screen) {
        boolean isPlayer = screen.activeCards.containsValue(owner, true);

        // Cek deck musuh (atau deck pemain jika musuh yang memakai)
        com.badlogic.gdx.utils.Array<CardActor> targetDeck = isPlayer ? screen.enemyDeck : screen.deck;

        if (targetDeck.size > 0) {
            // Intip kartu paling atas tanpa mengambilnya (jadikan final agar bisa dipakai di dalam Actions.run)
            final CardActor topCard = targetDeck.peek();

            Gdx.app.log("Skill", "DATA INTERCEPT! " + owner.getData().name + " mengintip kartu teratas: " + topCard.getData().name);

            // ==========================================
            // 1. ANIMASI SINEMATIK: KARTU MELAYANG
            // ==========================================
            topCard.setVisible(true);
            topCard.isFaceUp = true;
            topCard.toFront();

            // Simpan posisi awal deck
            final float startX = topCard.getX();
            final float startY = topCard.getY();

            // Hitung titik tengah layar
            float centerX = Gdx.graphics.getWidth() / 2f - (topCard.getWidth() * 1.5f) / 2f;
            float centerY = Gdx.graphics.getHeight() / 2f - (topCard.getHeight() * 1.5f) / 2f;

            topCard.clearActions();
            topCard.addAction(Actions.sequence(
                // Terbang ke tengah dan membesar perlahan
                Actions.parallel(
                    Actions.moveTo(centerX, centerY, 0.6f, Interpolation.pow3Out),
                    Actions.scaleTo(1.5f, 1.5f, 0.6f, Interpolation.pow3Out)
                ),

                // Tahan di layar selama 3.5 detik agar pemain sempat membaca
                Actions.delay(3.5f),

                // Balikkan (tutup) kartunya kembali
                Actions.run(new Runnable() {
                    @Override
                    public void run() {
                        topCard.isFaceUp = false;
                    }
                }),

                // Terbang kembali ke tumpukan deck
                Actions.parallel(
                    Actions.moveTo(startX, startY, 0.5f, Interpolation.pow3In),
                    Actions.scaleTo(0.5f, 0.5f, 0.5f, Interpolation.pow3In)
                ),

                // Sembunyikan lagi dari layar
                Actions.run(new Runnable() {
                    @Override
                    public void run() {
                        topCard.setVisible(false);
                        topCard.clearActions();
                    }
                })
            ));

            // Notifikasi Popup
            screen.uiManager.showNotification("DATA INTERCEPT!\nMengintip: " + topCard.getData().name);

            // ==========================================
            // 2. LOGIKA BUFF: +1 ATK JIKA SCRIPT
            // ==========================================
            if (topCard.getData() instanceof ScriptData) {
                if (owner.getData() instanceof ProgramData) {
                    ((ProgramData) owner.getData()).atk += 1;
                    Gdx.app.log("Skill", "Kartu adalah Script! " + owner.getData().name + " mendapat permanen +1 ATK.");

                    // Efek visual membesar dan berkedip hijau pada Hound
                    owner.addAction(Actions.sequence(
                        Actions.color(com.badlogic.gdx.graphics.Color.GREEN, 0.2f),
                        Actions.scaleTo(1.2f, 1.2f, 0.2f),
                        Actions.scaleTo(1.0f, 1.0f, 0.2f),
                        Actions.color(com.badlogic.gdx.graphics.Color.WHITE, 0.2f)
                    ));

                    // Tambahkan Pop-up Notifikasi Kedua agar pemain sadar ATK-nya bertambah!
                    screen.stage.addAction(Actions.sequence(
                        Actions.delay(1.5f),
                        Actions.run(new Runnable() {
                            @Override
                            public void run() {
                                screen.uiManager.showNotification(owner.getData().name + " menyerap data!\nMendapat permanen +1 ATK!");
                            }
                        })
                    ));
                }
            }

        } else {
            Gdx.app.log("Skill", "DATA INTERCEPT gagal, deck target sudah kosong.");
            screen.uiManager.showNotification("INTERCEPT GAGAL!\nDeck musuh sudah kosong.");
        }
    }
}
