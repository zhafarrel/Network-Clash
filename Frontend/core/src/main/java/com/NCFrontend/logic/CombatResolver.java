package com.NCFrontend.logic;

import com.NCFrontend.managers.GamePhaseManager;
import com.NCFrontend.models.BaseCard;
import com.NCFrontend.models.ProgramData;
import com.NCFrontend.screens.GameplayScreen;
import com.NCFrontend.ui.CardActor;
import com.NCFrontend.ui.InjectionMiniGame;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.utils.ObjectMap;

public class CombatResolver {
    private static Texture effectTexture;
    private static Label.LabelStyle defaultLabelStyle;

    private static void initEffects() {
        if (effectTexture == null) {
            Pixmap pix = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
            pix.setColor(Color.WHITE);
            pix.fill();
            effectTexture = new Texture(pix);
            pix.dispose();

            defaultLabelStyle = new Label.LabelStyle(new BitmapFont(), Color.WHITE);
        }
    }

    private static void spawnFloatingText(GameplayScreen screen, float x, float y, String text, Color color, float scale, float floatDist) {
        initEffects();
        Label label = new Label(text, defaultLabelStyle);
        label.setColor(color);
        label.setFontScale(scale);

        label.setPosition(x - (label.getPrefWidth() * scale) / 2f, y);
        screen.stage.addActor(label);
        label.toFront();

        label.addAction(Actions.sequence(
            Actions.parallel(
                Actions.moveBy(0, floatDist, 0.8f, Interpolation.pow2Out),
                Actions.fadeOut(0.8f)
            ),
            Actions.removeActor()
        ));
    }

    private static void spawnSlashEffect(GameplayScreen screen, float targetX, float targetY, int damage) {
        initEffects();

        float length = 120f;
        float thickness = 15f;

        Image slash = new Image(effectTexture);
        slash.setSize(length, thickness);
        slash.setOrigin(length / 2f, thickness / 2f);
        slash.setPosition(targetX - length / 2f, targetY - thickness / 2f);
        slash.setRotation(45f);
        slash.setColor(Color.CYAN);

        screen.stage.addActor(slash);

        slash.addAction(Actions.sequence(
            Actions.parallel(
                Actions.scaleTo(1.5f, 0.1f, 0.2f, Interpolation.pow2Out),
                Actions.fadeOut(0.2f)
            ),
            Actions.removeActor()
        ));

        spawnFloatingText(screen, targetX + 40f, targetY + 20f, String.valueOf(damage), Color.RED, 3f, 60f);

        for (int i = 0; i < 6; i++) {
            Image particle = new Image(effectTexture);
            float pSize = MathUtils.random(4f, 8f);
            particle.setSize(pSize, pSize);
            particle.setOrigin(pSize / 2f, pSize / 2f);
            particle.setPosition(targetX - pSize / 2f, targetY - pSize / 2f);

            if (i % 2 == 0) particle.setColor(Color.CYAN);
            else particle.setColor(Color.WHITE);

            float pAngle = MathUtils.random(0, 360);
            float pDist = MathUtils.random(30f, 70f);
            float destX = targetX + MathUtils.cosDeg(pAngle) * pDist;
            float destY = targetY + MathUtils.sinDeg(pAngle) * pDist;

            screen.stage.addActor(particle);

            particle.addAction(Actions.sequence(
                Actions.parallel(
                    Actions.moveTo(destX, destY, 0.3f, Interpolation.exp10Out),
                    Actions.rotateBy(MathUtils.random(-180f, 180f), 0.3f),
                    Actions.fadeOut(0.3f)
                ),
                Actions.removeActor()
            ));
        }
    }

    private static void showRedFlash(GameplayScreen screen) {
        initEffects();
        Image flash = new Image(effectTexture);
        flash.setSize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        flash.setColor(1, 0, 0, 0f);
        flash.setTouchable(com.badlogic.gdx.scenes.scene2d.Touchable.disabled);
        screen.stage.addActor(flash);
        flash.addAction(Actions.sequence(Actions.alpha(0.3f, 0.1f, Interpolation.fade), Actions.fadeOut(0.2f), Actions.removeActor()));
    }

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

