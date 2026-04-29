package com.NCFrontend.logic.abilities;

import com.NCFrontend.logic.CardAbility;
import com.NCFrontend.models.ProgramData;
import com.NCFrontend.screens.GameplayScreen;
import com.NCFrontend.ui.CardActor;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.utils.ObjectMap;

public class BackdoorBreachAbility implements CardAbility {
    @Override
    public void onDeath(CardActor owner, GameplayScreen screen) {
        String[] lanes = {"Localhost", "Cloud Storage", "DMZ", "Dark Node"};
        boolean isPlayer = screen.activeCards.containsValue(owner, true);
        ObjectMap<String, CardActor> board = isPlayer ? screen.activeCards : screen.enemyActiveCards;

        int deadIdx = -1;
        for (int i = 0; i < lanes.length; i++) {
            // Cari index lane berdasarkan kartu yang sedang mati
            if (board.get(lanes[i]) == owner) {
                deadIdx = i;
                break;
            }
        }

        if (deadIdx != -1) {
            Gdx.app.log("Skill", "BACKDOOR BREACH! Menyebarkan virus ke jaringan sekitar...");

            // Iterasi untuk lane saat ini (deadIdx), kiri (deadIdx-1), dan kanan (deadIdx+1)
            for (int i = deadIdx - 1; i <= deadIdx + 1; i++) {
                if (i >= 0 && i < lanes.length) {
                    spawnWormAtLane(lanes[i], isPlayer, screen, owner);
                }
            }
        }
    }

    private void spawnWormAtLane(String laneName, boolean isPlayer, GameplayScreen screen, CardActor owner) {
        ObjectMap<String, CardActor> board = isPlayer ? screen.activeCards : screen.enemyActiveCards;

        // Syarat: Hanya spawn jika lane tersebut kosong (agar tidak menimpa kartu teman sendiri)
        if (!board.containsKey(laneName) || board.get(laneName) == owner) {

            ProgramData wormData = new ProgramData("Worm Token", 0, 1, 1, "MALWARE", "Virus hasil replikasi Trojan Knight.");

            Texture tex;
            try {
                tex = new Texture(Gdx.files.internal("images/trojan.png"));
            } catch (Exception e) {
                tex = new Texture(Gdx.files.internal("libgdx.png"));
            }

            CardActor worm = new CardActor(wormData, tex);
            worm.isOnBoard = true;
            worm.isFaceUp = true;

            // --- PERHITUNGAN POSISI DROPZONE ---
            float targetX = getXForLane(laneName);
            float targetY = isPlayer ? 280f : 550f; // Posisi Y standar dropzone pemain dan musuh
            float rotation = isPlayer ? 0 : 180f;

            // Set posisi awal di tempat Trojan mati agar ada transisi visual
            worm.setPosition(owner.getX(), owner.getY());
            worm.setRotation(rotation);
            worm.setScale(0); // Mulai dari kecil untuk efek animasi muncul
            worm.setColor(1, 1, 1, 0); // Transparan awal

            // Tambahkan ke sistem logika
            board.put(laneName, worm);
            screen.stage.addActor(worm);

            // --- ANIMASI MASUK KE DROPZONE ---
            worm.addAction(Actions.parallel(
                Actions.moveTo(targetX, targetY, 0.5f, Interpolation.pow3Out),
                Actions.scaleTo(0.55f, 0.55f, 0.5f, Interpolation.pow3Out),
                Actions.fadeIn(0.5f)
            ));
        }
    }

    private float getXForLane(String lane) {
        if (lane.equalsIgnoreCase("Localhost")) return 327.5f;
        if (lane.equalsIgnoreCase("Cloud Storage")) return 727.5f;
        if (lane.equalsIgnoreCase("DMZ")) return 1127.5f;
        if (lane.equalsIgnoreCase("Dark Node")) return 1527.5f;
        return 500f;
    }
}
