package com.NCFrontend.logic;

import com.NCFrontend.models.BaseCard;
import com.NCFrontend.models.ProgramData;
import com.NCFrontend.screens.GameplayScreen;
import com.NCFrontend.ui.CardActor;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;

public class CombatResolver {

    // Helper untuk mengambil dan mengubah HP/ATK karena struktur data kita fleksibel
    public static int getAtk(BaseCard data) {
        if (data instanceof ProgramData) return ((ProgramData) data).atk;
        if (data.getClass().getSimpleName().equals("MalwareData")) {
            try { return (int) data.getClass().getField("atk").get(data); } catch (Exception e) {}
        }
        return 0;
    }

    public static int getHp(BaseCard data) {
        if (data instanceof ProgramData) return ((ProgramData) data).hp;
        if (data.getClass().getSimpleName().equals("MalwareData")) {
            try { return (int) data.getClass().getField("hp").get(data); } catch (Exception e) {}
        }
        return 0;
    }

    public static void setHp(BaseCard data, int newHp) {
        if (data instanceof ProgramData) ((ProgramData) data).hp = newHp;
        else if (data.getClass().getSimpleName().equals("MalwareData")) {
            try { data.getClass().getField("hp").set(data, newHp); } catch (Exception e) {}
        }
    }

    // Eksekusi Pertarungan Papan (Auto-Battler)
    public static void resolveBoardCombat(GameplayScreen screen, boolean isPlayerAttacking, Runnable onComplete) {
        String[] lanes = {"Localhost", "Cloud Storage", "DMZ", "Dark Node"};
        float delay = 0f;

        for (String lane : lanes) {
            CardActor attacker = isPlayerAttacking ? screen.activeCards.get(lane) : screen.enemyActiveCards.get(lane);
            CardActor defender = isPlayerAttacking ? screen.enemyActiveCards.get(lane) : screen.activeCards.get(lane);

            // Kartu hanya bisa menyerang jika dia ada, dan tidak sedang tertidur (isFlooped)
            if (attacker != null && !attacker.isFlooped) {
                int atk = getAtk(attacker.getData());
                if (atk > 0) {
                    float startY = attacker.getY();
                    float attackY = isPlayerAttacking ? startY + 100 : startY - 100; // Maju ke depan

                    // Animasi Serangan (Maju cepat, pukul, mundur pelan)
                    attacker.toFront();
                    attacker.addAction(Actions.sequence(
                        Actions.delay(delay),
                        Actions.moveTo(attacker.getX(), attackY, 0.15f, Interpolation.exp10In),
                        Actions.run(() -> applyDamage(attacker, defender, isPlayerAttacking, screen, lane)),
                        Actions.moveTo(attacker.getX(), startY, 0.2f, Interpolation.pow2Out)
                    ));
                    delay += 0.4f; // Jeda antar lane agar serangannya bergantian (kelihatan keren)
                }
            }
        }

        // Setelah semua lane selesai animasi, pindah turn!
        screen.stage.addAction(Actions.sequence(Actions.delay(delay + 0.5f), Actions.run(onComplete)));
    }

    private static void applyDamage(CardActor attacker, CardActor defender, boolean isPlayerAttacking, GameplayScreen screen, String lane) {
        int atk = getAtk(attacker.getData());

        if (defender != null) {
            // HAJAR KARTU LAWAN
            int defHp = getHp(defender.getData());
            defHp -= atk;
            setHp(defender.getData(), defHp);
            Gdx.app.log("Combat", attacker.getData().name + " menyerang " + defender.getData().name + " sebesar " + atk + " DMG!");

            // Animasi kartu musuh bergetar kena pukul
            defender.addAction(Actions.sequence(
                Actions.moveBy(15, 0, 0.05f), Actions.moveBy(-30, 0, 0.05f), Actions.moveBy(15, 0, 0.05f)
            ));

            if (defHp <= 0) {
                Gdx.app.log("Combat", defender.getData().name + " HANCUR!");
                defender.addAction(Actions.sequence(
                    Actions.parallel(Actions.scaleTo(0, 0, 0.3f), Actions.fadeOut(0.3f)),
                    Actions.removeActor()
                ));
                // Bersihkan dari memori papan
                if (isPlayerAttacking) screen.enemyActiveCards.remove(lane);
                else screen.activeCards.remove(lane);
            }
        } else {
            // HAJAR WAJAH (DIRECT ATTACK KARENA LANE KOSONG)
            Gdx.app.log("Combat", attacker.getData().name + " DIRECT ATTACK!");
            if (isPlayerAttacking) {
                screen.enemyHP -= atk;
                if (screen.enemyHP < 0) screen.enemyHP = 0;
            } else {
                screen.playerHP -= atk;
                if (screen.playerHP < 0) screen.playerHP = 0;
            }
            screen.uiManager.updateHP(); // Update teks UI
        }
    }
}