    public static void resolveBoardCombat(GameplayScreen screen, boolean isPlayerAttacking, Runnable onComplete) {
        // MENGGUNAKAN ARRAY DINAMIS DARI LAYAR
        String[] lanes = screen.boardLanes;

        // Pastikan tidak ada lane yang null sebelum memproses serangan
        if (lanes[0] != null && lanes[1] != null && lanes[2] != null && lanes[3] != null) {
            processAttackForLane(screen, lanes, 0, isPlayerAttacking, onComplete);
        } else {
            if (onComplete != null) onComplete.run();
        }
    }

    private static void processAttackForLane(GameplayScreen screen, String[] lanes, int index, boolean isPlayerAttacking, Runnable onComplete) {
        if (index >= lanes.length) {
            if (onComplete != null) onComplete.run();
            return;
        }

        String lane = lanes[index];
        CardActor attacker = isPlayerAttacking ? screen.activeCards.get(lane) : screen.enemyActiveCards.get(lane);
        CardActor originalDefender = isPlayerAttacking ? screen.enemyActiveCards.get(lane) : screen.activeCards.get(lane);

        final CardActor[] finalDefender = {originalDefender};

        if (attacker != null && attacker.isStunned) {
            // Jika sedang kena Stun, hapus efek Stun-nya untuk giliran berikutnya
            attacker.isStunned = false;

            // Munculkan tulisan STUNNED! agar pemain tahu kenapa kartunya diam
            spawnFloatingText(screen, attacker.getX() + 50, attacker.getY() + 150, "STUNNED!", Color.YELLOW, 1.5f, 40f);

            // Lewati serangan dan lanjut ke lane berikutnya
            processAttackForLane(screen, lanes, index + 1, isPlayerAttacking, onComplete);
            return;
        }

        if (attacker != null && !attacker.isFlooped) {

            if (attacker.getData().abilities != null) {
                for (CardAbility ability : attacker.getData().abilities) {
                    finalDefender[0] = ability.onTargeting(attacker, attacker, finalDefender[0], screen);
                }
            }

            ObjectMap<String, CardActor> enemyBoard = isPlayerAttacking ? screen.enemyActiveCards : screen.activeCards;
            for (CardActor enemyOnBoard : enemyBoard.values()) {
                if (enemyOnBoard.getData().abilities != null) {
                    for (CardAbility ability : enemyOnBoard.getData().abilities) {
                        finalDefender[0] = ability.onTargeting(enemyOnBoard, attacker, finalDefender[0], screen);
                    }
                }
            }

            int baseAtk = getAtk(attacker.getData());
            if (baseAtk > 0) {
                float startY = attacker.getY();
                float attackY = isPlayerAttacking ? startY + 100 : startY - 100;

                attacker.toFront();

                attacker.addAction(Actions.sequence(
                    Actions.moveTo(attacker.getX(), attackY, 0.15f, Interpolation.exp10In),
                    Actions.run(() -> {

                        boolean shouldPlayMinigame = isPlayerAttacking || finalDefender[0] != null;

                        if (shouldPlayMinigame) {
                            final InjectionMiniGame[] miniGameRef = new InjectionMiniGame[1];
                            miniGameRef[0] = new InjectionMiniGame(baseAtk, finalDefender[0] != null, isPlayerAttacking, () -> {
                                int multiplier = miniGameRef[0].getFinalDamageMultiplier();
                                boolean gameOver = applyDamage(attacker, finalDefender[0], isPlayerAttacking, screen, lane, multiplier);
                                finishAttackAnimation(attacker, startY, screen, lanes, index, isPlayerAttacking, onComplete, gameOver);
                            });

                            screen.stage.addActor(miniGameRef[0]);
                            miniGameRef[0].toFront();
                        } else {
                            boolean gameOver = applyDamage(attacker, finalDefender[0], isPlayerAttacking, screen, lane, -1);
                            finishAttackAnimation(attacker, startY, screen, lanes, index, isPlayerAttacking, onComplete, gameOver);
                        }
                    })
                ));
            } else {
                processAttackForLane(screen, lanes, index + 1, isPlayerAttacking, onComplete);
            }
        } else {
            processAttackForLane(screen, lanes, index + 1, isPlayerAttacking, onComplete);
        }
    }

