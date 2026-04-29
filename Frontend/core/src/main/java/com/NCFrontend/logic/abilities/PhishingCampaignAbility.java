package com.NCFrontend.logic.abilities;

import com.NCFrontend.logic.CardAbility;
import com.NCFrontend.screens.GameplayScreen;
import com.NCFrontend.ui.CardActor;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;

public class PhishingCampaignAbility implements CardAbility {
    @Override
    public void onPlayScript(CardActor owner, String targetLane, GameplayScreen screen) {
        boolean isPlayer = screen.hand.contains(owner, true);
        com.badlogic.gdx.utils.Array<CardActor> targetHand = isPlayer ? screen.enemyHand : screen.hand;

        if (targetHand.size > 0) {
            // Pilih indeks acak dari tangan target
            int randomIndex = MathUtils.random(0, targetHand.size - 1);
            CardActor discardedCard = targetHand.get(randomIndex);

            Gdx.app.log("Script", "PHISHING CAMPAIGN! Membuang paksa kartu: " + discardedCard.getData().name);

            targetHand.removeIndex(randomIndex);

            // Animasi kartu terbuang (jatuh ke bawah dan menghilang)
            discardedCard.addAction(Actions.sequence(
                Actions.parallel(
                    Actions.moveBy(0, -100, 0.4f),
                    Actions.fadeOut(0.4f),
                    Actions.rotateBy(90, 0.4f)
                ),
                Actions.removeActor()
            ));

            // Rapikan kembali tangan yang tersisa
            if (isPlayer) {
                // Asumsi ada fungsi merapikan tangan musuh (jika ada visualnya)
            } else {
                screen.updateHandPositions();
            }
        } else {
            Gdx.app.log("Script", "PHISHING CAMPAIGN Gagal: Tangan musuh sudah kosong.");
        }
    }
}