    private static void finishAttackAnimation(CardActor attacker, float startY, GameplayScreen screen, String[] lanes, int index, boolean isPlayerAttacking, Runnable onComplete, boolean gameOver) {
        if (attacker != null && attacker.getStage() != null) {
            attacker.addAction(Actions.moveTo(attacker.getX(), startY, 0.2f, Interpolation.pow2Out));
        }

        screen.stage.addAction(Actions.sequence(
            Actions.delay(0.4f),
            Actions.run(() -> {
                if (!gameOver && screen.phaseManager.currentPhase != GamePhaseManager.GamePhase.WIN && screen.phaseManager.currentPhase != GamePhaseManager.GamePhase.LOSE) {
                    processAttackForLane(screen, lanes, index + 1, isPlayerAttacking, onComplete);
                }
            })
        ));
    }

    private static boolean applyDamage(CardActor attacker, CardActor defender, boolean isPlayerAttacking, GameplayScreen screen, String lane, int multiplier) {

        float attackerCenterX = attacker.getX() + (attacker.getWidth() * attacker.getScaleX()) / 2f;
        float attackerTopY = attacker.getY() + (attacker.getHeight() * attacker.getScaleY());

        int baseAtk = getAtk(attacker.getData());
        int finalAtk = 0;
        boolean isGameOver = false;

        // LOGIKA MULTIPLIER DAMAGE
        if (isPlayerAttacking) {
            if (multiplier == 0) {
                spawnFloatingText(screen, attackerCenterX, attackerTopY, "MISS!", Color.GRAY, 2f, 50f);
                return false;
            } else if (multiplier == 1) {
                spawnFloatingText(screen, attackerCenterX, attackerTopY, "HIT!", Color.GREEN, 1.5f, 50f);
                finalAtk = baseAtk;
            } else if (multiplier == 2) {
                spawnFloatingText(screen, attackerCenterX, attackerTopY, "CRITICAL!", Color.CYAN, 2f, 70f);
                finalAtk = baseAtk * 2;
            }
        }
        else {
            if (multiplier == -1) {
                spawnFloatingText(screen, attackerCenterX, attackerTopY, "DIRECT!", Color.RED, 1.5f, 50f);
                finalAtk = baseAtk;
            }
            else if (multiplier == 0) {
                spawnFloatingText(screen, attackerCenterX, attackerTopY, "FAILED!", Color.RED, 2f, 50f);
                finalAtk = baseAtk;
            }
            else if (multiplier == 1) {
                spawnFloatingText(screen, attackerCenterX, attackerTopY, "BLOCKED!", Color.GRAY, 2f, 50f);
                Gdx.app.log("Combat", "Pemain berhasil mengeblok serangan!");
                return false;
            }
            else if (multiplier == 2) {
                spawnFloatingText(screen, attackerCenterX, attackerTopY, "COUNTER!", Color.CYAN, 2f, 70f);

                int enemyHp = getHp(attacker.getData());
                enemyHp -= baseAtk;
                setHp(attacker.getData(), enemyHp);

                spawnSlashEffect(screen, attackerCenterX, attacker.getY() + (attacker.getHeight()*attacker.getScaleY())/2f, baseAtk);

                attacker.addAction(Actions.sequence(
                    Actions.moveBy(15, 0, 0.05f), Actions.moveBy(-30, 0, 0.05f), Actions.moveBy(15, 0, 0.05f)
                ));

                if (enemyHp <= 0) {
                    if (attacker.getData().abilities != null) {
                        for (CardAbility ability : attacker.getData().abilities) {
                            ability.onDeath(attacker, screen);
                        }
                    }
                    attacker.addAction(Actions.sequence(
                        Actions.parallel(Actions.scaleTo(0, 0, 0.3f), Actions.fadeOut(0.3f)),
                        Actions.removeActor()
                    ));

                    // --- PERBAIKAN BUG HANTU UNTUK COUNTER ATTACK ---
                    // Cek apakah attacker masih attacker asli, bukan token
                    if (screen.enemyActiveCards.get(lane) == attacker) {
                        screen.enemyActiveCards.remove(lane);
                    }
                }
                return false;
            }
        }

        // EKSEKUSI DAMAGE KE DEFENDER
        if (defender != null) {
            int defHp = getHp(defender.getData());
            defHp -= finalAtk;
            setHp(defender.getData(), defHp);

            if (defHp < 0) { // Jika damage luber/berlebih
                boolean hasPierce = false;
                if (attacker.getData().abilities != null) {
                    for (CardAbility ability : attacker.getData().abilities) {
                        if (ability instanceof com.NCFrontend.logic.abilities.OversizedPacketAbility) hasPierce = true;
                    }
                }

                if (hasPierce) {
                    int pierceDamage = Math.abs(defHp);
                    Gdx.app.log("Combat", "OVERSIZED PACKET! Sisa " + pierceDamage + " Damage menembus ke sistem utama!");
                    if (isPlayerAttacking) {
                        screen.enemyProfile.takeDamage(pierceDamage);
                    } else {
                        screen.playerProfile.takeDamage(pierceDamage);
                    }
                    screen.uiManager.updateHP();
                    showRedFlash(screen);
                }
            }

            float defCenterX = defender.getX() + (defender.getWidth() * defender.getScaleX()) / 2f;
            float defCenterY = defender.getY() + (defender.getHeight() * defender.getScaleY()) / 2f;

            spawnSlashEffect(screen, defCenterX, defCenterY, finalAtk);

            defender.addAction(Actions.sequence(
                Actions.moveBy(15, 0, 0.05f), Actions.moveBy(-30, 0, 0.05f), Actions.moveBy(15, 0, 0.05f)
            ));

            if (defHp <= 0) {
                // Cari lane sebelum memicu onDeath
                String defenderLane = null;
                ObjectMap<String, CardActor> defBoard = isPlayerAttacking ? screen.enemyActiveCards : screen.activeCards;
                for (ObjectMap.Entry<String, CardActor> entry : defBoard.entries()) {
                    if (entry.value == defender) {
                        defenderLane = entry.key;
                        break;
                    }
                }

                // Picu Skill onDeath (Bisa melahirkan Token di sini)
                if (defender.getData().abilities != null) {
                    for (CardAbility ability : defender.getData().abilities) {
                        ability.onDeath(defender, screen);
                    }
                }

                for (CardActor ally : screen.activeCards.values()) {
                    if (ally != defender && ally.getData().abilities != null) {
                        for (CardAbility ability : ally.getData().abilities) ability.onAnyCardDeath(ally, defender, screen);
                    }
                }
                for (CardActor enemy : screen.enemyActiveCards.values()) {
                    if (enemy != defender && enemy.getData().abilities != null) {
                        for (CardAbility ability : enemy.getData().abilities) ability.onAnyCardDeath(enemy, defender, screen);
                    }
                }

                // Hancurkan gambar aslinya
                defender.addAction(Actions.sequence(
                    Actions.parallel(Actions.scaleTo(0, 0, 0.3f), Actions.fadeOut(0.3f)),
                    Actions.removeActor()
                ));

                // --- PERBAIKAN BUG HANTU ---
                // Hanya hapus dari logika jika isi board MASIH kartu yang mati tadi
                if (defenderLane != null && defBoard.get(defenderLane) == defender) {
                    defBoard.remove(defenderLane);
                }
            }
            return false;
        } else {
            // DIRECT ATTACK KE SYSTEM
            float targetY = isPlayerAttacking ? Gdx.graphics.getHeight() - 150f : 150f;

            spawnSlashEffect(screen, attackerCenterX, targetY, finalAtk);

            if (isPlayerAttacking) {
                screen.enemyProfile.takeDamage(finalAtk);
                if (screen.enemyProfile.hp <= 0) {
                    screen.phaseManager.triggerGameOver(true);
                    isGameOver = true;
                }
            } else {
                screen.playerProfile.takeDamage(finalAtk);
                if (screen.playerProfile.hp <= 0) {
                    screen.phaseManager.triggerGameOver(false);
                    isGameOver = true;
                }
            }

            if (!isPlayerAttacking) {
                screen.stage.getRoot().addAction(Actions.sequence(
                    Actions.moveBy(20, -20, 0.05f), Actions.moveBy(-40, 40, 0.05f), Actions.moveBy(20, -20, 0.05f), Actions.moveTo(0, 0, 0.05f)
                ));
                showRedFlash(screen);
            }

            screen.uiManager.updateHP();
            return isGameOver;
        }
    }
}
